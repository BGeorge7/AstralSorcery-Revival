package hellfirepvp.astralsorcery.common.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Reusable helper for faint ambient starlight motes.
 *
 * <p>This intentionally stays lightweight and data-free. It is the shared place
 * for the small always-on particles around crystals and similar starlight
 * emitters. The visual design still needs a later pass to replace the current
 * vanilla placeholder particle with a more faithful Astral Sorcery mote.</p>
 */
public final class StarlightAmbientParticles {
    public static final Settings COLLECTOR_CRYSTAL = new Settings(ParticleTypes.END_ROD, 0.06F, 0.75D, 0.16D, 0.28D, 0.65D);

    private StarlightAmbientParticles() {
    }

    /**
     * Spawns a subtle orbiting mote around a block-space origin.
     *
     * @param level client level receiving the particle
     * @param pos block position of the effect owner
     * @param settings particle type and spawn volume
     */
    public static void tick(Level level, BlockPos pos, Settings settings) {
        RandomSource random = level.random;
        if (random.nextFloat() >= settings.chancePerTick()) {
            return;
        }

        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + settings.centerYOffset();
        double centerZ = pos.getZ() + 0.5D;
        double angle = random.nextDouble() * Math.PI * 2.0D;
        double radius = settings.minRadius() + random.nextDouble() * settings.randomRadius();
        double x = centerX + Math.cos(angle) * radius;
        double y = centerY + random.nextDouble() * settings.randomYOffset();
        double z = centerZ + Math.sin(angle) * radius;
        level.addParticle(settings.particle(), x, y, z, (centerX - x) * 0.002D, 0.001D + random.nextDouble() * 0.003D, (centerZ - z) * 0.002D);
    }

    /**
     * Particle type, spawn chance, and spawn volume for ambient starlight motes.
     */
    public record Settings(ParticleOptions particle, float chancePerTick, double centerYOffset, double minRadius, double randomRadius, double randomYOffset) {
    }
}
