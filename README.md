# MakeExcuse

A client-side Minecraft utility mod for competitive PvP players providing a realistic "Instant TCP Drop" alibi ("Connection reset by peer" / "Timed out").

**Author:** b4n9z  
**Domain / Group:** `io.github.b4n9z` (`https://b4n9z.github.io`)  
**Mod ID:** `makeexcuse`

---

## 1. Story & Purpose

In competitive Minecraft PvP, losing fights happens—but pride is everything. When on the brink of death, players often panic and want to combat-log, but leaving cleanly displays a conspicuous `"Player left the game"` message in chat and combat tags, immediately branding them as a coward.

**MakeExcuse** provides the perfect alibi:
When the emergency keybind (`U` by default, rebindable in *Controls*) is pressed, the client instantly drops the network socket (`Channel.close()` with `SO_LINGER = 0` and a simulated network `IOException: Connection reset by peer`).
- To opponents and server logs, it looks like a sudden network disconnect or internet timeout rather than an intentional disconnect.
- The player is immediately brought to the standard Minecraft `Disconnected` screen without crashing the game client, allowing a swift reconnect when ready.

---

## 2. Technical Architecture

- **Unified Multi-Loader Tree:** Supports **Fabric**, **Forge**, and **NeoForge** from a single source tree (`src/main/java`).
- **Client-Side Only:** No server installation required.
- **Instant TCP Drop:**
  - Configures `ChannelOption.SO_LINGER` to `0` to immediately abort the TCP socket (transmitting a TCP RST instead of a graceful 4-way FIN-ACK handshake).
  - Fires an authentic `IOException("Connection reset by peer")` through Netty's inbound pipeline.
  - Forcibly closes the Netty `Channel`.
  - Transitions to Minecraft's built-in `DisconnectedScreen` gracefully.
- **Chat Guard:** Protects against accidental triggers when typing in the `ChatScreen`.

---

## 3. How to Build

Run Gradle with the target loader subproject:

### Fabric
```bash
./gradlew -p fabric build
```
Output: `fabric/build/libs/MakeExcuse-fabric-mc26.3-1.0.0.jar`

### Forge
```bash
./gradlew -p forge build
```
Output: `forge/build/libs/MakeExcuse-forge-mc26.3-1.0.0.jar`

### NeoForge
```bash
./gradlew -p neoforge build
```
Output: `neoforge/build/libs/MakeExcuse-neoforge-mc26.3-1.0.0.jar`

---

## 4. Run Configurations

### Client Run
- Fabric: `./gradlew -p fabric runClient`
- Forge: `./gradlew -p forge runClient`
- NeoForge: `./gradlew -p neoforge runClient`

---

## 5. Project Structure

```
MakeExcuse/
├── gradle.properties                 # Shared versions & mod metadata
├── gradlew / gradlew.bat             # Gradle wrapper scripts
├── fabric/                           # Fabric Loom build module
├── forge/                            # ForgeGradle build module
├── neoforge/                         # NeoForged ModDev build module
└── src/
    └── main/
        ├── java/io/github/b4n9z/makeexcuse/
        │   ├── MakeExcuse.java               # Common entry & constants
        │   ├── client/
        │   │   └── MakeExcuseClient.java     # KeyMapping registration & client tick check
        │   ├── network/
        │   │   └── TcpDropHandler.java       # Instant TCP RST socket termination logic
        │   ├── mixin/
        │   │   └── ConnectionAccessor.java   # Netty Channel accessor
        │   ├── fabric/
        │   │   └── MakeExcuseFabricClient.java
        │   ├── forge/
        │   │   └── MakeExcuseForge.java
        │   └── neoforge/
        │       └── MakeExcuseNeoForge.java
        └── resources/
            ├── assets/makeexcuse/lang/en_us.json
            ├── makeexcuse.mixins.json
            ├── fabric.mod.json
            ├── icon.png
            ├── pack.mcmeta
            └── META-INF/
                ├── mods.toml                 # Forge manifest
                └── neoforge.mods.toml        # NeoForge manifest
```
