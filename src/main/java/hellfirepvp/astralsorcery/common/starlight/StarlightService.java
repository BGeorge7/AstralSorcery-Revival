package hellfirepvp.astralsorcery.common.starlight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class StarlightService {
    private StarlightService() {
    }

    public static float getAmbientStarlight(Level level, BlockPos pos) {
        if (!level.dimensionType().hasSkyLight() || !level.canSeeSky(pos.above())) {
            return 0.0F;
        }

        long dayTime = level.getDayTime() % 24000L;
        float nightFactor;
        if (dayTime >= 13000L && dayTime <= 23000L) {
            long distanceFromMidnight = Math.abs(18000L - dayTime);
            nightFactor = 1.0F - (distanceFromMidnight / 5000.0F);
        } else {
            nightFactor = 0.0F;
        }

        float altitudeFactor = Math.min(1.0F, Math.max(0.15F, (pos.getY() + 64.0F) / 384.0F));
        return Math.max(0.0F, nightFactor * altitudeFactor);
    }
}
