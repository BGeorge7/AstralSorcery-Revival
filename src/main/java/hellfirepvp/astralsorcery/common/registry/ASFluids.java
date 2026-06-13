package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ASFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, AstralSorcery.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, AstralSorcery.MODID);

    public static final DeferredHolder<FluidType, FluidType> LIQUID_STARLIGHT_TYPE = FLUID_TYPES.register("liquid_starlight",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.astralsorcery.liquid_starlight")
                    .lightLevel(7)
                    .density(900)
                    .viscosity(1100)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_STARLIGHT = FLUIDS.register("liquid_starlight",
            () -> new BaseFlowingFluid.Source(properties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_LIQUID_STARLIGHT = FLUIDS.register("flowing_liquid_starlight",
            () -> new BaseFlowingFluid.Flowing(properties()));

    private ASFluids() {
    }

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(LIQUID_STARLIGHT_TYPE, LIQUID_STARLIGHT, FLOWING_LIQUID_STARLIGHT)
                .bucket(ASItems.BUCKET_LIQUID_STARLIGHT)
                .block(ASBlocks.LIQUID_STARLIGHT)
                .tickRate(20)
                .slopeFindDistance(3)
                .levelDecreasePerBlock(2)
                .explosionResistance(100.0F);
    }
}
