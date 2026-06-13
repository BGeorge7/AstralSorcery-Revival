package hellfirepvp.astralsorcery.common.starlight.network;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class StarlightNetwork {
    private final Map<BlockPos, BlockPos> links = new HashMap<>();

    public void link(BlockPos source, BlockPos target) {
        links.put(source.immutable(), target.immutable());
    }

    public void unlink(BlockPos source) {
        links.remove(source);
    }

    public Optional<BlockPos> targetFor(BlockPos source) {
        return Optional.ofNullable(links.get(source));
    }

    public Set<BlockPos> sources() {
        return Set.copyOf(links.keySet());
    }
}
