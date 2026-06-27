package hellfirepvp.astralsorcery.client.render.effect;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Shared render types for Astral Sorcery light effects.
 *
 * <p>The collector crystal god rays intentionally do not write depth. Vanilla
 * lightning writes depth, which causes other translucent geometry, such as the
 * crystal model itself, to disappear when viewed behind a ray.</p>
 */
public final class StarlightRenderTypes {
    private static final ResourceLocation LIGHTBEAM_TRANSFER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AstralSorcery.MODID,
            "textures/effect/lightbeam_transfer.png");
    private static final ResourceLocation PARTICLE_SMALL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AstralSorcery.MODID,
            "textures/effect/particle_small.png");
    private static final RenderStateShard.ShaderStateShard POSITION_TEX_COLOR_SHADER =
            new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader);

    private static final RenderType STARLIGHT_RAYS = RenderType.create(
            "astralsorcery_starlight_rays",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_LIGHTNING_SHADER)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false));
    private static final RenderType STARLIGHT_BEAM_TRANSFER = RenderType.create(
            "astralsorcery_starlight_beam_transfer",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_TEX_COLOR_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(LIGHTBEAM_TRANSFER_TEXTURE, false, false))
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false));
    private static final RenderType STARLIGHT_PARTICLES = RenderType.create(
            "astralsorcery_starlight_particles",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_TEX_COLOR_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(PARTICLE_SMALL_TEXTURE, true, false))
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false));

    private StarlightRenderTypes() {
    }

    /**
     * Additive, position/color-only starlight ray rendering with no depth write.
     */
    public static RenderType starlightRays() {
        return STARLIGHT_RAYS;
    }

    /**
     * Textured additive transfer beam rendering using Astral Sorcery's legacy
     * {@code lightbeam_transfer.png} asset.
     */
    public static RenderType starlightBeamTransfer() {
        return STARLIGHT_BEAM_TRANSFER;
    }

    /**
     * Textured translucent particle rendering using Astral Sorcery's legacy
     * {@code particle_small.png} sprite. The legacy texture declares blur in
     * its {@code .mcmeta}; this render type preserves that soft sampling.
     */
    public static RenderType starlightParticles() {
        return STARLIGHT_PARTICLES;
    }
}
