package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.registry.ASBlockEntityTypes;
import hellfirepvp.astralsorcery.common.starlight.StarlightService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DiscoveryAltarBlockEntity extends BlockEntity {
    private float lastAmbientStarlight;

    public DiscoveryAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ASBlockEntityTypes.DISCOVERY_ALTAR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DiscoveryAltarBlockEntity altar) {
        if (level.getGameTime() % 20L != 0L) {
            return;
        }
        float starlight = StarlightService.getAmbientStarlight(level, pos);
        if (Float.compare(starlight, altar.lastAmbientStarlight) != 0) {
            altar.lastAmbientStarlight = starlight;
            altar.setChanged();
        }
    }

    public float getLastAmbientStarlight() {
        return lastAmbientStarlight;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        lastAmbientStarlight = tag.getFloat("last_ambient_starlight");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("last_ambient_starlight", lastAmbientStarlight);
    }
}
