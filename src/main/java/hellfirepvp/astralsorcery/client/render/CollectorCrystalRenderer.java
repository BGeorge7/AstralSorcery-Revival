package hellfirepvp.astralsorcery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.render.effect.StarlightDirectedBeam;
import hellfirepvp.astralsorcery.client.render.effect.StarlightGodRays;
import hellfirepvp.astralsorcery.client.render.effect.StarlightRenderTypes;
import hellfirepvp.astralsorcery.common.tile.CollectorCrystalBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Block entity renderer for collector crystals.
 *
 * <p>The reusable starlight effects live under {@code client.render.effect};
 * this renderer only chooses which effects are active for this block entity.</p>
 */
public class CollectorCrystalRenderer implements BlockEntityRenderer<CollectorCrystalBlockEntity> {
    public CollectorCrystalRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CollectorCrystalBlockEntity crystal, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexConsumer buffer = bufferSource.getBuffer(StarlightRenderTypes.starlightRays());
        Vec3 center = new Vec3(0.5D, 0.5D, 0.5D);

        if (crystal.doesSeeSky()) {
            double time = (crystal.getLevel() == null ? 0.0D : crystal.getLevel().getGameTime() + partialTick);
            StarlightGodRays.render(poseStack, buffer, center, time, StarlightGodRays.COLLECTOR_CRYSTAL);
        }

        BlockPos target = crystal.getActiveTransmutationTarget();
        if (target != null && crystal.doesSeeSky()) {
            Vec3 targetCenter = Vec3.atCenterOf(target.subtract(crystal.getBlockPos())).add(0.0D, 0.35D, 0.0D);
            StarlightDirectedBeam.render(poseStack, buffer, center, targetCenter, StarlightDirectedBeam.TABLE_TRANSMUTATION);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(CollectorCrystalBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}
