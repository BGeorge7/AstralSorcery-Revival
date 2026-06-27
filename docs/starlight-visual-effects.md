# Starlight Visual Effects

This document explains the reusable Astral Sorcery light/animation helpers in the NeoForge port. Future agents should use these helpers before creating new one-off beam, ray, sparkle, or collector-crystal visual code.

## Purpose

Astral Sorcery uses visual effects as gameplay feedback. Collector crystals, links, relays, altar crafts, rituals, and sky exposure all need effects that read as the same visual language.

The current reusable pieces live under:

- `src/main/java/hellfirepvp/astralsorcery/client/render/effect/StarlightGodRays.java`
- `src/main/java/hellfirepvp/astralsorcery/client/render/effect/StarlightAmbientMotes.java`
- `src/main/java/hellfirepvp/astralsorcery/client/render/effect/StarlightDirectedBeam.java`
- `src/main/java/hellfirepvp/astralsorcery/client/render/effect/StarlightRenderTypes.java`
- `src/main/java/hellfirepvp/astralsorcery/client/render/effect/StarlightRenderHelper.java`

The collector crystal block entity renderer shows the intended usage pattern:

- `src/main/java/hellfirepvp/astralsorcery/client/render/CollectorCrystalRenderer.java`

## Current Effect Parts

### God Rays

Use `StarlightGodRays` for ambient sky-exposure rays around a source block.

Current example:

```java
VertexConsumer rayBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightRays());
StarlightGodRays.render(poseStack, rayBuffer, center, time, StarlightGodRays.COLLECTOR_CRYSTAL);
```

Behavior:

- Draws flat diverging wedge planes.
- Every ray starts at the origin.
- Each ray has two straight sides that diverge at a fixed rate.
- Alpha fades in from the origin and fades out at the tip.
- Rays drift slowly with horizontal, vertical, length, and spin variation.

Use cases:

- Collector crystal sky exposure.
- Strong magical source idles.
- Later altar/ritual ambient light fields, if the effect should radiate from one point.

Do not use god rays for:

- Straight links between two positions.
- Crafting table conversion beams.
- Lens or prism network links.

### Ambient Motes

Use `StarlightAmbientMotes` for the small sparkle particles that idle around a crystal or magical source.

Current example:

```java
VertexConsumer particleBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightParticles());
StarlightAmbientMotes.render(
        poseStack,
        particleBuffer,
        center,
        time,
        StarlightAmbientMotes.COLLECTOR_CRYSTAL);
```

Behavior:

- Renders camera-facing sprites from local-space positions around the origin.
- Uses the legacy `textures/effect/particle_small.png` sprite used by old `EffectTemplatesAS.GENERIC_PARTICLE`.
- The collector crystal preset is centered on `(0.5, 0.5, 0.5)`, the middle of the crystal block.
- Motes appear at seeded random locations around the crystal, fade in, linger briefly, and fade out.
- Motes should not orbit around the crystal; old Astral Sorcery reads more like blurry stars blooming in place.
- Motes are layered and rotated with faint larger shells to soften the baked alpha silhouette of the sprite.
- This effect is always-on and separate from sky-exposure god rays.

Known limitation:

- `particle_small.png` itself has a visible octagonal alpha silhouette. Both `textures/effect/particle_small.png` and `textures/particle/particle_small.png` are identical. Blur, translucent blending, and layered/rotated backing sprites reduce the hard edge but do not remove it completely.
- Do not spend time trying to fix this by changing UVs; the texture is not cropped. If the octagon becomes unacceptable later, make a derived soft mote texture or choose a different legacy sprite for this specific ambient effect.

Use cases:

- Collector crystal idle sparkles.
- Later lightwell, ritual, gateway, and altar idle sparkles.
- Any subtle magical source that should read as alive without implying an active link.

### Directed Beam

Use `StarlightDirectedBeam.renderBeam` for focused light links between two points.

Current example:

```java
VertexConsumer beamBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightBeamTransfer());
StarlightDirectedBeam.renderBeam(
        poseStack,
        beamBuffer,
        center,
        targetCenter,
        time,
        StarlightDirectedBeam.TABLE_TRANSMUTATION);
```

Behavior:

- Renders a focused, pulsing beam from `start` toward `end`.
- Starts brighter at the source.
- Narrows toward the target.
- Extends slightly past the target so the taper finishes inside/past the target block.
- Uses multiple planes: bright core plus softer halo planes.
- Uses the legacy `textures/effect/lightbeam_transfer.png` sprite sheet.

Use cases:

- Collector crystal to crafting table conversion.
- Future collector-to-lens or lens-to-target links if they should look like focused starlight.
- Future altar crafting beams, with a new preset if the table-conversion tuning is wrong.

### Directed Beam Sparkles

Use `StarlightDirectedBeam.renderSparkles` for small motes moving along a focused beam.

Current example:

