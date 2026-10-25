package com.github.oobila.bukkit.minigame.arena;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A named point in an {@link Arena} that a script's actions/triggers look players up by -
 * {@link #getType()} is what they match on, and {@link #getMetadata()} carries whatever extra
 * data a given type needs (e.g. {@link #TEAM_SPAWN}'s team colour, {@link #CHECKPOINT}'s order)
 * rather than a dedicated field.
 * <p>
 * Unlike a {@link com.github.oobila.bukkit.minigame.game.script.GameAction}/{@code GameTrigger},
 * there's no registry to extend here - a type is just a string, so a host plugin (or another
 * library built on this one) can place and look up its own marker types with no change needed
 * in this library. {@link #SOLO_SPAWN}, {@link #TEAM_SPAWN}, {@link #PRE_GAME_LOBBY}, {@link
 * #POST_GAME_LOBBY} and {@link #CHECKPOINT} are simply the types the built-in {@code
 * teleport-to-marker} action and {@code checkpoint-proximity} trigger already know about.
 */
@Getter
@SerializableAs("ArenaMarker")
public class ArenaMarker implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(ArenaMarker.class);
    }

    public static final String SOLO_SPAWN = "solo-spawn";
    public static final String TEAM_SPAWN = "team-spawn";
    public static final String PRE_GAME_LOBBY = "pre-game-lobby";
    public static final String POST_GAME_LOBBY = "post-game-lobby";
    public static final String CHECKPOINT = "checkpoint";

    private final Location location;
    private final String type;
    private final Map<String, String> metadata;

    public ArenaMarker(Location location, String type) {
        this(location, type, new HashMap<>());
    }

    public ArenaMarker(Location location, String type, Map<String, String> metadata) {
        this.location = location;
        this.type = type;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("type", type);
        map.put("metadata", metadata);
        return map;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static ArenaMarker deserialize(@NotNull Map<String, Object> args) {
        Object rawMetadata = args.get("metadata");
        Map<String, String> metadata = rawMetadata != null ? (Map<String, String>) rawMetadata : new HashMap<>();
        return new ArenaMarker((Location) args.get("location"), (String) args.get("type"), metadata);
    }
}
