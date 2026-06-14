package hellfirepvp.astralsorcery.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

/**
 * Reusable renderer for focused starlight beams between two points.
 *
 * <p>This is separate from {@link StarlightGodRays}: god rays are ambient,
 * fan-like wedges, while directed beams represent an active connection such as
 * a collector crystal converting a nearby crafting table.</p>
 */
public final class StarlightDirectedBeam {
    /**
     * Current placeholder/tuning for collector crystal table conversion.
     */
    public static final Settings TABLE_TRANSMUTATION = new Settings(0.11D, 230, 0.24D, 72);

    private StarlightDirectedBeam() {
    }

    /**
     * Renders a straight beam as two crossed planes.
     *
     * @param poseStack active render pose stack
     * @param buffer vertex buffer using {@link StarlightRenderTypes#starlightRays()}
     * @param start local-space start point
     * @param end local-space end point
     * @param settings widths and alpha values for the bright core and soft halo
     */
    public static void render(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end, Settings settings) {
        renderBeamPlane(poseStack, buffer, start, end, settings.coreWidth(), settings.coreAlpha());
        renderBeamPlane(poseStack, buffer, start, end, settings.haloWidth(), settings.haloAlpha());
    }

    private static void renderBeamPlane(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end, double width, int alpha) {
        Vec3 direction = end.subtract(start);
        if (direction.lengthSqr() < 1.0E-4D) {
            return;
        }
        Vec3 side = direction.normalize().cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (side.lengthSqr() < 1.0E-4D) {
            side = new Vec3(1.0D, 0.0D, 0.0D);
        }
        side = side.normalize().scale(width);
        Vec3 vertical = direction.normalize().cross(side).normalize().scale(width);

        StarlightRenderHelper.renderQuad(poseStack, buffer, start.add(side), start.subtract(side), end.subtract(side), end.add(side), alpha);
        StarlightRenderHelper.renderQuad(poseStack, buffer, start.add(vertical), start.subtract(vertical), end.subtract(vertical), end.add(vertical), alpha);
    }

    /**
     * Width and opacity controls for a directed starlight connection.
     */
    public record Settings(double coreWidth, int coreAlpha, double haloWidth, int haloAlpha) {
    }
}
