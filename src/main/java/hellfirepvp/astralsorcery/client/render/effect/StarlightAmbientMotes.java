package hellfirepvp.astralsorcery.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

/**
 * Reusable camera-facing starlight mote renderer.
 *
 * <p>Legacy Astral Sorcery used {@code EffectTemplatesAS.GENERIC_PARTICLE} for
 * these little sparkles, which resolves to the {@code particle_small.png}
 * texture. This modern renderer keeps the same idea but renders deterministic
 * local-space sprites from an effect origin, so block entity renderers can use
 * it without creating transient vanilla particles every tick.</p>
 */
public final class StarlightAmbientMotes {
    /**
     * Faint always-on collector crystal sparkles.
     */
    public static final Settings COLLECTOR_CRYSTAL = new Settings(
            10,
            0.76D,
            0.58D,
            0.16D,
            0.12D,
            42,
            142,
            42.0D,
            34.0D,
            0.018D);

    private StarlightAmbientMotes() {
    }

    /**
     * Renders small sprite motes around an origin.
     *
     * <p>Motes do not orbit. Each mote picks a seeded random position for its
     * current lifetime, blooms in place, and fades out before choosing another
     * seeded position. This matches the old collector crystal sparkle read more
     * closely than a continuous orbital motion.</p>
     *
     * @param poseStack active render pose stack
     * @param buffer vertex buffer using {@link StarlightRenderTypes#starlightParticles()}
     * @param origin local-space center of the effect, usually the crystal center
     * @param time world game time plus partial tick
     * @param settings count, radius, size, alpha, and motion tuning
     */
    public static void render(PoseStack poseStack, VertexConsumer buffer, Vec3 origin, double time, Settings settings) {
        for (int i = 0; i < settings.moteCount(); i++) {
            double seed = StarlightRenderHelper.hashUnit(i * 41.0D + 7.0D);
            double cycleLength = settings.minLifetimeTicks() + seed * settings.randomLifetimeTicks();
            double shiftedTime = time + seed * cycleLength;
            double cycle = Math.floor(shiftedTime / cycleLength);
            double life = (shiftedTime % cycleLength) / cycleLength;

            double positionSeed = i * 131.0D + cycle * 17.0D;
            double angle = StarlightRenderHelper.hashUnit(positionSeed + 3.0D) * Math.PI * 2.0D;
            double radius = settings.radius() * (0.2D + StarlightRenderHelper.hashUnit(positionSeed + 11.0D) * 0.8D);
            double vertical = (StarlightRenderHelper.hashUnit(positionSeed + 23.0D) - 0.5D) * settings.verticalRange();
            double jitter = Math.sin(time * 0.045D + positionSeed) * settings.jitterRadius();
            Vec3 center = origin.add(
                    Math.cos(angle) * (radius + jitter),
                    vertical + Math.sin(time * 0.035D + positionSeed * 0.37D) * settings.jitterRadius(),
                    Math.sin(angle) * (radius + jitter));

            double bloom = pyramidFade(life);
            if (bloom <= 0.01D) {
                continue;
            }
            double twinkle = 0.82D + Math.sin(time * 0.12D + positionSeed * 0.71D) * 0.18D;
            int alpha = (int) Math.round((settings.minAlpha() + settings.randomAlpha() * bloom) * twinkle);
            double sizeSeed = StarlightRenderHelper.hashUnit(positionSeed + 47.0D);
            double size = settings.minSize() + sizeSeed * settings.randomSize();
            double rotation = StarlightRenderHelper.hashUnit(positionSeed + 61.0D) * Math.PI * 2.0D;
            StarlightRenderHelper.renderFacingSprite(poseStack, buffer, center, size * 2.45D, rotation + 0.31D, Math.max(1, alpha / 12));
            StarlightRenderHelper.renderFacingSprite(poseStack, buffer, center, size * 1.9D, rotation + 0.83D, Math.max(1, alpha / 7));
            StarlightRenderHelper.renderFacingSprite(poseStack, buffer, center, size * 1.38D, rotation + 1.57D, Math.max(1, alpha / 4));
            StarlightRenderHelper.renderFacingSprite(poseStack, buffer, center, size, rotation, alpha);
        }
    }

    private static double pyramidFade(double life) {
        double fadeIn = Math.min(1.0D, life / 0.28D);
        double fadeOut = Math.min(1.0D, (1.0D - life) / 0.36D);
        return Math.max(0.0D, Math.min(fadeIn, fadeOut));
    }

    /**
     * Immutable tuning for ambient sprite motes.
     */
    public record Settings(
            int moteCount,
            double radius,
            double verticalRange,
            double minSize,
            double randomSize,
            int minAlpha,
            int randomAlpha,
            double minLifetimeTicks,
            double randomLifetimeTicks,
            double jitterRadius) {
    }
}
