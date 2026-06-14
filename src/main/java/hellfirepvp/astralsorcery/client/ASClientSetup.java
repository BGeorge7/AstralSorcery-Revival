package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.client.particle.StarlightParticle;
import hellfirepvp.astralsorcery.client.render.CollectorCrystalRenderer;
import hellfirepvp.astralsorcery.common.registry.ASBlockEntityTypes;
import hellfirepvp.astralsorcery.common.registry.ASBlocks;
import hellfirepvp.astralsorcery.common.registry.ASParticles;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class ASClientSetup {
    private ASClientSetup() {
    }

    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(ASBlocks.ROCK_COLLECTOR_CRYSTAL.get(), RenderType.translucent()));
    }

    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ASParticles.STARLIGHT_SMALL.get(), StarlightParticle.SmallProvider::new);
        event.registerSpriteSet(ASParticles.STARLIGHT_LARGE.get(), StarlightParticle.LargeProvider::new);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ASBlockEntityTypes.COLLECTOR_CRYSTAL.get(), CollectorCrystalRenderer::new);
    }
}
