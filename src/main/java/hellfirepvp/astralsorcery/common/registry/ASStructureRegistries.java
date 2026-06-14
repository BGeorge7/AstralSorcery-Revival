package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.world.structure.AstralShrinePiece;
import hellfirepvp.astralsorcery.common.world.structure.AstralShrineStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ASStructureRegistries {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, AstralSorcery.MODID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, AstralSorcery.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<AstralShrineStructure>> SHRINE =
            STRUCTURE_TYPES.register("shrine", () -> () -> AstralShrineStructure.CODEC);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> SHRINE_PIECE =
            STRUCTURE_PIECES.register("shrine_piece",
                    () -> (context, tag) -> new AstralShrinePiece(context.structureTemplateManager(), tag));

    private ASStructureRegistries() {
    }

    public static void register(IEventBus modEventBus) {
        STRUCTURE_TYPES.register(modEventBus);
        STRUCTURE_PIECES.register(modEventBus);
    }
}
