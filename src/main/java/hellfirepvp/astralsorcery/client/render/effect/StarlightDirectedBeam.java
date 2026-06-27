package hellfirepvp.astralsorcery.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

/**
 * Reusable renderer for focused starlight beams between two points.
 *
 * <p>This is separate from {@link StarlightGodRays}: god rays are ambient,
 * fan-like wedges, while directed beams represent an active connection such as
 * a collector crystal converting a nearby crafting table. The legacy table
 * conversion beam starts brightest at the crystal, narrows and fades toward the
 * target, pulses slowly, and carries small star-like motes along its edges.</p>
 */
public final class StarlightDirectedBeam {
    private static final int LIGHTBEAM_ROWS = 4;
    private static final int LIGHTBEAM_COLUMNS = 16;
    private static final int LIGHTBEAM_FRAMES = LIGHTBEAM_ROWS * LIGHTBEAM_COLUMNS;

    /**
     * Tuned preset for collector crystal table conversion.
     */
    public static final Settings TABLE_TRANSMUTATION = new Settings(
            0.2D,
            0.16D,
            255,
            96,
            0.55D,
            0.08D,
            0.045D,
            92,
            22,
            0.012D,
            0.045D,
            1.0D);

    private StarlightDirectedBeam() {
    }

    /**
     * Renders a tapered, pulsing straight beam.
     *
     * @param poseStack active render pose stack
     * @param beamBuffer vertex buffer using {@link StarlightRenderTypes#starlightBeamTransfer()}
     * @param start local-space start point
     * @param end local-space end point
     * @param time world game time plus partial tick
     * @param settings widths, opacity, pulse, and sparkle tuning
     */
    public static void renderBeam(PoseStack poseStack, VertexConsumer beamBuffer, Vec3 start, Vec3 end, double time, Settings settings) {
        Vec3 direction = end.subtract(start);
        if (direction.lengthSqr() < 1.0E-4D) {
            return;
        }

        Vec3 normal = direction.normalize();
        Vec3 drawEnd = end.add(normal.scale(settings.endExtension()));
        Vec3 side = normal.cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (side.lengthSqr() < 1.0E-4D) {
            side = new Vec3(1.0D, 0.0D, 0.0D);
        }
        side = side.normalize();
        Vec3 vertical = normal.cross(side).normalize();

        double pulse = 0.78D + (Math.sin(time * settings.pulseSpeed()) * 0.22D);
        int startAlpha = (int) Math.round(settings.startAlpha() * pulse);
        int endAlpha = (int) Math.round(settings.endAlpha() * pulse);

        int frame = Math.floorMod((int) Math.floor(time * settings.textureFrameSpeed()), LIGHTBEAM_FRAMES);
        float u0 = (frame % LIGHTBEAM_COLUMNS) / (float) LIGHTBEAM_COLUMNS;
        float v0 = (frame / LIGHTBEAM_COLUMNS) / (float) LIGHTBEAM_ROWS;
        float u1 = u0 + (1.0F / LIGHTBEAM_COLUMNS);
        float v1 = v0 + (1.0F / LIGHTBEAM_ROWS);

        renderTexturedPlane(poseStack, beamBuffer, start, drawEnd, side, settings.startWidth(), settings.endWidth(), u0, v0, u1, v1, startAlpha, endAlpha);
        renderTexturedPlane(poseStack, beamBuffer, start, drawEnd, rotateAroundAxis(side, normal, Math.toRadians(120.0D)), settings.startWidth() * 0.86D, settings.endWidth() * 0.86D, u0, v0, u1, v1, startAlpha / 2, endAlpha / 2);
        renderTexturedPlane(poseStack, beamBuffer, start, drawEnd, rotateAroundAxis(side, normal, Math.toRadians(240.0D)), settings.startWidth() * 0.86D, settings.endWidth() * 0.86D, u0, v0, u1, v1, startAlpha / 2, endAlpha / 2);
        renderTexturedPlane(poseStack, beamBuffer, start, drawEnd, side, settings.startWidth() * 2.7D, settings.endWidth() * 2.4D, u0, v0, u1, v1, startAlpha / 3, endAlpha / 3);
        renderTexturedPlane(poseStack, beamBuffer, start, drawEnd, vertical, settings.startWidth() * 2.2D, settings.endWidth() * 2.0D, u0, v0, u1, v1, startAlpha / 4, endAlpha / 4);
    }