```java
VertexConsumer sparkleBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightParticles());
StarlightDirectedBeam.renderSparkles(
        poseStack,
        sparkleBuffer,
        center,
        targetCenter,
        time,
        StarlightDirectedBeam.TABLE_TRANSMUTATION);
```

Behavior:

- Renders small white camera-facing sprites along the beam.
- Uses the legacy `textures/effect/particle_small.png` sprite used by old `EffectTemplatesAS.GENERIC_PARTICLE`.
- Motes are seeded deterministically, so they do not flicker randomly every frame.
- Motes move slowly along the beam.
- The table-conversion preset keeps them close to the beam core.
- Motes use a pyramid-style path fade so they emerge from the crystal center and dissolve near the target.

Use cases:

- Beam energy flow.
- Future link charging/transfer feedback.
- Crafting lifecycle accents.

### Target Transmutation Motes

Use `StarlightTransmutationMotes` for the brief particle burst that rises from a block after starlight finishes transforming it.

Current example:

```java
BlockPos burstTarget = crystal.getLastTransmutationBurstTarget();
double burstAgeTicks = time - crystal.getLastTransmutationBurstGameTime();
VertexConsumer targetParticleBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightParticles());
Vec3 targetBlockOrigin = Vec3.atLowerCornerOf(burstTarget.subtract(crystal.getBlockPos()));
StarlightTransmutationMotes.renderBurst(
        poseStack,
        targetParticleBuffer,
        targetBlockOrigin,
        burstAgeTicks,
        StarlightTransmutationMotes.CRAFTING_TABLE);
```

Behavior:

- Renders camera-facing `particle_small.png` sprites from seeded random positions inside/above the target block.
- Particles are tinted legacy rock-crystal white-blue (`0xDDDDFF`).
- Particles drift upward and fade out, mirroring old `BlockTransmutationHandler.playTransmutation`.
- This is completion feedback, separate from the crystal-to-table directed beam and beam sparkles.
- Do not render these motes continuously during an active conversion; the collector crystal syncs a burst target and game time when the table actually changes.

Legacy reference:

```java
EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
        .spawn(new Vector3(pos).add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()))
        .setAlphaMultiplier(1F)
        .alpha(VFXAlphaFunction.FADE_OUT)
        .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
        .setScaleMultiplier(0.2F + rand.nextFloat() * 0.15F)
        .setGravityStrength(-0.0014F)
        .setMaxAge(40 + rand.nextInt(20));
```

## Render Types

### `starlightRays()`

Use for position/color-only additive light geometry:

- God rays.
- Simple untextured glow quads.

Important properties:

- Uses `DefaultVertexFormat.POSITION_COLOR`.
- Uses lightning/additive transparency.
- Writes color only, not depth.
- No cull.

The no-depth-write behavior is intentional. A vanilla lightning-style render type that writes depth can cause translucent geometry, such as collector crystal glass, to disappear behind rays.

### `starlightBeamTransfer()`

Use for textured transfer beams:

- Directed beam core/halo.

Important properties:

- Uses `DefaultVertexFormat.POSITION_TEX_COLOR`.
- Uses `GameRenderer::getPositionTexColorShader`.
- Uses `textures/effect/lightbeam_transfer.png`.
- Uses additive transparency.
- Writes color only, not depth.
- No cull.

### `starlightParticles()`

Use for textured Astral particle sprites:

- Directed beam motes.
- Collector-crystal idle motes.
- Target-side transmutation motes.
- Future altar crafting accents.

Important properties:

- Uses `DefaultVertexFormat.POSITION_TEX_COLOR`.
- Uses `GameRenderer::getPositionTexColorShader`.
- Uses `textures/effect/particle_small.png`.
- Uses translucent alpha blending, matching old generic particles more closely than additive beam blending.
- Enables texture blur because `particle_small.png.mcmeta` declares `"blur": true`; disabling blur makes the soft falloff look clipped/octagonal.
- Writes color only, not depth.
- No cull.

This is the modern reusable equivalent of the old generic facing particle shape, not a vanilla particle engine particle.

## Legacy Texture Sheet Warning

The legacy `lightbeam_transfer.png` is not a single beam texture. It is a sprite sheet.

Legacy registration:

```java
SPR_LIGHTBEAM_TRANSFER = new SpriteSheetResource(TEX_LIGHTBEAM_TRANSFER, 4, 16);
```

That means:

- 4 rows
- 16 columns
- 64 total frames

Do not sample the full texture from `(0, 0)` to `(1, 1)`. That stretches the whole sheet across the beam and creates a broken striped slab.

The current renderer computes one frame per render:

- `u0 = (frame % 16) / 16`
- `v0 = (frame / 16) / 4`
- `u1 = u0 + 1 / 16`
- `v1 = v0 + 1 / 4`

Then it animates by advancing `frame` from world time.

## Buffer Ordering Gotcha

Minecraft's `MultiBufferSource` can end the current buffer when a different render type is requested. Do not grab multiple `VertexConsumer`s up front and then write to an older one afterward.

Risky pattern:

