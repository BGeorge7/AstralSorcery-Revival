package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CrystalAttributeSet {
    private static final String LEGACY_ROOT = "crystalProperties";
    private static final String LEGACY_ATTRIBUTES = "attributes";
    private static final String LEGACY_PROPERTY = "property";
    private static final String LEGACY_LEVEL = "pLevel";
    private static final String LEGACY_DISCOVERED = "discovered";

    public static final ResourceLocation SIZE = ResourceLocation.fromNamespaceAndPath(AstralSorcery.MODID, "size");
    public static final ResourceLocation PURITY = ResourceLocation.fromNamespaceAndPath(AstralSorcery.MODID, "purity");
    public static final ResourceLocation SHAPE = ResourceLocation.fromNamespaceAndPath(AstralSorcery.MODID, "shape");
    public static final ResourceLocation COLLECTOR_RATE = ResourceLocation.fromNamespaceAndPath(AstralSorcery.MODID, "collector.rate");

    private final Map<ResourceLocation, Integer> levels = new LinkedHashMap<>();

    public static CrystalAttributeSet empty() {
        return new CrystalAttributeSet();
    }

    public static CrystalAttributeSet worldgenShrineCollector() {
        return empty()
                .with(SIZE, 2)
                .with(SHAPE, 2)
                .with(PURITY, 2)
                .with(COLLECTOR_RATE, 2);
    }

    public static CrystalAttributeSet load(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return worldgenShrineCollector();
        }

        if (tag.contains(LEGACY_ROOT, Tag.TAG_COMPOUND)) {
            return fromLegacyRoot(tag.getCompound(LEGACY_ROOT));
        }

        if (tag.contains("crystal_properties", Tag.TAG_COMPOUND)) {
            return fromOldPortTag(tag.getCompound("crystal_properties"));
        }

        return fromLegacyRoot(tag);
    }

    private static CrystalAttributeSet fromLegacyRoot(CompoundTag tag) {
        CrystalAttributeSet attributes = empty();
        ListTag list = tag.getList(LEGACY_ATTRIBUTES, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag attributeTag = list.getCompound(i);
            String propertyId = attributeTag.getString(LEGACY_PROPERTY);
            int level = attributeTag.getInt(LEGACY_LEVEL);
            if (!propertyId.isBlank() && level > 0) {
                attributes.with(ResourceLocation.parse(propertyId), level);
            }
        }
        return attributes.isEmpty() ? worldgenShrineCollector() : attributes;
    }

    private static CrystalAttributeSet fromOldPortTag(CompoundTag tag) {
        CrystalAttributeSet attributes = empty();
        attributes.with(SIZE, tag.getInt("size"));
        attributes.with(SHAPE, tag.getInt("shape"));
        attributes.with(PURITY, tag.getInt("purity"));
        attributes.with(COLLECTOR_RATE, tag.getInt("collection_rate"));
        return attributes.isEmpty() ? worldgenShrineCollector() : attributes;
    }

    public CrystalAttributeSet with(ResourceLocation property, int level) {
        if (level <= 0) {
            levels.remove(property);
        } else {
            levels.put(property, level);
        }
        return this;
    }

    public int get(ResourceLocation property) {
        return levels.getOrDefault(property, 0);
    }

    public boolean isEmpty() {
        return levels.isEmpty();
    }

    public CompoundTag saveIntoBlockEntityTag() {
        CompoundTag tag = new CompoundTag();
        tag.put(LEGACY_ROOT, saveLegacyRoot());
        return tag;
    }

    public CompoundTag saveLegacyRoot() {
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();
        for (Map.Entry<ResourceLocation, Integer> entry : levels.entrySet()) {
            CompoundTag attributeTag = new CompoundTag();
            attributeTag.putString(LEGACY_PROPERTY, entry.getKey().toString());
            attributeTag.putInt(LEGACY_LEVEL, entry.getValue());
            attributeTag.putBoolean(LEGACY_DISCOVERED, true);
            list.add(attributeTag);
        }
        root.put(LEGACY_ATTRIBUTES, list);
        return root;
    }

    public float collectorMultiplier() {
        float value = 1.0F;
        value *= 1.0F + (0.2F * get(SIZE));
        value *= 1.0F + (0.25F * get(SHAPE));
        value *= 1.0F + (0.4F * get(PURITY));
        value *= 1.0F + (0.2F * get(COLLECTOR_RATE));
        return value;
    }

    public String summary() {
        return "Size " + get(SIZE)
                + ", Purity " + get(PURITY)
                + ", Shape " + get(SHAPE)
                + ", Collector Rate " + get(COLLECTOR_RATE);
    }
}
