# Dependency Strategy

## ObserverLib

Astral Sorcery 1.16.5 depended on `hellfirepvp.observerlib:observerlib:1.16.5-1.5.2.v77`.

ObserverLib is not just a small optional integration. Legacy Astral Sorcery uses it for:

- tick handler registration and lifecycle helpers
- structure and multiblock pattern descriptions
- efficient block-change observation around multiblocks
- world/section/global data caches
- structure previews and render helpers
- small utility types such as alternating sets and registry helpers

NeoForge 1.21.1 does not appear to provide a direct built-in replacement for the full ObserverLib feature set. Some individual pieces now have modern NeoForge/Minecraft equivalents, but the structure observation and cached world data layer will need a deliberate replacement.

Recommended approach:

1. Do not depend on old ObserverLib.
2. Do not port all of ObserverLib blindly as a separate required mod.
3. Build small internal Astral Sorcery systems only as each ported feature needs them.
4. Start with simple local replacements:
   - `TickTask` / scheduler abstraction over NeoForge tick events
   - minimal `BlockPattern` / `MultiblockPattern` model
   - structure validation by direct scans
5. Add efficient block-change observation later, only when performance profiling or gameplay behavior needs it.

This keeps the 1.21.1 port self-contained and avoids reviving a second abandoned dependency before the main mod can run.

## Curios

Astral Sorcery 1.16.5 used Curios for wearable/equipment integration, mostly around amulets and related accessory behavior.

The project has pivoted to NeoForge because Curios publishes current `1.21.1` artifacts as `curios-neoforge-*`.

Current dependency setup:

- `compileOnly "top.theillusivec4.curios:curios-neoforge:${curios_version}:api"`
- `localRuntime "top.theillusivec4.curios:curios-neoforge:${curios_version}"`

Recommended approach:

1. Keep Curios integration isolated behind an adapter.
2. Treat Curios as optional until we intentionally decide the mod should hard-require it.
3. Port amulet/core behavior with Curios support once the basic item registry exists.

## Immediate Porting Rule

When moving code out of `legacy-1.16.5`, any `observerlib` import should be treated as a porting boundary, not copied forward directly. Curios imports may be ported after a small integration adapter exists.
