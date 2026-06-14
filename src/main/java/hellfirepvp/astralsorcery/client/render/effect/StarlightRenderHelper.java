package hellfirepvp.astralsorcery.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

final class StarlightRenderHelper {
    private StarlightRenderHelper() {
    }

    static void renderQuad(PoseStack poseStack, VertexConsumer buffer, Vec3 a, Vec3 b, Vec3 c, Vec3 d, int alpha) {
        addVertex(poseStack, buffer, a, alpha);
        addVertex(poseStack, buffer, b, alpha);
        addVertex(poseStack, buffer, c, alpha);
        addVertex(poseStack, buffer, d, alpha);
    }

    static void renderGradientQuad(PoseStack poseStack, VertexConsumer buffer, Vec3 a, Vec3 b, Vec3 c, Vec3 d, int startAlpha, int endAlpha) {
        addVertex(poseStack, buffer, a, startAlpha);
        addVertex(poseStack, buffer, b, startAlpha);
        addVertex(poseStack, buffer, c, endAlpha);
        addVertex(poseStack, buffer, d, endAlpha);
        addVertex(poseStack, buffer, d, endAlpha);
        addVertex(poseStack, buffer, c, endAlpha);
        addVertex(poseStack, buffer, b, startAlpha);
        addVertex(poseStack, buffer, a, startAlpha);
    }

    static int clampAlpha(int alpha) {
        return Math.max(0, Math.min(255, alpha));
    }

    static double hashUnit(double input) {
        return Math.abs(Math.sin(input * 12.9898D) * 43758.5453D) % 1.0D;
    }

    private static void addVertex(PoseStack poseStack, VertexConsumer buffer, Vec3 pos, int alpha) {
        buffer.addVertex(poseStack.last(), new Vector3f((float) pos.x, (float) pos.y, (float) pos.z))
                .setColor(255, 255, 255, clampAlpha(alpha));
    }
}
