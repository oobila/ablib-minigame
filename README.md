# ablib-minigame

A generic Bukkit/Spigot library for building minigame plugins. It provides the
reusable scaffolding — arenas, games, teams, portals, and the environments
that tie them together — so individual minigame plugins only need to
implement their own rules on top of it.

It is one of the `ablib-*` family of libraries (`ablib-common`,
`ablib-persistence`, `ablib-command`, `ablib-gui`, `ablib-itemstack`,
`ablib-chat`), published to JitPack, and depends on those siblings plus
[`abid`](https://github.com/alastairbooth/abid) for ID generation.

> **Status:** early scaffolding. Several classes below are functional today;
> others exist only as empty stubs sketching out where a concept will live.
> Each section says which is which.

## Core concepts

The library is built around five concepts that map onto how a minigame
server is actually laid out:

- **Arena** — a physical region in the world where a single instance of a
  game is played.
- **Game** — the rules and lifecycle for a specific minigame, bound to one
  Arena.
- **Team** — a group of players within a Game.
- **Environment** — the allocated "slot" that links a Game to the portals and
  other server-facing plumbing so a player can actually get into it.
- **Portal / GameMarker** — in-world features, loaded from arena schematics,
  that connect the physical server to these logical objects.

The intended production workflow is: an arena is built and saved as a
WorldEdit schematic, with `GameMarker` entities placed inside it at
important locations (spawns, chest points, etc.). WorldGuard protects the
pasted region. When the schematic is pasted back into the world, the markers
are read back into memory and turned into the `Arena`'s spawn points, and
`Portal`s elsewhere on the server are linked to the resulting `Environment`
so players can be transported in and out.

## Package structure

```
com.github.oobila.bukkit.minigame
├── Minigame.java              — static lookup helpers
├── build/                     — sequencing arena setup work
├── commands/                  — /environment and /game command trees
├── environments/              — Environment, Portal, EnvironmentStatus
├── game/                      — Arena, Game, Team, GameMarker, ...
│   └── markers/                 — concrete GameMarker types
└── gui/                       — inventory GUIs
```

## `game` package

### `Arena` (implemented)

Represents the physical bounding box a game is played in. Holds:

- `id` — an `ABID`, assigned on construction.
- `minLocation` / `maxLocation` — the two corners of the arena's bounding
  box (intended to line up with the WorldGuard region protecting it).
- `status` — an `ArenaStatus`: `SETUP`, `READY`, `IN_USE`, `CLEAN_UP`. This
  tracks where the arena is in its build/play/teardown cycle, e.g. while a
  schematic is being pasted vs. while it's safe to hand to a `Game`.
- `soloSpawns` — spawn points for free-for-all style games.
- `teamSpawns` — spawn points keyed by team index, for team-based games.

`Arena` implements `ConfigurationSerializable` so instances can be persisted
directly by Bukkit's config/YAML serialization (and by `ablib-persistence`
caches). The spawn lists are currently expected to be populated by hand or
by whatever reads the arena's `GameMarker`s at paste time — there is no
loader wired up yet.

### `Game` (implemented, abstract)

The base class a specific minigame implementation extends. It owns:

- `id`, `name` — identity.
- `area` — the `Arena` this instance of the game is bound to.
- `environment` — the `Environment` currently hosting this game.
- `status` — a `GameStatus` (`PREPARING`, `READY`, `PRE_GAME_LOBBY`,
  `IN_PROGRESS`, `POST_GAME_LOBBY`, `ENDED`), defaulting to `PREPARING`.
  It's settable only by `Game` and its subclasses (not external callers),
  since a game is expected to transition it itself as it moves through its
  own lifecycle — `Environment.open()` in particular only proceeds once a
  bound game reaches `READY`.

Subclasses must implement the lifecycle contract: `open()`, `close()`,
`forceEnd()`, `canJoin()`, `canRejoin()`, and `getDetailedStatusMessage()`.
`close()` and `forceEnd()` return `void` — a game is expected to move its
own `status` through to `ENDED` asynchronously rather than reporting
success/failure synchronously. `Environment` drives this lifecycle — see
below. Note that `teams` currently lives on the `TeamDeathmatchGame`
subclass rather than on the abstract `Game` base.

There is also a `GameInterface` (name, `setup()`, `onStart()`, `onStop()`)
which is **not yet wired to anything**. It looks like an earlier or
alternative sketch of the same lifecycle contract now expressed by the
abstract `Game` class; treat it as unused until it's either connected or
removed.

### `Team` (implemented)

A named group of `Player`s with a `TeamColor` (`WHITE`, `GRAY`, `RED`,
`YELLOW`, `GREEN`, `CYAN`, `PINK`), each mapped to an `ablib-common`
`BlockColor` so teams can be rendered consistently (banners, glass,
scoreboard tags, etc.). Serializable like `Arena`, though `Player`s
themselves are naturally excluded from serialization — a `Team` is expected
to be rebuilt with its players re-added at runtime rather than restored
from disk with players inside it.

### `ArenaMarker` and `arena.markers.*` (stub)

`ArenaMarker` is meant to be the in-schematic entity type that marks a
significant location inside an arena — currently just wraps a `Location`.
`SoloSpawnLocation` (in `arena.markers`) is the first concrete marker type,
extending `ArenaMarker` to represent a `soloSpawns` entry. Neither class has
any behaviour yet: nothing scans a pasted schematic for these markers, and
`ArenaMarker`'s `location` field isn't even exposed. When this is built out,
the expected shape is a small hierarchy of marker subtypes (solo spawn, team
spawn, chest point, etc.) that a schematic-paste listener discovers and
converts into the corresponding fields on `Arena` (and friends).

