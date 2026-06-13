package hellfirepvp.astralsorcery.common.registry;

import net.neoforged.bus.api.IEventBus;

public final class ASRegistries {
    private ASRegistries() {
    }

    public static void register(IEventBus modEventBus) {
        ASBlocks.register(modEventBus);
        ASItems.register(modEventBus);
        ASFluids.register(modEventBus);
        ASSounds.register(modEventBus);
        ASDataComponents.register(modEventBus);
        ASBlockEntityTypes.register(modEventBus);
        ASMenuTypes.register(modEventBus);
        ASRecipeSerializers.register(modEventBus);
        ASRecipeTypes.register(modEventBus);
        ASCreativeTabs.register(modEventBus);
    }
}
