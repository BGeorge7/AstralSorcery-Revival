package hellfirepvp.astralsorcery.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

/**
 * Reusable target-side particles for block transmutation.
 *
 * <p>Legacy block transmutation spawned {@code EffectTemplatesAS.GENERIC_PARTICLE}
 * at random positions inside the target block, tinted rock-crystal white-blue,
 * with fade-out alpha and negative gravity so the particle floated upward.
 * This renderer mirrors that behavior deterministically as a short burst from
 * the completed target position.</p>
 */
public final class StarlightTransmutationMotes {
    /**
     * Tuned for collector crystal crafting-table conversion.
     */
    public static final Settings CRAFTING_TABLE = new Settings(
            18,
            0.18D,
            0.14D,
            120,
            104,
            40.0D,
            20.0D,
            8.0D,
            0.58D,
            0.65D,
            0.16D,
            0.74D,
            0.24D);

    private static final int ROCK_CRYSTAL_R = 0xDD;
    private static final int ROCK_CRYSTAL_G = 0xDD;
    private static final int ROCK_CRYSTAL_B = 0xFF;

    private StarlightTransmutationMotes() {
    }

    /**
     * Renders one short particle burst from a target block.
     *
     * @param poseStack active render pose stack
     * @param buffer vertex buffer using {@link StarlightRenderTypes#starlightParticles()}
     * @param blockOrigin local-space lower corner of the target block
     * @param ageTicks ticks since the transmutation completed
     * @param settings count, scale, lifetime, and motion tuning
     */
    public static void renderBurst(PoseStack poseStack, VertexConsumer buffer, Vec3 blockOrigin, double ageTicks, Settings settings) {
        for (int i = 0; i < settings.moteCount(); i++) {
            double seed = StarlightRenderHelper.hashUnit(i * 31.0D + 3.0D);
            double delay = StarlightRenderHelper.hashUnit(i * 41.0D + 5.0D) * settings.randomDelayTicks();
            double lifetime = settings.minLifetimeTicks() + seed * settings.randomLifetimeTicks();
            double localAge = ageTicks - delay;
            if (localAge < 0.0D || localAge > lifetime) {
                continue;
            }
            double life = localAge / lifetime;

            double positionSeed = i * 149.0D + 23.0D;
            double x = settings.horizontalPadding()
                    + StarlightRenderHelper.hashUnit(positionSeed + 7.0D) * (1.0D - settings.horizontalPadding() * 2.0D);
            double z = settings.horizontalPadding()
                    + StarlightRenderHelper.hashUnit(positionSeed + 13.0D) * (1.0D - settings.horizontalPadding() * 2.0D);
            double startY = settings.minStartY() + StarlightRenderHelper.hashUnit(positionSeed + 19.0D) * settings.randomStartY();
            double driftAngle = StarlightRenderHelper.hashUnit(positionSeed + 29.0D) * Math.PI * 2.0D;
            double drift = (StarlightRenderHelper.hashUnit(positionSeed + 37.0D) - 0.5D) * settings.horizontalDrift();
            double rise = settings.riseHeight() * (life * 0.45D + life * life * 0.55D);
            Vec3 center = blockOrigin.add(
                    x + Math.cos(driftAngle) * drift * life,
                    startY + rise,
                    z + Math.sin(driftAngle) * drift * life);

            int alpha = (int) Math.round((settings.minAlpha() + settings.randomAlpha() * StarlightRenderHelper.hashUnit(positionSeed + 43.0D)) * (1.0D - life));
            if (alpha <= 1) {
                continue;
            }
            double size = settings.minSize() + StarlightRenderHelper.hashUnit(positionSeed + 47.0D) * settings.randomSize();
            double rotation = StarlightRenderHelper.hashUnit(positionSeed + 53.0D) * Math.PI * 2.0D;
            StarlightRenderHelper.renderFacingSprite(
                    poseStack,
                    buffer,
                    center,
                    size,
                    rotation,
                    ROCK_CRYSTAL_R,
                    ROCK_CRYSTAL_G,
                    ROCK_CRYSTAL_B,
                    alpha);
        }
    }

    /**
     * Immutable tuning for target-side block transmutation motes.
     */
    public record Settings(
            int moteCount,
            double minSize,
            double randomSize,
            int minAlpha,
            int randomAlpha,
            double minLifetimeTicks,
            double randomLifetimeTicks,
            double randomDelayTicks,
            double minStartY,
            double randomStartY,
            double horizontalPadding,
            double riseHeight,
            double horizontalDrift) {
        public double maxDurationTicks() {
            return minLifetimeTicks + randomLifetimeTicks + randomDelayTicks;
        }
    }
}
