package com.github.oobila.bukkit.minigame.arena;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class ArenaMarker implements ConfigurationSerializable {

    private final Location location;
    private final Map<String, String> metadata;

    protected ArenaMarker(Location location) {
        this(location, new HashMap<>());
    }

    protected ArenaMarker(Location location, Map<String, String> metadata) {
        this.location = location;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * Serializes the fields common to every {@link ArenaMarker} subtype. Concrete
     * subtypes that add their own fields (e.g. {@code TeamSpawnLocation}) must
     * override this, call {@code super.serialize()} and add those fields to the
     * returned (mutable) map before returning it. A subtype with no extra fields
     * (e.g. {@code SoloSpawnLocation}) can rely on this implementation as-is.
     */
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("metadata", metadata);
        return map;
    }

    @NotNull
    protected static Location extractLocation(@NotNull Map<String, Object> args) {
        return (Location) args.get("location");
    }

    @NotNull
    @SuppressWarnings("unchecked")
    protected static Map<String, String> extractMetadata(@NotNull Map<String, Object> args) {
        Object raw = args.get("metadata");
        return raw != null ? (Map<String, String>) raw : new HashMap<>();
    }
}
