package com.github.oobila.bukkit.minigame.environments;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.GameStatus;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@Getter
public class Environment implements ConfigurationSerializable {

    private final ABID id;
    private final String name;
    private Arena arena;
    private EnvironmentStatus status = EnvironmentStatus.CLOSED;
    @Setter
    private Location exitLocation;
    private final List<Portal> portals;
    // Set only by deserialize(), when a saved environment had a bound arena - Arena lives in
    // its own cache/file (data/arenas.yml), loaded independently of this one, so it can't be
    // resolved here. MinigamePlugin#onEnable resolves it against the arena cache once both
    // caches have loaded and calls restoreArena() with the result; null again afterwards, and
    // always null for a freshly-constructed Environment.
    private ABID pendingArenaId;

    public Environment(String name) throws ABIDException {
        this.id = new ABID();
        this.name = name;
        this.portals = new ArrayList<>();
    }

    private Environment(ABID id, String name, EnvironmentStatus status, Location exitLocation,
                         List<Portal> portals, ABID pendingArenaId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.exitLocation = exitLocation;
        this.portals = portals != null ? portals : new ArrayList<>();
        this.pendingArenaId = pendingArenaId;
    }

    /**
     * Restores the arena binding a saved environment had before its last shutdown, wiring both
     * sides of the back-reference directly. Unlike {@link #setArena}, this doesn't require (or
     * check) {@code CLOSED} status - it's reconstructing state that was already consistent when
     * it was saved, not performing a new user-initiated bind, so a saved {@code OPEN}
     * environment with a mid-match game stays exactly that instead of needing to be reopened by
     * hand. See {@link #pendingArenaId}.
     */
    public void restoreArena(Arena arena) {
        this.arena = arena;
        this.pendingArenaId = null;
        if (arena != null) {
            arena.setEnvironment(this);
        }
    }

    public void addPortal(Portal portal) {
        portals.add(portal);
    }

    public void removeAllPortals() {
        portals.clear();
    }

    public boolean setArena(Arena arena) {
        if (!this.status.equals(EnvironmentStatus.CLOSED)) {
            return false;
        }
        if (this.arena != null) {
            this.arena.setEnvironment(null);
        }
        this.arena = arena;
        if (arena != null) {
            arena.setEnvironment(this);
        }
        return true;
    }

    public boolean open() {
        Game game = arena != null ? arena.getGame() : null;
        if (game == null || game.getStatus() == null || !game.getStatus().equals(GameStatus.READY)) {
            return false;
        }
        status = EnvironmentStatus.OPEN;
        game.open();
        return true;
    }

    public boolean close() {
        if (!status.equals(EnvironmentStatus.OPEN)) {
            return false;
        }
        // status must flip to CLOSING before calling Game#close(), not after: a synchronous
        // close() implementation calls notifyClosed() immediately, which only takes effect
        // while status is already CLOSING - otherwise it's a no-op and this never reaches CLOSED.
        status = EnvironmentStatus.CLOSING;
        arena.getGame().close();
        return true;
    }

    public void notifyClosed() {
        if (status.equals(EnvironmentStatus.CLOSING)) {
            status = EnvironmentStatus.CLOSED;
        }
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("name", name);
        map.put("status", status.name());
        if (exitLocation != null) {
            map.put("exitLocation", exitLocation);
        }
        map.put("portals", new ArrayList<>(portals));
        if (arena != null) {
            map.put("arenaId", arena.getId().toString());
        }
        return map;
    }

    public static Environment deserialize(Map<String, Object> args) {
        return new Environment(
                ABID.fromString((String) args.get("id")),
                (String) args.get("name"),
                extractStatus(args),
                (Location) args.get("exitLocation"),
                extractPortals(args),
                extractArenaId(args)
        );
    }

    // Saves made before this field existed have no "status" key - default to CLOSED, the
    // field's own original hardcoded default, rather than failing to load old data.
    private static EnvironmentStatus extractStatus(@NotNull Map<String, Object> args) {
        Object raw = args.get("status");
        return raw instanceof String string ? EnvironmentStatus.valueOf(string) : EnvironmentStatus.CLOSED;
    }

    private static ABID extractArenaId(@NotNull Map<String, Object> args) {
        Object raw = args.get("arenaId");
        return raw instanceof String string ? ABID.fromString(string) : null;
    }

    @NotNull
    private static List<Portal> extractPortals(@NotNull Map<String, Object> args) {
        List<Portal> portals = new ArrayList<>();
        Object raw = args.get("portals");
        if (raw instanceof List<?> list) {
            for (Object entry : list) {
                if (entry instanceof Portal portal) {
                    portals.add(portal);
                }
            }
        }
        return portals;
    }
}
