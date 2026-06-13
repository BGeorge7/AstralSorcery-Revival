package hellfirepvp.astralsorcery;

import com.mojang.logging.LogUtils;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;

@Mod(AstralSorcery.MODID)
public class AstralSorcery {
    public static final String MODID = "astralsorcery";

    private static final Logger LOGGER = LogUtils.getLogger();

    public AstralSorcery(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Astral Sorcery Revival common setup loaded.");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Astral Sorcery Revival client setup loaded.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Astral Sorcery Revival server starting.");
    }
}
