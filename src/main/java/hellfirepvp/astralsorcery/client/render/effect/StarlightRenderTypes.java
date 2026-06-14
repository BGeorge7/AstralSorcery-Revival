package hellfirepvp.astralsorcery.client.render.effect;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

/**
 * Shared render types for Astral Sorcery light effects.
 *
 * <p>The collector crystal god rays intentionally do not write depth. Vanilla
 * lightning writes depth, which causes other translucent geometry, such as the
 * crystal model itself, to disappear when viewed behind a ray.</p>
 */
public final class StarlightRenderTypes {
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

    private StarlightRenderTypes() {
    }

    /**
     * Additive, position/color-only starlight ray rendering with no depth write.
     */
    public static RenderType starlightRays() {
        return STARLIGHT_RAYS;
    }
}
