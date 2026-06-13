package hellfirepvp.astralsorcery.common.structure;

import net.minecraft.core.BlockPos;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MultiblockPattern {
    private final Map<BlockPos, MatchRule> rules;

    private MultiblockPattern(Map<BlockPos, MatchRule> rules) {
        this.rules = Map.copyOf(rules);
    }

    public Map<BlockPos, MatchRule> rules() {
        return rules;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<BlockPos, MatchRule> rules = new LinkedHashMap<>();

        public Builder at(int x, int y, int z, MatchRule rule) {
            rules.put(new BlockPos(x, y, z), rule);
            return this;
        }

        public MultiblockPattern build() {
            return new MultiblockPattern(rules);
        }
    }
}
