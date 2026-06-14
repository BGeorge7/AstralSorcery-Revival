package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.registry.ASStructureRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class AstralShrinePiece extends TemplateStructurePiece {
    private static final String ROTATION_TAG = "Rot";

    public AstralShrinePiece(StructureTemplateManager templateManager, ResourceLocation templateId, BlockPos position, Rotation rotation) {
        super(ASStructureRegistries.SHRINE_PIECE.get(), 0, templateManager, templateId, templateId.toString(),
                AstralShrinePieces.settings(rotation), position);
    }

    public AstralShrinePiece(StructureTemplateManager templateManager, CompoundTag tag) {
        super(ASStructureRegistries.SHRINE_PIECE.get(), tag, templateManager,
                templateId -> AstralShrinePieces.settings(Rotation.valueOf(tag.getString(ROTATION_TAG))));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString(ROTATION_TAG, placeSettings.getRotation().name());
    }

    @Override
    protected void handleDataMarker(String marker, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
        AstralShrinePieces.handleMarker(marker, pos, level, random);
    }
}
