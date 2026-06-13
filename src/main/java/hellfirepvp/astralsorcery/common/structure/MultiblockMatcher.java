package hellfirepvp.astralsorcery.common.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

import java.util.ArrayList;
import java.util.List;

public final class MultiblockMatcher {
    private MultiblockMatcher() {
    }

    public static Result match(LevelReader level, BlockPos origin, MultiblockPattern pattern) {
        List<BlockPos> mismatches = new ArrayList<>();
        pattern.rules().forEach((offset, rule) -> {
            BlockPos checkPos = origin.offset(offset);
            if (!rule.matches(level.getBlockState(checkPos))) {
                mismatches.add(checkPos);
            }
        });
        return new Result(mismatches.isEmpty(), List.copyOf(mismatches));
    }

    public record Result(boolean matches, List<BlockPos> mismatches) {
    }
}
