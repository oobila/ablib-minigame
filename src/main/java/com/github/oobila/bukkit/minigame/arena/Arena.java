package com.github.oobila.bukkit.minigame.arena;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.minigame.game.Game;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Getter
@SerializableAs("Arena")
public class Arena implements ConfigurationSerializable {

    private final ABID id;
    private final Location minLocation;
    private final Location maxLocation;
    private ArenaStatus status;
    private final Map<Location, ArenaMarker> markers;
    private Game game;
    @Setter
    private Environment environment;

    public Arena(Location minLocation, Location maxLocation) throws ABIDException {
        this(new ABID(), minLocation, maxLocation, ArenaStatus.SETUP, new HashMap<>());
    }

    private Arena(
            ABID id,
            Location minLocation,
            Location maxLocation,
            ArenaStatus status,
            Map<Location, ArenaMarker> markers
    ) {
        this.id = id;
        this.minLocation = minLocation;
        this.maxLocation = maxLocation;
        this.status = status;
        this.markers = markers != null ? markers : new HashMap<>();
    }

    public void setGame(Game game) {
        if (this.game != null) {
            this.game.setArena(null);
        }
        this.game = game;
        if (game != null) {
            game.setArena(this);
        }
        // SETUP only ever describes a brand new arena that's never hosted a game - once one
        // has, there's no way back to SETUP, just IN_USE while bound and READY again once freed.
        status = game != null ? ArenaStatus.IN_USE : ArenaStatus.READY;
    }

    public void addMarker(ArenaMarker marker) {
        markers.put(marker.getLocation(), marker);
    }

    /**
     * Removes whichever marker occupies the same block as {@code location}, ignoring pitch/yaw -
     * a marker's exact placement {@link Location} is kept with pitch/yaw (so
     * {@code TeleportToMarkerAction} can orient a teleported player the way it was facing when
     * placed), so it will essentially never match the caller's current orientation exactly.
     * Returns whether a marker was actually found and removed.
     */
    public boolean removeMarkerAt(Location location) {
        return markers.entrySet().removeIf(entry -> sameBlock(entry.getKey(), location));
    }

    private static boolean sameBlock(@NotNull Location a, @NotNull Location b) {
        return a.getWorld() != null && a.getWorld().equals(b.getWorld())
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }

    public List<ArenaMarker> getMarkers(String type) {
        return markers.values().stream()
                .filter(marker -> marker.getType().equals(type))
                .toList();
    }

    public boolean contains(Location location) {
        if (location.getWorld() == null || !location.getWorld().equals(minLocation.getWorld())) {
            return false;
        }
        double minX = Math.min(minLocation.getX(), maxLocation.getX());
        double maxX = Math.max(minLocation.getX(), maxLocation.getX());
        double minY = Math.min(minLocation.getY(), maxLocation.getY());
        double maxY = Math.max(minLocation.getY(), maxLocation.getY());
        double minZ = Math.min(minLocation.getZ(), maxLocation.getZ());
        double maxZ = Math.max(minLocation.getZ(), maxLocation.getZ());
        return location.getX() >= minX && location.getX() <= maxX
                && location.getY() >= minY && location.getY() <= maxY
                && location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("minLocation", minLocation);
        map.put("maxLocation", maxLocation);
        map.put("status", status.name());
        // Location isn't a valid config-section key, so markers are flattened to a
        // list here and re-keyed by their own location on the way back in.
        map.put("markers", new ArrayList<>(markers.values()));
        if (game != null) {
            map.put("game", game);
        }
        return map;
    }

    @NotNull
    public static Arena deserialize(@NotNull Map<String, Object> args) {
        Arena arena = new Arena(
                ABID.fromString((String) args.get("id")),
                (Location) args.get("minLocation"),
                (Location) args.get("maxLocation"),
                ArenaStatus.valueOf((String) args.get("status")),
                extractMarkers(args)
        );
        if (args.get("game") instanceof Game game) {
            arena.setGame(game);
        }
        return arena;
    }

    @NotNull
    private static Map<Location, ArenaMarker> extractMarkers(@NotNull Map<String, Object> args) {
        Map<Location, ArenaMarker> markers = new HashMap<>();
        Object raw = args.get("markers");
        if (raw instanceof List<?> list) {
            for (Object entry : list) {
                if (entry instanceof ArenaMarker marker) {
                    markers.put(marker.getLocation(), marker);
                }
            }
        }
        return markers;
    }
}
