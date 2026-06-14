# Astral Sorcery Revival - NeoForge 1.21.1 Workspace

This workspace is a clean NeoForge 1.21.1 scaffold for porting Astral Sorcery.

## Current Baseline

- Minecraft: 1.21.1
- NeoForge: 21.1.233
- Java target: 21
- Mod id: `astralsorcery`
- Main class: `hellfirepvp.astralsorcery.AstralSorcery`
- Curios NeoForge: `9.5.1+1.21.1` as an optional development/runtime dependency

The original 1.16.5-era fork source is kept in `legacy-1.16.5` for migration reference. Do not wire that whole source tree into the build at once; port features incrementally into `src/main`.

## Useful Commands

```powershell
.\gradlew.bat tasks
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
.\gradlew.bat runData
```

NeoForge 1.21.1 requires Java 21. `JAVA_HOME` should point at a JDK 21 install.

## Porting Notes

- `docs/ai-porting-notes.md` tracks workflow decisions, migration findings, and handoff notes for future AI sessions.
- `docs/current-game-mechanics.md` tracks what currently works in-game versus what is still placeholder or not implemented.
- `docs/dependency-strategy.md` tracks dependency decisions around ObserverLib, Curios, and related integrations.
