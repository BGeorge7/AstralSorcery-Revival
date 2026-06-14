package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.world.feature.AstralShrineFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ASFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, AstralSorcery.MODID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> ANCIENT_SHRINE =
            shrine("ancient_shrine");
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> DESERT_SHRINE =
            shrine("desert_shrine");
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SMALL_SHRINE =
            shrine("small_shrine");

    private ASFeatures() {
    }

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }

    private static DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> shrine(String name) {
        return FEATURES.register(name, () -> new AstralShrineFeature(name));
    }
}
