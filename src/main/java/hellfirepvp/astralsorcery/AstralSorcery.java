package hellfirepvp.astralsorcery;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(AstralSorcery.MODID)
public class AstralSorcery {
    public static final String MODID = "astralsorcery";

    private static final Logger LOGGER = LogUtils.getLogger();

    public AstralSorcery(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        NeoForge.EVENT_BUS.register(this);
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
