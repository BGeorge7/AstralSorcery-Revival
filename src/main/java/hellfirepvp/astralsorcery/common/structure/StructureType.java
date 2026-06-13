package hellfirepvp.astralsorcery.common.structure;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record StructureType(ResourceLocation id, Component displayName, MultiblockPattern pattern) {
}