```java
VertexConsumer rayBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightRays());
VertexConsumer beamBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightBeamTransfer());

// This can crash with "Not building!" because requesting beamBuffer may close rayBuffer.
StarlightGodRays.render(poseStack, rayBuffer, center, time, settings);
```

Safer pattern:

```java
VertexConsumer rayBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightRays());
StarlightGodRays.render(poseStack, rayBuffer, center, time, settings);

VertexConsumer beamBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightBeamTransfer());
StarlightDirectedBeam.renderBeam(poseStack, beamBuffer, start, end, time, settings);

// Re-request this after rendering the beam. BufferSource may close the previous
// particle buffer when a different RenderType is requested.
VertexConsumer sparkleBuffer = bufferSource.getBuffer(StarlightRenderTypes.starlightParticles());
StarlightDirectedBeam.renderSparkles(poseStack, sparkleBuffer, start, end, time, settings);
```

Observed failure:

- Crash description: `Rendering Block Entity`
- Cause: `java.lang.IllegalStateException: Not building!`
- Source was writing vertices into a stale buffer after switching render types.

## Effect Culling

Block entity renderers are frustum-culled by a render bounding box. NeoForge exposes this through `IBlockEntityRendererExtension#getRenderBoundingBox`, and the default is only the source block's one-block cube.

Any block entity renderer that draws beams, god rays, auras, or target-side particles outside the source block must override `getRenderBoundingBox`.

Current collector crystal behavior:

- Expands the crystal bounds by `12` blocks for god rays and ambient effects.
- Includes the active crafting-table beam target.
- Includes the recent transmutation burst target.

If future effects vanish when the source block is just off-screen, check this bounding box before tuning the renderer itself.

## Coordinate Space

These effects expect local block-entity renderer coordinates.

For a block center:

```java
Vec3 center = new Vec3(0.5D, 0.5D, 0.5D);
```

For a target block relative to the block entity:

```java
Vec3 targetCenter = Vec3.atCenterOf(target.subtract(sourceBlockPos)).add(0.0D, 0.35D, 0.0D);
```

Use world game time plus partial tick for smooth animation:

```java
double time = level.getGameTime() + partialTick;
```

## Current Presets

### `StarlightGodRays.COLLECTOR_CRYSTAL`

Tuned for shrine/worldgen collector crystals that can see the sky.

Visual intent:

- White translucent fan-like rays.
- Slowly rotating and drifting.
- Visible from multiple angles, including above.
- Soft enough not to hide the crystal completely.

### `StarlightDirectedBeam.TABLE_TRANSMUTATION`

Tuned for collector-crystal conversion of a vanilla crafting table into the discovery altar.

Visual intent:

- Brighter than the god rays.
- Continuous from crystal to target.
- Thick enough to still reach the target visibly.
- Narrows into/past the target block, not before the target.
- Has close, slow-moving motes along the beam.
- Motes are rendered from the crystal center toward the target as billboarded legacy particle sprites.
- Has a soft aura around the beam.

Important settings:

- `startWidth`: width at source.
- `endWidth`: width at target side.
- `startAlpha`: source-side brightness.
- `endAlpha`: target-side brightness.
- `endExtension`: how far past `end` the beam continues before taper completes.
- `pulseSpeed`: slow brightness pulse.
- `sparkleSize`: mote size.
- `sparkleAlpha`: mote brightness.
- `sparkleCount`: number of motes.
- `sparkleDriftSpeed`: speed of motes along beam.
- `sparkleFlankOffset`: distance of motes from beam core.
- `textureFrameSpeed`: animation speed through the legacy 4x16 beam sprite sheet.

## When Adding New Effects

Prefer adding a new preset before writing a new renderer.

Good examples:

- `StarlightDirectedBeam.LENS_LINK`
- `StarlightDirectedBeam.ALTAR_CRAFT`
- `StarlightGodRays.RITUAL_PEDESTAL`
- `StarlightTransmutationMotes.INFUSER_TARGET`

Create a new renderer only if the effect shape is genuinely different.

## Validation Checklist

After changing these effects:

1. Run `.\gradlew.bat --no-daemon build`.
2. Relaunch the NeoForge dev client only. Do not kill the user's separate 1.16.5 reference instance.
3. Check `run/logs/latest.log` for:
   - `Rendering Block Entity`
   - `IllegalStateException`
   - `Missing elements in vertex`
   - shader/render type errors
4. Test crystal visible through rays/beam.
5. Test from above and below.
6. Test day/night and sky blocked/unblocked.
7. Test with a nearby crafting table target and without one.
8. Compare against the 1.16.5 reference instance or a short recording when tuning visuals.

## Known Limitations

- Ambient motes are implemented, but the legacy `particle_small.png` alpha silhouette remains visibly octagonal in some views.
- Altar crafting particles/sounds are not implemented yet beyond the collector-crystal table conversion slice.
- The directed beam is tuned by visual iteration and should still be compared against more legacy footage.
- Future lens/prism/network beams may need additional presets or link-state syncing.
