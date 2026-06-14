package hellfirepvp.astralsorcery.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class StarlightParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float startingAlpha;

    private StarlightParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
                              SpriteSet sprites, boolean large) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.hasPhysics = false;
        this.friction = 0.94F;
        this.gravity = -0.006F;
        this.lifetime = large ? 34 + random.nextInt(18) : 24 + random.nextInt(14);
        this.quadSize = (large ? 0.18F : 0.075F) + random.nextFloat() * (large ? 0.12F : 0.05F);
        this.startingAlpha = large ? 0.72F : 0.92F;
        this.alpha = startingAlpha;

        float warmth = random.nextFloat() * 0.08F;
        this.rCol = 0.92F + warmth;
        this.gCol = 0.96F + warmth;
        this.bCol = 1.0F;
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        float life = age / (float) lifetime;
        this.alpha = startingAlpha * (1.0F - life);
        this.quadSize *= 0.992F;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    public static class SmallProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SmallProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new StarlightParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, false);
        }
    }

    public static class LargeProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public LargeProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new StarlightParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, true);
        }
    }
}