    /**
     * Renders the small flanking motes that travel along a focused beam.
     *
     * @param poseStack active render pose stack
     * @param sparkleBuffer vertex buffer using {@link StarlightRenderTypes#starlightParticles()}
     * @param start local-space start point
     * @param end local-space end point
     * @param time world game time plus partial tick
     * @param settings sparkle tuning shared with the main beam
     */
    public static void renderSparkles(PoseStack poseStack, VertexConsumer sparkleBuffer, Vec3 start, Vec3 end, double time, Settings settings) {
        Vec3 direction = end.subtract(start);
        if (direction.lengthSqr() < 1.0E-4D) {
            return;
        }

        Vec3 normal = direction.normalize();
        Vec3 side = normal.cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (side.lengthSqr() < 1.0E-4D) {
            side = new Vec3(1.0D, 0.0D, 0.0D);
        }
        side = side.normalize();
        Vec3 vertical = normal.cross(side).normalize();
        double pulse = 0.78D + (Math.sin(time * settings.pulseSpeed()) * 0.22D);
        renderSparklesAlongPath(poseStack, sparkleBuffer, start, direction, side, vertical, time, settings, pulse);
    }

    private static void renderTexturedPlane(
            PoseStack poseStack,
            VertexConsumer buffer,
            Vec3 start,
            Vec3 end,
            Vec3 side,
            double startWidth,
            double endWidth,
            float u0,
            float v0,
            float u1,
            float v1,
            int startAlpha,
            int endAlpha) {
        Vec3 startSide = side.scale(startWidth);
        Vec3 endSide = side.scale(endWidth);
        StarlightRenderHelper.renderTexturedGradientQuad(
                poseStack,
                buffer,
                start.add(startSide),
                start.subtract(startSide),
                end.subtract(endSide),
                end.add(endSide),
                u0,
                v0,
                u1,
                v1,
                startAlpha,
                endAlpha);
    }

    private static Vec3 rotateAroundAxis(Vec3 vector, Vec3 axis, double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        Vec3 parallel = axis.scale(vector.dot(axis));
        Vec3 perpendicular = vector.subtract(parallel);
        Vec3 w = axis.cross(vector);
        return parallel.add(perpendicular.scale(cos)).add(w.scale(sin)).normalize();
    }

    private static void renderSparklesAlongPath(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 direction, Vec3 side, Vec3 vertical, double time, Settings settings, double pulse) {
        for (int i = 0; i < settings.sparkleCount(); i++) {
            double seed = StarlightRenderHelper.hashUnit(i * 17.0D + 5.0D);
            double seed2 = StarlightRenderHelper.hashUnit(i * 29.0D + 13.0D);
            double seed3 = StarlightRenderHelper.hashUnit(i * 43.0D + 23.0D);
            double path = (seed + time * settings.sparkleDriftSpeed()) % 1.0D;
            double sideSign = i % 2 == 0 ? 1.0D : -1.0D;
            double pathWidth = settings.startWidth() + (settings.endWidth() - settings.startWidth()) * path;
            double sideOffset = sideSign * (pathWidth * 0.35D + seed2 * settings.sparkleFlankOffset());
            double verticalOffset = (seed3 - 0.5D) * settings.sparkleFlankOffset();
            Vec3 center = start.add(direction.scale(path))
                    .add(side.scale(sideOffset))
                    .add(vertical.scale(verticalOffset));
            double lifeAlpha = Math.sin(path * Math.PI);
            int alpha = (int) Math.round((settings.sparkleAlpha() * pulse * lifeAlpha) * (0.55D + seed2 * 0.45D));
            double size = settings.sparkleSize() * (0.75D + seed3 * 0.55D);
            StarlightRenderHelper.renderFacingSprite(poseStack, buffer, center, size, alpha);
        }
    }

    /**
     * Width, opacity, pulse, and sparkle controls for a directed starlight connection.
     */
    public record Settings(
            double startWidth,
            double endWidth,
            int startAlpha,
            int endAlpha,
            double endExtension,
            double pulseSpeed,
            double sparkleSize,
            int sparkleAlpha,
            int sparkleCount,
            double sparkleDriftSpeed,
            double sparkleFlankOffset,
            double textureFrameSpeed) {
    }
}
