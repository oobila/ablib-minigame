# ablib-minigame

A generic Bukkit/Spigot library for running minigames on a server. It provides
the reusable scaffolding — environments, arenas, games, teams, and a
file-driven scripting engine for game rules — so that a single, genre-agnostic
`Game` implementation can host wildly different minigames (1v1 duels, FFA,
team deathmatch, checkpoint races, capture the flag, battle royale, ...)
purely by loading a different config/script, with no new Java code per game
type.

It is one of the `ablib-*` family of libraries (`ablib-common`,
`ablib-persistence`, `ablib-command`, `ablib-gui`, `ablib-itemstack`,
`ablib-chat`), published to JitPack, and depends on those siblings plus
[`abid`](https://github.com/alastairbooth/abid) for ID generation.

> **Status:** the object model and scripting engine described below are
> functional and exercised by real (worked-example) scripts for FFA, team
> deathmatch, and a checkpoint race. What's **not** built yet: nothing drives
> the engine's tick loop or bridges real Bukkit events (deaths, etc.) into it
> — that glue is expected to live in the consuming plugin. See "What's not
> built yet" at the bottom.

## Core concepts

The object hierarchy is **Environment → Arena → Game**:

- **Environment** — the "slot" that links a `Game` to the rest of the server:
  it owns the portals players use to get in, an exit location for when they
  leave, and an open/closed lifecycle (`EnvironmentStatus`).
- **Arena** — the physical bounding box a game is actually played in, plus
  whatever `ArenaMarker`s have been placed inside it (spawn points, lobby
  points, checkpoints). An Environment holds at most one Arena at a time.
- **Game** — a running match, bound to one Arena. There is exactly one
  concrete implementation, `ScriptedGame`; every genre difference lives in
  the `GameConfig`/`GameScript` bound to it, not in a subclass.
- **GameConfig / GameScript** — the reusable, file-authored template for a
  game type: which teams it has (if any), how players get assigned to them,
  and the script of phases/triggers/actions that drives the match from lobby
  to finish. Many arenas can share the same `GameConfig`.
- **Team / TeamConfig** — a group of players within a `Game`, and the
  reusable name/colour/glow config it was built from.

## Package structure

```
com.github.oobila.bukkit.minigame
├── environments/     — Environment, Portal, EnvironmentStatus
├── arena/            — Arena, ArenaStatus, ArenaMarker
│   └── markers/         — concrete ArenaMarker types (spawns, lobbies, checkpoints)
├── team/             — Team, TeamConfig, TeamAssignment
├── game/             — Game, ScriptedGame, GameConfig, GameStatus
│   └── script/           — the phase/trigger/action scripting engine
│       ├── actions/         — built-in GameAction implementations
│       └── triggers/        — built-in GameTrigger implementations
├── items/            — AreaSelectionTool (the in-game arena-marking tool)
└── build/            — sequencing arena setup work (stub, see below)
```

## `environments` package

### `Environment`

Owns at most one `Arena` at a time via `setArena` (refuses to swap while not
`CLOSED`), an `exitLocation`, and a `List<Portal>`. `open()` requires the
bound Arena to have a `Game` that has reached `GameStatus.READY`, then calls
`Game.open()`. `close()` requires the environment to already be `OPEN`, calls
`Game.close()`, and optimistically moves to `CLOSING` — the `Game` is
expected to call `notifyClosed()` once its own teardown actually finishes.

`EnvironmentStatus` (`OPEN`/`CLOSING`/`CLOSED`) also carries a player-skull
texture hash per status, for GUI rendering in the consuming plugin.

### `Portal`

An in-world, always-present feature (a `minLocation`/`maxLocation` bounding
box, mirroring `Arena`) that teleports a player into an `Environment`. Fully
`ConfigurationSerializable`. An `Environment` owns the list of `Portal`s
attached to it; nothing yet performs the actual teleport (that's plugin-side
listener work).

## `arena` package

### `Arena`

The physical bounding box a game is played in: `minLocation`/`maxLocation`,
an `ArenaStatus` (`SETUP`/`READY`/`IN_USE`/`CLEAN_UP`), a `Map<Location,
ArenaMarker>`, and (not final) a `game`/`environment` pair of back-references.
`getMarkers(Class<T>)` filters the marker map by concrete type. `setGame`
wires the bidirectional Arena↔Game link.

`Arena` persists its bound `Game` inline when one is set (nested
`ConfigurationSerializable`, same pattern `Team` uses for `TeamConfig`) — so
an in-progress match survives a server restart for free via whatever cache
the consuming plugin already stores Arenas in, with no separate game cache
needed.

### `ArenaMarker` and `arena.markers.*`

`ArenaMarker` wraps a `Location` plus a free-form `Map<String,String>
metadata` bag, and is the common base every concrete marker type extends.
Current marker types:

| Marker | Purpose |
|---|---|
| `SoloSpawnLocation` | Where an FFA/team-less player spawns |
| `TeamSpawnLocation` | Where a team spawns, matched to a `TeamConfig` by `BlockColor` |
| `PreGameLobbySpawn` | Where players wait before a match starts |
| `PostGameLobbySpawn` | Where players go once a match ends |
| `CheckpointMarker` | An ordered (`order`) waypoint for a checkpoint race |

Nothing yet scans a pasted schematic for these — they're expected to be
placed by hand (or by whatever the `items.AreaSelectionTool` workflow grows
into) until that's built out.

## `team` package

`TeamConfig` (`name`, `teamColor: BlockColor`, `isGlowing`) is the reusable,
file-authored description of a team. `Team` is the runtime instance: a
`TeamConfig` plus a live roster of `OfflinePlayer`s (`addPlayer`/
`removePlayer`/`hasPlayer`/`getPlayerCount`/`forEachPlayer`/`sumPlayers`) —
deliberately no raw list getter, so nothing outside `Team` can mutate the
roster except through those methods. `addPlayer`/`removePlayer` also toggle
glowing if `isGlowing` is set. `TeamAssignment` is currently just `RANDOM`.

## `game` package

### `Game` (abstract) and `ScriptedGame`

`Game` owns identity (`id`, `name`), its bound `arena`/`gameConfig`, a
`GameStatus` (`PREPARING → READY → PRE_GAME_LOBBY → IN_PROGRESS →
POST_GAME_LOBBY → ENDED`), its `teams`, `players`, per-player `scores`, and
an `eliminated` set. It provides the primitives every game mode needs
regardless of genre:

- `addScore`/`getScore` (per player) and `getTeamScore` (derived by summing
  a team's members) — this one primitive covers kill counts, team kill
  aggregates, *and* "checkpoints passed" (see the race example below).
- `eliminate`/`isEliminated`/`getActivePlayers`/`getActiveTeams`.
- `getLeadingPlayer`/`getLeadingTeam` (highest score) for announcing a
  winner.
- `transitionTo(GameStatus)` — moves to a new phase and runs that phase's
  `onEnter` actions (see `game.script` below).
- `tick()` / `fireEvent(GameEventContext)` — evaluates the current phase's
  triggers against an event key (`"tick"`, `"death"`, ...). Nothing calls
  these yet; see "What's not built yet".

`join`/`leave` are generic framework behaviour (not script-driven): if teams
already exist and the game is `IN_PROGRESS`, a joining player is added to
whichever team currently has the fewest players, and a leaving player is
removed from whichever team they were on.

`ScriptedGame` is the single concrete `Game`. Its lifecycle methods
(`open`/`close`/`forceEnd`/`canJoin`/`canRejoin`) are just thin wrappers
around `transitionTo`/status checks — there is deliberately nothing
game-specific in this class.

### `GameConfig`

The reusable template: `teams: List<TeamConfig>`, `teamAssignment`, and the
`GameScript` that drives the match. `ConfigurationSerializable`, so it can be
hand-authored as YAML (see the worked example below) or built by a command
in the consuming plugin.

## `game.script` package — the scripting engine

This is how a game's rules are "100% on file, not code": a `GameScript` is a
`Map<GameStatus, GamePhaseScript>`. Each `GamePhaseScript` has:

- `onEnter: List<ActionBinding>` — actions that always run once, the moment
  that phase is entered (e.g. teleport everyone to lobby markers).
- `triggers: List<TriggerBinding>` — each pairs a named `GameTrigger` (with
  its own config `attributes`) to a list of `ActionBinding`s that run when
  it matches. A binding only reacts to one event key (`event: tick`,
  `event: death`, ...) and, unless `repeatable: true`, only fires once per
  phase entry (its `fired` flag resets whenever the phase is re-entered) —
  this is what stops a win condition from re-firing every tick once true.

`GameAction`/`GameTrigger` are plain interfaces
(`run(Game, GameActionAttributes, GameEventContext)` /
`test(Game, GameActionAttributes, GameEventContext)`); implementations are
looked up purely by string key via `GameActionRegistry`/
`GameTriggerRegistry`, never referenced by class from a script. Nothing
persists an `Attributes` object as its own `ConfigurationSerializable` type —
it's a plain `Map<String, Object>` nested directly under the binding, so
hand-written YAML doesn't need an extra `==:` marker, and unquoted numbers/
booleans are coerced to the right type on read.

A consuming plugin must call `BuiltInGameScript.register()` once at startup
(before loading any saved games/scripts) to populate both registries.

### Built-in actions (`game.script.actions`)

| Key | Attributes | Effect |
|---|---|---|
| `assign-teams` | — | Builds one `Team` per `TeamConfig` in the `GameConfig`, then (if `teamAssignment: RANDOM`) shuffles and round-robins current players across them. No-op for team-less configs. |
| `teleport-to-marker` | `marker`: `solo-spawn` (default, round-robin) / `team-spawn` (matched by colour) / `pre-game-lobby` / `post-game-lobby` | Teleports players to the matching `ArenaMarker`(s). |
| `award-score` | `who` (context key, default `player`), `amount` (default 1), `friendlyFire` (default true), `against` (context key) | Adds to a player's score; if `friendlyFire: false` and `who`/`against` are on the same team, does nothing. |
| `eliminate` | `who` (context key, default `player`) | Marks a player eliminated. |
| `transition-phase` | `phase` | Moves the game to another `GameStatus`. |
| `end-game` | — | Shorthand for `transition-phase(phase: POST_GAME_LOBBY)`. |
| `announce-winner` | `target`: `player` (default) / `team` | Sends everyone a "wins!" message for the current leader, via `ablib-chat`'s `Message`. |
| `broadcast-message` | `message` | Sends literal text to every current participant, via `ablib-chat`. |

### Built-in triggers (`game.script.triggers`)

| Key | Attributes | Fires when |
|---|---|---|
| `always` | — | Unconditionally (for events where the point is just reacting, e.g. every death). |
| `time-elapsed` | `seconds` (default 60) | The current phase has been active at least this long. Doubles as a lobby countdown and a time-limit win condition. |
| `score-threshold` | `threshold` (default 1), `target`: `player` (default) / `team` | Any player/team's score reaches the threshold. Also the finish line for a checkpoint race (`threshold` = number of checkpoints). |
| `last-entity-standing` | `target`: `player` (default) / `team` | Only one non-eliminated player/team remains, out of more than one starter. |
| `checkpoint-proximity` | `radius` (default 2.0) | Per active player: advances their score by one if they're within `radius` of the `CheckpointMarker` matching their current score (i.e. their next checkpoint). Side-effecting, not just a test. |

## Example: a team deathmatch script

This is exactly what `GameConfig#serialize()` writes (and `#deserialize()`
reads back) for a 2-team, first-to-20-kills-or-10-minutes deathmatch:

```yaml
==: GameConfig
teams:
- ==: TeamConfig
  name: Red
  teamColor: RED
  isGlowing: false
- ==: TeamConfig
  name: Blue
  teamColor: BLUE
  isGlowing: false
teamAssignment: RANDOM
script:
  ==: GameScript
  name: team-deathmatch
  phases:
    PRE_GAME_LOBBY:
      ==: GamePhaseScript
      onEnter:
      - ==: ActionBinding
        actionKey: assign-teams
        attributes: {}
      - ==: ActionBinding
        actionKey: teleport-to-marker
        attributes:
          marker: pre-game-lobby
      triggers:
      - ==: TriggerBinding
        event: tick
        triggerKey: time-elapsed
        attributes:
          seconds: 20
        repeatable: false
        actions:
        - ==: ActionBinding
          actionKey: transition-phase
          attributes:
            phase: IN_PROGRESS
    IN_PROGRESS:
      ==: GamePhaseScript
      onEnter:
      - ==: ActionBinding
        actionKey: teleport-to-marker
        attributes:
          marker: team-spawn
      triggers:
      - ==: TriggerBinding
        event: death
        triggerKey: always
        attributes: {}
        repeatable: true
        actions:
        - ==: ActionBinding
          actionKey: award-score
          attributes:
            who: killer
            against: victim
            friendlyFire: false
            amount: 1
      - ==: TriggerBinding
        event: tick
        triggerKey: score-threshold
        attributes:
          target: team
          threshold: 20
        repeatable: false
        actions:
        - ==: ActionBinding
          actionKey: end-game
          attributes: {}
        - ==: ActionBinding
          actionKey: announce-winner
          attributes:
            target: team
      - ==: TriggerBinding
        event: tick
        triggerKey: time-elapsed
        attributes:
          seconds: 600
        repeatable: false
        actions:
        - ==: ActionBinding
          actionKey: end-game
          attributes: {}
        - ==: ActionBinding
          actionKey: announce-winner
          attributes:
            target: team
    POST_GAME_LOBBY:
      ==: GamePhaseScript
      onEnter:
      - ==: ActionBinding
        actionKey: teleport-to-marker
        attributes:
          marker: post-game-lobby
      - ==: ActionBinding
        actionKey: broadcast-message
        attributes:
          message: Game over! Returning to the lobby shortly...
      triggers:
      - ==: TriggerBinding
        event: tick
        triggerKey: time-elapsed
        attributes:
          seconds: 10
        repeatable: false
        actions:
        - ==: ActionBinding
          actionKey: transition-phase
          attributes:
            phase: ENDED
```

Note the `death` binding is `repeatable: true` (every death should count) while
every win-condition binding is `repeatable: false` (fire once, end the game,
don't re-fire on the next tick) and the score and time-limit conditions race
each other — whichever hits first ends the match.

### The same shape for FFA and a checkpoint race

Only the `IN_PROGRESS` phase (and whether `teams`/`assign-teams` are present
at all) actually changes between game types — `PRE_GAME_LOBBY`/
`POST_GAME_LOBBY` above are reusable as-is.

**FFA** — no `teams` in the `GameConfig`, spawn on `solo-spawn` instead of
`team-spawn`, `award-score` defaults (`who: killer`, no `against`/
`friendlyFire`), and win on either threshold:

```yaml
IN_PROGRESS:
  ==: GamePhaseScript
  onEnter:
  - ==: ActionBinding
    actionKey: teleport-to-marker
    attributes:
      marker: solo-spawn
  triggers:
  - ==: TriggerBinding
    event: death
    triggerKey: always
    attributes: {}
    repeatable: true
    actions:
    - ==: ActionBinding
      actionKey: award-score
      attributes:
        who: killer
  - ==: TriggerBinding
    event: tick
    triggerKey: score-threshold
    attributes:
      threshold: 10
    repeatable: false
    actions:
    - ==: ActionBinding
      actionKey: end-game
      attributes: {}
    - ==: ActionBinding
      actionKey: announce-winner
      attributes: {}
  - ==: TriggerBinding
    event: tick
    triggerKey: last-entity-standing
    attributes: {}
    repeatable: false
    actions:
    - ==: ActionBinding
      actionKey: end-game
      attributes: {}
    - ==: ActionBinding
      actionKey: announce-winner
      attributes: {}
```

**Checkpoint race** — also no `teams`; score *is* checkpoint progress, so the
win condition is just a `score-threshold` set to the arena's checkpoint count:

```yaml
IN_PROGRESS:
  ==: GamePhaseScript
  onEnter:
  - ==: ActionBinding
    actionKey: teleport-to-marker
    attributes:
      marker: solo-spawn
  triggers:
  - ==: TriggerBinding
    event: tick
    triggerKey: checkpoint-proximity
    attributes:
      radius: 3
    repeatable: true
    actions: []
  - ==: TriggerBinding
    event: tick
    triggerKey: score-threshold
    attributes:
      threshold: 5
    repeatable: false
    actions:
    - ==: ActionBinding
      actionKey: end-game
      attributes: {}
    - ==: ActionBinding
      actionKey: announce-winner
      attributes: {}
```

## `items` package

`AreaSelectionTool` is a wand item (left/right click to mark two corners)
used when creating an `Arena` from the current selection.

## `build` package (stub)

`Build`/`BuildStep` sketch a way to sequence arena-provisioning steps (paste
schematic → protect region → scan markers → mark `Arena` READY), wrapping an
`ablib-common` `Job` per step. Nothing currently constructs or runs a
`Build`.

## What's not built yet

- **Nothing calls `Game.tick()` or bridges real Bukkit events into
  `fireEvent`.** A consuming plugin needs to: run a repeating task that calls
  `tick()` on every active game, and register a `PlayerDeathEvent` listener
  that finds which game a dying player belongs to and calls
  `fireEvent(new GameEventContext(GameEventContext.DEATH, Map.of("player",
  victim, "killer", killer, "victim", victim)))` — note `award-score`/
  `eliminate` default their `who` attribute to the context key `"player"`,
  so a plain "eliminate on death" binding needs no attributes, while
  "award the killer" needs `who: killer` explicitly.
- **No command wires a `GameConfig`/`GameScript` file to an arena** — right
  now these objects exist and (de)serialize correctly, but something in the
  consuming plugin needs to load/author them (e.g. via a
  `SimpleFileCache<String, GameConfig>` from `ablib-persistence`, keyed by a
  chosen template name so many arenas can share one) and call
  `arena.setGame(new ScriptedGame(name))` /
  `game.setGameConfig(theLoadedConfig)`.
- Gameplay-critical custom blocks (a CTF flag, a BR supply crate) and a
  battle-royale shrinking world border are intentionally deferred — see the
  design discussion this library grew out of.

## Build

Standard Maven build (`./mvnw package`), targeting Java 26, against Spigot
API and the other `ablib-*` artifacts resolved via JitPack. Lombok
(`@Getter`/`@Setter`/etc.) requires the explicit annotation-processor-path
entry in `pom.xml` — JDK 26 no longer auto-discovers annotation processors
from the plain classpath. `jitpack.yml` pins the build JDK to Temurin
26.0.2 rather than letting JitPack auto-select an `-open` build, which has
been unreliable.
