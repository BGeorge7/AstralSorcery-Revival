# Astral Sorcery Revival - 1.21.1 Workspace

This workspace is a clean Forge 1.21.1 scaffold for porting Astral Sorcery.

## Current Baseline

- Minecraft: 1.21.1
- Forge: 52.1.14
- Java target: 21
- Mod id: `astralsorcery`
- Main class: `hellfirepvp.astralsorcery.AstralSorcery`

The original 1.16.5-era fork source is kept in `legacy-1.16.5` for migration reference. Do not wire that whole source tree into the build at once; port features incrementally into `src/main`.

## Useful Commands

```powershell
.\gradlew.bat tasks
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

Forge 1.21.1 requires Java 21. If `java -version` shows Java 8, install a JDK 21 distribution and set `JAVA_HOME` before running Gradle.

This workspace also includes a local helper that points Gradle at the portable JDK 21 downloaded under `..\work\jdk21`:

```powershell
.\dev-gradle.ps1 tasks
.\dev-gradle.ps1 build
.\run-client.ps1
```
