package hellfirepvp.astralsorcery.common.structure;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.function.Predicate;

public final class MatchRule {
    public static final MatchRule ANY = new MatchRule(state -> true);

    private final Predicate<BlockState> predicate;

    private MatchRule(Predicate<BlockState> predicate) {
        this.predicate = predicate;
    }

    public static MatchRule block(Block block) {
        Objects.requireNonNull(block, "block");
        return new MatchRule(state -> state.is(block));
    }

    public boolean matches(BlockState state) {
        return predicate.test(state);
    }
}
