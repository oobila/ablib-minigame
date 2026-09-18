package com.github.oobila.bukkit.minigame.arena.markers;

import com.github.oobila.bukkit.minigame.arena.ArenaMarker;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@SerializableAs("SoloSpawnLocation")
public class SoloSpawnLocation extends ArenaMarker {

    static {
        ConfigurationSerialization.registerClass(SoloSpawnLocation.class);
    }

    private final boolean canSpawnHere = true;

    public SoloSpawnLocation(Location location) {
        super(location);
    }

    private SoloSpawnLocation(Location location, Map<String, String> metadata) {
        super(location, metadata);
    }

    // No extra fields to serialize beyond ArenaMarker's, so ArenaMarker#serialize()
    // is inherited as-is; deserialize() below is still required per-class by Bukkit's
    // ConfigurationSerialization contract.
    @NotNull
    public static SoloSpawnLocation deserialize(@NotNull Map<String, Object> args) {
        return new SoloSpawnLocation(extractLocation(args), extractMetadata(args));
    }
}