### `build` package (stub)

`Build` and `BuildStep` sketch a way to sequence the steps involved in
standing up an arena (e.g. paste schematic → protect with WorldGuard → scan
`GameMarker`s → mark `Arena` as `READY`). A `BuildStep` wraps an
`ablib-common` `Job` and tracks `WAITING`/`RUNNING`/`COMPLETE` status; `Build`
is meant to hold an ordered list of them. Nothing currently constructs or
runs a `Build`, so treat this as a placeholder for arena-provisioning
orchestration rather than usable code yet.

## `environments` package

### `Environment` (implemented)

The unit that links a `Game` to the rest of the server. An `Environment`
has a `status` (`EnvironmentStatus`: `OPEN`, `CLOSING`, `CLOSED`), an
`exitLocation` (where a player is sent when they leave the game — settable
via the `/environment exit` command below), a `List<Portal>` of the
portals attached to it (`addPortal`/`removePortal(ABID)`, managed via the
`/environment feature` commands below), and holds at most one `Game` at a
time via `setGame`, which refuses to swap games while the environment
isn't `CLOSED`.

`open()` requires the bound `Game` to be non-null and to have reached
`GameStatus.READY` before it calls `Game.open()`. `close()` requires the
environment to already be `OPEN`; since `Game.close()` is `void` (fire and
forget — it can't report success synchronously), `close()` optimistically
moves the environment straight to `CLOSING` once it's called `Game.close()`,
and something (the `Game` implementation, once its own teardown actually
finishes) is expected to call `notifyClosed()` to complete the transition
to `CLOSED`. This is the object the `/environment` commands and
`ViewEnvironmentsGui` operate on.

`EnvironmentStatus` also carries a `texture` per status — a player-skull
texture hash used by `ViewEnvironmentsGui` so open/closing/closed
environments render with a different head icon in the GUI.

### `Portal` (partially implemented)

Intended to represent an in-world portal that teleports a player to an
`Environment`'s lobby or an `Arena`'s spawn point, analogous to
`ArenaMarker` but for player-facing, always-present world features rather
than arena-internal schematic content. Its physical footprint is a
`minLocation`/`maxLocation` bounding box (mirroring `Arena`), rather than an
arbitrary set of blocks, and is fully serializable via
`ConfigurationSerializable`. An `Environment` now owns a
list of the `Portal`s attached to it (see above), but nothing yet points
the other way — a `Portal` doesn't yet know which `Environment` it should
send a player into, and nothing actually performs the teleport.

## `commands` package (implemented)

`EnvironmentCommand` is the `/environment` command tree (registered by the
consuming plugin), with subcommands:

| Subcommand | Aliases | Behaviour |
|---|---|---|
| `create <name>` | | Creates a new `Environment`, rejecting duplicate names. |
| `open <name>` | `o` | Opens the named environment if it's `CLOSED`. |
| `close <name>` | `c` | Closes the named environment if it's `OPEN`. |
| `remove <name>` | `delete`, `r` | Deletes the environment, only while `CLOSED`. |
| `exit <name>` | `e` | Sets the environment's `exitLocation` to the command sender's current location. Player-only. |
| `feature add portal <name>` | | Creates a new `Portal` and attaches it to the named environment; reports the portal's generated id. |
| `feature remove portal <name>` | | Removes every `Portal` from the named environment. |
| `view` | `v` | Opens `ViewEnvironmentsGui`, listing all environments. |

`feature` is itself a small command tree (`EnvironmentFeature` →
`EnvironmentFeatureAdd`/`EnvironmentFeatureRemove` → a per-feature-type
command such as `EnvironmentFeatureAddPortal`), deliberately structured so
that adding a new attachable feature type in future is one more subcommand
registered under `add`/`remove`, rather than a change to existing commands.

All subcommands operate against a `ReadAndWriteCache<ABID, Environment>`
(from `ablib-persistence`) passed in by the consuming plugin, which is the
source of truth for which environments exist. `CommandUtils` holds the
shared name-lookup and tab-completion helpers used across these commands.

`GameCommand` is a placeholder — it registers the `/game` command with no
subcommands yet.

## `gui` package (implemented)

`ViewEnvironmentsGui` is an `ablib-gui` `SimpleGui` listing every
`Environment` as a player-head button: the head texture reflects
`EnvironmentStatus`, and the lore shows the environment's ID, status, and
bound game name (or `NO GAME`).

## `Minigame` (implemented)

A small static lookup facade — `getCurrentGame(Player)` and
`getTeam(Player)` — that scans a static `environments` list to find which
game/team a player currently belongs to. Note `Minigame.environments` is
never assigned anywhere in the library yet; a consuming plugin (or a future
initializer) needs to set it before these lookups will return anything.

## Build

Standard Maven build (`./mvnw package`), targeting Java 26, against Spigot
API and the other `ablib-*` artifacts resolved via JitPack. Lombok
(`@Getter`/`@Setter`/etc.) requires the explicit annotation-processor-path
entry in `pom.xml` — JDK 26 no longer auto-discovers annotation processors
from the plain classpath. `jitpack.yml` pins the build JDK to Temurin
26.0.2 rather than letting JitPack auto-select an `-open` build, which has
been unreliable.
