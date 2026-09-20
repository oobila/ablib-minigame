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

    public Environment(String name) throws ABIDException {
        this.id = new ABID();
        this.name = name;
        this.portals = new ArrayList<>();
    }

    private Environment(ABID id, String name, Location exitLocation, List<Portal> portals) {
        this.id = id;
        this.name = name;
        this.exitLocation = exitLocation;
        this.portals = portals != null ? portals : new ArrayList<>();
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
        arena.setEnvironment(this);
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
        arena.getGame().close();
        status = EnvironmentStatus.CLOSING;
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
        if (exitLocation != null) {
            map.put("exitLocation", exitLocation);
        }
        map.put("portals", new ArrayList<>(portals));
        return map;
    }

    public static Environment deserialize(Map<String, Object> args) {
        return new Environment(
                ABID.fromString((String) args.get("id")),
                (String) args.get("name"),
                (Location) args.get("exitLocation"),
                extractPortals(args)
        );
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
