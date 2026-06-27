package hellfirepvp.astralsorcery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.render.effect.StarlightAmbientMotes;
import hellfirepvp.astralsorcery.client.render.effect.StarlightDirectedBeam;
import hellfirepvp.astralsorcery.client.render.effect.StarlightGodRays;
import hellfirepvp.astralsorcery.client.render.effect.StarlightRenderTypes;
import hellfirepvp.astralsorcery.client.render.effect.StarlightTransmutationMotes;
import hellfirepvp.astralsorcery.common.tile.CollectorCrystalBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Block entity renderer for collector crystals.
 *
 * <p>The reusable starlight effects live under {@code client.render.effect};
 * this renderer only chooses which effects are active for this block entity.</p>
 */
public class CollectorCrystalRenderer implements BlockEntityRenderer<CollectorCrystalBlockEntity> {
    private static final double EFFECT_RENDER_PADDING = 12.0D;

    public CollectorCrystalRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CollectorCrystalBlockEntity crystal, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Vec3 center = new Vec3(0.5D, 0.5D, 0.5D);
        double time = crystal.getLevel() == null ? 0.0D : crystal.getLevel().getGameTime() + partialTick;

        VertexConsumer particleBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightParticles());
        StarlightAmbientMotes.render(poseStack, particleBuffer, center, time, StarlightAmbientMotes.COLLECTOR_CRYSTAL);

        if (crystal.doesSeeSky()) {
            VertexConsumer rayBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightRays());
            StarlightGodRays.render(poseStack, rayBuffer, center, time, StarlightGodRays.COLLECTOR_CRYSTAL);
        }

        BlockPos target = crystal.getActiveTransmutationTarget();
        if (target != null && crystal.doesSeeSky()) {
            Vec3 targetCenter = Vec3.atCenterOf(target.subtract(crystal.getBlockPos())).add(0.0D, 0.35D, 0.0D);
            VertexConsumer beamBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightBeamTransfer());
            StarlightDirectedBeam.renderBeam(poseStack, beamBuffer, center, targetCenter, time, StarlightDirectedBeam.TABLE_TRANSMUTATION);
            VertexConsumer beamSparkleBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightParticles());
            StarlightDirectedBeam.renderSparkles(poseStack, beamSparkleBuffer, center, targetCenter, time, StarlightDirectedBeam.TABLE_TRANSMUTATION);
        }

        BlockPos burstTarget = crystal.getLastTransmutationBurstTarget();
        double burstAgeTicks = time - crystal.getLastTransmutationBurstGameTime();
        if (burstTarget != null && burstAgeTicks >= 0.0D && burstAgeTicks <= StarlightTransmutationMotes.CRAFTING_TABLE.maxDurationTicks()) {
            VertexConsumer targetParticleBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightParticles());
            Vec3 targetBlockOrigin = Vec3.atLowerCornerOf(burstTarget.subtract(crystal.getBlockPos()));
            StarlightTransmutationMotes.renderBurst(poseStack, targetParticleBuffer, targetBlockOrigin, burstAgeTicks, StarlightTransmutationMotes.CRAFTING_TABLE);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(CollectorCrystalBlockEntity blockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(CollectorCrystalBlockEntity blockEntity) {
        AABB bounds = new AABB(blockEntity.getBlockPos()).inflate(EFFECT_RENDER_PADDING);
        bounds = includeEffectTarget(bounds, blockEntity.getActiveTransmutationTarget());
        bounds = includeEffectTarget(bounds, blockEntity.getLastTransmutationBurstTarget());
        return bounds;
    }

    private static AABB includeEffectTarget(AABB bounds, BlockPos target) {
        if (target == null) {
            return bounds;
        }
        return bounds.minmax(new AABB(target).inflate(2.0D));
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}
