package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.data.ShrineCrystalProperties;
import hellfirepvp.astralsorcery.common.registry.ASBlockEntityTypes;
import hellfirepvp.astralsorcery.common.starlight.StarlightService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Locale;

public class CollectorCrystalBlockEntity extends BlockEntity {
    private static final List<String> MAJOR_CONSTELLATIONS = List.of("aevitas", "discidia", "armara", "vicio", "evorsio");

    private ShrineCrystalProperties properties = ShrineCrystalProperties.worldgenCollector();
    private String constellation = "";
    private boolean playerMade;
    private float lastCollectedStarlight;

    public CollectorCrystalBlockEntity(BlockPos pos, BlockState blockState) {
        super(ASBlockEntityTypes.COLLECTOR_CRYSTAL.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CollectorCrystalBlockEntity crystal) {
        if (level.getGameTime() % 20L != 0L) {
            return;
        }
        float collected = StarlightService.getAmbientStarlight(level, pos) * crystal.properties.collectionMultiplier();
        if (Float.compare(collected, crystal.lastCollectedStarlight) != 0) {
            crystal.lastCollectedStarlight = collected;
            crystal.setChanged();
        }
    }

    public void initializeWorldgen(RandomSource random) {
        this.playerMade = false;
        this.properties = ShrineCrystalProperties.worldgenCollector();
        this.constellation = MAJOR_CONSTELLATIONS.get(random.nextInt(MAJOR_CONSTELLATIONS.size()));
        setChanged();
    }

    public void initializePlayerMade() {
        this.playerMade = true;
        this.properties = ShrineCrystalProperties.worldgenCollector();
        setChanged();
    }

    public boolean isPlayerMade() {
        return playerMade;
    }

    public float getLastCollectedStarlight() {
        return lastCollectedStarlight;
    }

    public String getConstellationDisplayName() {
        if (constellation == null || constellation.isBlank()) {
            return "unattuned";
        }
        return constellation.substring(0, 1).toUpperCase(Locale.ROOT) + constellation.substring(1);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.properties = ShrineCrystalProperties.load(tag.getCompound("crystal_properties"));
        this.constellation = tag.getString("constellation");
        this.playerMade = tag.getBoolean("player_made");
        this.lastCollectedStarlight = tag.getFloat("last_collected_starlight");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("crystal_properties", properties.save());
        tag.putString("constellation", constellation == null ? "" : constellation);
        tag.putBoolean("player_made", playerMade);
        tag.putFloat("last_collected_starlight", lastCollectedStarlight);
    }
}
