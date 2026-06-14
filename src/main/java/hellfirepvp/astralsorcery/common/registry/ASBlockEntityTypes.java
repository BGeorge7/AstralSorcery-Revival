package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.tile.CollectorCrystalBlockEntity;
import hellfirepvp.astralsorcery.common.tile.DiscoveryAltarBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public final class ASBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AstralSorcery.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DiscoveryAltarBlockEntity>> DISCOVERY_ALTAR =
            BLOCK_ENTITY_TYPES.register("discovery_altar",
                    () -> new BlockEntityType<>(DiscoveryAltarBlockEntity::new, Set.of(ASBlocks.ALTAR_DISCOVERY.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CollectorCrystalBlockEntity>> COLLECTOR_CRYSTAL =
            BLOCK_ENTITY_TYPES.register("collector_crystal",
                    () -> new BlockEntityType<>(CollectorCrystalBlockEntity::new, Set.of(ASBlocks.ROCK_COLLECTOR_CRYSTAL.get()), null));

    private ASBlockEntityTypes() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}
