package hellfirepvp.astralsorcery.common.data;

import net.minecraft.nbt.CompoundTag;

public record ShrineCrystalProperties(int size, int shape, int purity, int collectionRate) {
    private static final String SIZE = "size";
    private static final String SHAPE = "shape";
    private static final String PURITY = "purity";
    private static final String COLLECTION_RATE = "collection_rate";

    public static ShrineCrystalProperties worldgenCollector() {
        return new ShrineCrystalProperties(2, 2, 2, 2);
    }

    public static ShrineCrystalProperties load(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return worldgenCollector();
        }
        return new ShrineCrystalProperties(
                tag.getInt(SIZE),
                tag.getInt(SHAPE),
                tag.getInt(PURITY),
                tag.getInt(COLLECTION_RATE));
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(SIZE, size);
        tag.putInt(SHAPE, shape);
        tag.putInt(PURITY, purity);
        tag.putInt(COLLECTION_RATE, collectionRate);
        return tag;
    }

    public float collectionMultiplier() {
        return 1.0F + collectionRate * 0.25F;
    }
}
