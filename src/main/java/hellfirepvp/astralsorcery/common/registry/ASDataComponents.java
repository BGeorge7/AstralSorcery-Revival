package hellfirepvp.astralsorcery.common.registry;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ASDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, AstralSorcery.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CRYSTAL_PROPERTIES =
            stringComponent("crystal_properties");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CONSTELLATION =
            stringComponent("constellation");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> WAND_LINK =
            stringComponent("wand_link");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> RESONATOR_UPGRADE =
            stringComponent("resonator_upgrade");

    private ASDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }

    private static DeferredHolder<DataComponentType<?>, DataComponentType<String>> stringComponent(String name) {
        return DATA_COMPONENTS.registerComponentType(name, builder -> builder
                .persistent(Codec.STRING)
                .networkSynchronized(ByteBufCodecs.STRING_UTF8));
    }
}
