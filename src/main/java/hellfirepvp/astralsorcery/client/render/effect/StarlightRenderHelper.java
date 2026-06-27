package hellfirepvp.astralsorcery.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
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

    static void renderTexturedGradientQuad(
            PoseStack poseStack,
            VertexConsumer buffer,
            Vec3 startLeft,
            Vec3 startRight,
            Vec3 endRight,
            Vec3 endLeft,
            float u0,
            float v0,
            float u1,
            float v1,
            int startAlpha,
            int endAlpha) {
        addTexturedVertex(poseStack, buffer, endLeft, u0, v1, endAlpha);
        addTexturedVertex(poseStack, buffer, endRight, u1, v1, endAlpha);
        addTexturedVertex(poseStack, buffer, startRight, u1, v0, startAlpha);
        addTexturedVertex(poseStack, buffer, startLeft, u0, v0, startAlpha);
    }

    static void renderFacingSprite(PoseStack poseStack, VertexConsumer buffer, Vec3 center, double size, int alpha) {
        renderFacingSprite(poseStack, buffer, center, size, 0.0D, alpha);
    }

    static void renderFacingSprite(PoseStack poseStack, VertexConsumer buffer, Vec3 center, double size, double angle, int alpha) {
        renderFacingSprite(poseStack, buffer, center, size, angle, 255, 255, 255, alpha);
    }

    static void renderFacingSprite(PoseStack poseStack, VertexConsumer buffer, Vec3 center, double size, double angle, int red, int green, int blue, int alpha) {
        Vector3f right = new Vector3f(1.0F, 0.0F, 0.0F).rotate(Minecraft.getInstance().gameRenderer.getMainCamera().rotation()).mul((float) size);
        Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F).rotate(Minecraft.getInstance().gameRenderer.getMainCamera().rotation()).mul((float) size);

        Vec3 rightVec = new Vec3(right.x(), right.y(), right.z());
        Vec3 upVec = new Vec3(up.x(), up.y(), up.z());
        if (angle != 0.0D) {
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            Vec3 rotatedRight = rightVec.scale(cos).add(upVec.scale(sin));
            Vec3 rotatedUp = upVec.scale(cos).subtract(rightVec.scale(sin));
            rightVec = rotatedRight;
            upVec = rotatedUp;
        }
        Vec3 bottomLeft = center.subtract(rightVec).subtract(upVec);
        Vec3 bottomRight = center.add(rightVec).subtract(upVec);
        Vec3 topRight = center.add(rightVec).add(upVec);
        Vec3 topLeft = center.subtract(rightVec).add(upVec);

        addTexturedVertex(poseStack, buffer, bottomLeft, 0.0F, 1.0F, red, green, blue, alpha);
        addTexturedVertex(poseStack, buffer, bottomRight, 1.0F, 1.0F, red, green, blue, alpha);
        addTexturedVertex(poseStack, buffer, topRight, 1.0F, 0.0F, red, green, blue, alpha);
        addTexturedVertex(poseStack, buffer, topLeft, 0.0F, 0.0F, red, green, blue, alpha);
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

    private static void addTexturedVertex(PoseStack poseStack, VertexConsumer buffer, Vec3 pos, float u, float v, int alpha) {
        addTexturedVertex(poseStack, buffer, pos, u, v, 255, 255, 255, alpha);
    }

    private static void addTexturedVertex(PoseStack poseStack, VertexConsumer buffer, Vec3 pos, float u, float v, int red, int green, int blue, int alpha) {
        buffer.addVertex(poseStack.last(), new Vector3f((float) pos.x, (float) pos.y, (float) pos.z))
                .setUv(u, v)
                .setColor(red, green, blue, clampAlpha(alpha));
    }
}
