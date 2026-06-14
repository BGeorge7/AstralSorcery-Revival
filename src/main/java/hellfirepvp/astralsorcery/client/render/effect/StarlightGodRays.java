package hellfirepvp.astralsorcery.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

/**
 * Reusable Astral Sorcery "god ray" renderer.
 *
 * <p>The legacy effect is a set of flat triangular light planes. Each plane has
 * two straight edges that meet at the effect origin and diverge at a fixed rate
 * until the ray tip. The alpha fades in from the origin and fades out again at
 * the tip, but the silhouette itself stays a simple diverging wedge.</p>
 */
public final class StarlightGodRays {
    /**
     * Tuned collector-crystal preset based on the legacy shrine crystal look.
     */
    public static final Settings COLLECTOR_CRYSTAL = new Settings(
            20,
            1.05D,
            1.1D,
            0.24D,
            2.25D,
            0.75D,
            0.85D,
            0.15D,
            0.12D,
            42,
            18,
            0.007D,
            0.014D);

    private StarlightGodRays() {
    }

    /**
     * Renders a set of animated starlight god rays around an origin.
     *
     * @param poseStack active render pose stack
     * @param buffer vertex buffer using {@link StarlightRenderTypes#starlightRays()}
     * @param origin local-space point where all ray edges meet
     * @param time world game time plus partial tick
     * @param settings visual tuning for count, size, alpha, and motion
     */
    public static void render(PoseStack poseStack, VertexConsumer buffer, Vec3 origin, double time, Settings settings) {
        for (int i = 0; i < settings.rayCount(); i++) {
            double seed = StarlightRenderHelper.hashUnit(i * 37.0D + 11.0D);
            double seed2 = StarlightRenderHelper.hashUnit(i * 53.0D + 19.0D);
            double seed3 = StarlightRenderHelper.hashUnit(i * 79.0D + 31.0D);
            double spinSpeed = (settings.baseSpinSpeed() + seed * settings.randomSpinSpeed()) * (i % 2 == 0 ? 1.0D : -1.0D);
            double angle = (i * Math.PI * 2.0D / settings.rayCount())
                    + time * spinSpeed
                    + Math.sin(time * (0.003D + seed2 * 0.004D) + i * 0.73D) * 0.42D;
            double horizontalLength = settings.minHorizontalLength()
                    + seed2 * settings.randomHorizontalLength()
                    + Math.sin(time * (0.005D + seed * 0.007D) + i * 0.91D) * settings.lengthWobble();
            double verticalBias = (seed3 - 0.5D) * settings.verticalBiasRange();
            double verticalDrift = Math.sin(time * (0.004D + seed3 * 0.006D) + i * 1.43D)
                    * (settings.minVerticalDrift() + seed * settings.randomVerticalDrift());
            Vec3 end = origin.add(Math.cos(angle) * horizontalLength, verticalBias + verticalDrift, Math.sin(angle) * horizontalLength);
            renderDivergingRay(poseStack, buffer, origin, end, settings.minEndWidth() + seed3 * settings.randomEndWidth(), settings.baseAlpha() + (int) (seed * settings.randomAlpha()));
        }
    }

    private static void renderDivergingRay(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end, double endWidth, int alpha) {
        Vec3 direction = end.subtract(start);
        if (direction.lengthSqr() < 1.0E-4D) {
            return;
        }
        Vec3 side = direction.normalize().cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (side.lengthSqr() < 1.0E-4D) {
            side = new Vec3(1.0D, 0.0D, 0.0D);
        }
        side = side.normalize().scale(endWidth);
        renderDivergingSegment(poseStack, buffer, start, direction, side, 0.00D, 0.38D, 0, alpha);
        renderDivergingSegment(poseStack, buffer, start, direction, side, 0.38D, 0.76D, alpha, Math.max(12, alpha * 3 / 4));
        renderDivergingSegment(poseStack, buffer, start, direction, side, 0.76D, 1.00D, Math.max(12, alpha * 3 / 4), 0);
    }

    private static void renderDivergingSegment(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 direction, Vec3 endSide, double from, double to, int fromAlpha, int toAlpha) {
        Vec3 segmentStart = start.add(direction.scale(from));
        Vec3 segmentEnd = start.add(direction.scale(to));
        Vec3 sideStart = endSide.scale(from);
        Vec3 sideEnd = endSide.scale(to);
        StarlightRenderHelper.renderGradientQuad(
                poseStack,
                buffer,
                segmentStart.subtract(sideStart),
                segmentStart.add(sideStart),
                segmentEnd.add(sideEnd),
                segmentEnd.subtract(sideEnd),
                fromAlpha,
                toAlpha);
    }

    /**
     * Immutable tuning values for god ray count, size, opacity, and motion.
     */
    public record Settings(
            int rayCount,
            double minHorizontalLength,
            double randomHorizontalLength,
            double lengthWobble,
            double verticalBiasRange,
            double minVerticalDrift,
            double randomVerticalDrift,
            double minEndWidth,
            double randomEndWidth,
            int baseAlpha,
            int randomAlpha,
            double baseSpinSpeed,
            double randomSpinSpeed) {
    }
}
