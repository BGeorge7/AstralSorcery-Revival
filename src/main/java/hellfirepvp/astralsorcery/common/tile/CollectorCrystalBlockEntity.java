package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.data.CrystalAttributeSet;
import hellfirepvp.astralsorcery.common.registry.ASBlockEntityTypes;
import hellfirepvp.astralsorcery.common.registry.ASBlocks;
import hellfirepvp.astralsorcery.common.registry.ASSounds;
import hellfirepvp.astralsorcery.common.starlight.StarlightService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CollectorCrystalBlockEntity extends BlockEntity {
    private static final List<String> MAJOR_CONSTELLATIONS = List.of("aevitas", "discidia", "armara", "vicio", "evorsio");
    private static final int TRANSMUTATION_RADIUS = 8;
    private static final float CRAFTING_TABLE_TRANSMUTATION_COST = 60.0F;
    private static final float TRANSMUTATION_TICK_SCALE = 0.20F;
    private static final long TRANSMUTATION_STALE_TICKS = 300L;

    private CrystalAttributeSet attributes = CrystalAttributeSet.worldgenShrineCollector();
    private String constellation = "";
    private String constellationTrait = "";
    private String collectorType = "ROCK_CRYSTAL";
    private boolean playerMade;
    private float lastCollectedStarlight;
    private boolean doesSeeSky;
    private boolean lastSyncedDoesSeeSky;
    @Nullable
    private BlockPos activeTransmutationTarget;
    private float activeTransmutationProgress;
    @Nullable
    private BlockPos lastTransmutationBurstTarget;
    private long lastTransmutationBurstGameTime = Long.MIN_VALUE;
    private final Map<BlockPos, ActiveTransmutation> activeTransmutations = new HashMap<>();

    public CollectorCrystalBlockEntity(BlockPos pos, BlockState blockState) {
        super(ASBlockEntityTypes.COLLECTOR_CRYSTAL.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CollectorCrystalBlockEntity crystal) {
        if (level.isClientSide()) {
            return;
        }

        crystal.doesSeeSky = level.dimensionType().hasSkyLight() && level.canSeeSky(pos.above());
        float collected = StarlightService.getAmbientStarlight(level, pos) * crystal.attributes.collectorMultiplier();
        if (level.getGameTime() % 10L == 0L && Float.compare(collected, crystal.lastCollectedStarlight) != 0) {
            crystal.lastCollectedStarlight = collected;
            crystal.setChanged();
        }
        if (crystal.doesSeeSky != crystal.lastSyncedDoesSeeSky) {
            crystal.lastSyncedDoesSeeSky = crystal.doesSeeSky;
            crystal.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
        crystal.tickCraftingTableTransmutation(level, pos, collected * TRANSMUTATION_TICK_SCALE);
    }

    public void initializeWorldgen(RandomSource random) {
        this.playerMade = false;
        this.attributes = CrystalAttributeSet.worldgenShrineCollector();
        this.constellation = MAJOR_CONSTELLATIONS.get(random.nextInt(MAJOR_CONSTELLATIONS.size()));
        this.constellationTrait = "";
        this.collectorType = "ROCK_CRYSTAL";
        setChanged();
    }

    public void initializePlayerMade() {
        this.playerMade = true;
        this.attributes = CrystalAttributeSet.worldgenShrineCollector();
        setChanged();
    }

    public boolean isPlayerMade() {
        return playerMade;
    }

    public float getLastCollectedStarlight() {
        return lastCollectedStarlight;
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    @Nullable
    public BlockPos getActiveTransmutationTarget() {
        return activeTransmutationTarget;
    }

    public float getActiveTransmutationProgress() {
        return activeTransmutationProgress;
    }

    @Nullable
    public BlockPos getLastTransmutationBurstTarget() {
        return lastTransmutationBurstTarget;
    }

    public long getLastTransmutationBurstGameTime() {
        return lastTransmutationBurstGameTime;
    }

    public String getAttributeSummary() {
        return attributes.summary();
    }

    public String getConstellationDisplayName() {
        if (constellation == null || constellation.isBlank()) {
            return "unattuned";
        }
        return constellation.substring(0, 1).toUpperCase(Locale.ROOT) + constellation.substring(1);
    }

    private void tickCraftingTableTransmutation(Level level, BlockPos crystalPos, float collected) {
        long gameTime = level.getGameTime();
        activeTransmutations.entrySet().removeIf(entry -> gameTime - entry.getValue().lastReceivedGameTime > TRANSMUTATION_STALE_TICKS
                || !level.getBlockState(entry.getKey()).is(Blocks.CRAFTING_TABLE));
        BlockPos nearest = findNearestCraftingTable(level, crystalPos);
        if (nearest == null) {
            updateTransmutationVisual(level, crystalPos, null, 0.0F);
            return;
        }

        ActiveTransmutation active = activeTransmutations.computeIfAbsent(nearest, ignored -> new ActiveTransmutation());
        if (collected <= 0.0F) {
            updateTransmutationVisual(level, crystalPos, nearest, active.progress / CRAFTING_TABLE_TRANSMUTATION_COST);
            return;
        }

        active.progress += collected;
        active.lastReceivedGameTime = gameTime;
        updateTransmutationVisual(level, crystalPos, nearest, active.progress / CRAFTING_TABLE_TRANSMUTATION_COST);
        if (active.progress >= CRAFTING_TABLE_TRANSMUTATION_COST) {
            level.setBlockAndUpdate(nearest, ASBlocks.ALTAR_DISCOVERY.get().defaultBlockState());
            level.playSound(null, nearest, ASSounds.CRAFT_FINISH.get(), SoundSource.BLOCKS, 0.8F, 1.2F);
            activeTransmutations.remove(nearest);
            lastTransmutationBurstTarget = nearest;
            lastTransmutationBurstGameTime = gameTime;
            updateTransmutationVisual(level, crystalPos, null, 0.0F);
        }
    }

    private void updateTransmutationVisual(Level level, BlockPos crystalPos, @Nullable BlockPos target, float progress) {
        float clampedProgress = Math.max(0.0F, Math.min(1.0F, progress));
        if ((target == null ? activeTransmutationTarget == null : target.equals(activeTransmutationTarget))
                && Math.abs(activeTransmutationProgress - clampedProgress) < 0.05F) {
            return;
        }

        activeTransmutationTarget = target;
        activeTransmutationProgress = clampedProgress;
        setChanged();
        level.sendBlockUpdated(crystalPos, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    private BlockPos findNearestCraftingTable(Level level, BlockPos crystalPos) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = crystalPos.getX() - TRANSMUTATION_RADIUS; x <= crystalPos.getX() + TRANSMUTATION_RADIUS; x++) {
            for (int y = crystalPos.getY() - TRANSMUTATION_RADIUS; y <= crystalPos.getY() + TRANSMUTATION_RADIUS; y++) {
                for (int z = crystalPos.getZ() - TRANSMUTATION_RADIUS; z <= crystalPos.getZ() + TRANSMUTATION_RADIUS; z++) {
                    cursor.set(x, y, z);
                    if (!level.isLoaded(cursor) || !level.getBlockState(cursor).is(Blocks.CRAFTING_TABLE)) {
                        continue;
                    }
                    if (!hasLineOfSightToTable(level, crystalPos, cursor)) {
                        continue;
                    }
                    double distance = cursor.distSqr(crystalPos);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = cursor.immutable();
                    }
                }
            }
        }
        return nearest;
    }

    private boolean hasLineOfSightToTable(Level level, BlockPos crystalPos, BlockPos tablePos) {
        Vec3 crystalCenter = Vec3.atCenterOf(crystalPos).add(0.0D, 0.25D, 0.0D);
        Vec3 end = Vec3.atCenterOf(tablePos).add(0.0D, 0.35D, 0.0D);
        Vec3 direction = end.subtract(crystalCenter);
        Vec3 start = direction.lengthSqr() < 1.0E-4D ? crystalCenter : crystalCenter.add(direction.normalize().scale(0.55D));
        BlockHitResult result = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        return result.getType() == HitResult.Type.MISS || result.getBlockPos().equals(tablePos);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.attributes = CrystalAttributeSet.load(tag);
        this.constellation = tag.getString("constellation");
        this.constellationTrait = tag.getString("constellationTrait");
        this.collectorType = tag.getString("collectorType");
        if (this.collectorType.isBlank()) {
            this.collectorType = "ROCK_CRYSTAL";
        }
        this.playerMade = tag.getBoolean("player_made");
        this.lastCollectedStarlight = tag.getFloat("last_collected_starlight");
        this.doesSeeSky = tag.getBoolean("doesSeeSky");
        if (tag.contains("activeTransmutationTarget")) {
            this.activeTransmutationTarget = BlockPos.of(tag.getLong("activeTransmutationTarget"));
            this.activeTransmutationProgress = tag.getFloat("activeTransmutationProgress");
        } else {
            this.activeTransmutationTarget = null;
            this.activeTransmutationProgress = 0.0F;
        }
        if (tag.contains("lastTransmutationBurstTarget")) {
            this.lastTransmutationBurstTarget = BlockPos.of(tag.getLong("lastTransmutationBurstTarget"));
            this.lastTransmutationBurstGameTime = tag.getLong("lastTransmutationBurstGameTime");
        } else {
            this.lastTransmutationBurstTarget = null;
            this.lastTransmutationBurstGameTime = Long.MIN_VALUE;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.merge(attributes.saveIntoBlockEntityTag());
        tag.putString("constellation", constellation == null ? "" : constellation);
        tag.putString("constellationType", constellation == null ? "" : constellation);
        tag.putString("constellationTrait", constellationTrait == null ? "" : constellationTrait);
        tag.putString("collectorType", collectorType == null || collectorType.isBlank() ? "ROCK_CRYSTAL" : collectorType);
        tag.putBoolean("player_made", playerMade);
        tag.putFloat("last_collected_starlight", lastCollectedStarlight);
        tag.putBoolean("doesSeeSky", doesSeeSky);
        writeVisualState(tag);
    }

    private void writeVisualState(CompoundTag tag) {
        if (activeTransmutationTarget != null) {
            tag.putLong("activeTransmutationTarget", activeTransmutationTarget.asLong());
            tag.putFloat("activeTransmutationProgress", activeTransmutationProgress);
        }
        if (lastTransmutationBurstTarget != null) {
            tag.putLong("lastTransmutationBurstTarget", lastTransmutationBurstTarget.asLong());
            tag.putLong("lastTransmutationBurstGameTime", lastTransmutationBurstGameTime);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private static class ActiveTransmutation {
        private float progress;
        private long lastReceivedGameTime;
    }
}
