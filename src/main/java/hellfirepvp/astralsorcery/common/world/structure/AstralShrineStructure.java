package hellfirepvp.astralsorcery.common.world.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.registry.ASStructureRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class AstralShrineStructure extends Structure {
    public static final MapCodec<AstralShrineStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            ResourceLocation.CODEC.fieldOf("template").forGetter(structure -> structure.templateId)
    ).apply(instance, AstralShrineStructure::new));

    private final ResourceLocation templateId;

    public AstralShrineStructure(StructureSettings settings, ResourceLocation templateId) {
        super(settings);
        this.templateId = templateId;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMiddleBlockX();
        int z = chunkPos.getMiddleBlockZ();
        int y = context.chunkGenerator().getFirstOccupiedHeight(
                x,
                z,
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState());
        BlockPos position = new BlockPos(x, y, z);
        Rotation rotation = Rotation.getRandom(context.random());
        return Optional.of(new GenerationStub(position, builder ->
                AstralShrinePieces.addPiece(context.structureTemplateManager(), builder, templateId, position, rotation)));
    }

    @Override
    public StructureType<?> type() {
        return ASStructureRegistries.SHRINE.get();
    }
}
