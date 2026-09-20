package com.github.oobila.bukkit.minigame.arena.markers;

import com.github.oobila.bukkit.minigame.arena.ArenaMarker;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@SerializableAs("PreGameLobbySpawn")
public class PreGameLobbySpawn extends ArenaMarker {

    static {
        ConfigurationSerialization.registerClass(PreGameLobbySpawn.class);
    }

    public PreGameLobbySpawn(Location location) {
        super(location);
    }

    private PreGameLobbySpawn(Location location, Map<String, String> metadata) {
        super(location, metadata);
    }

    @NotNull
    public static PreGameLobbySpawn deserialize(@NotNull Map<String, Object> args) {
        return new PreGameLobbySpawn(extractLocation(args), extractMetadata(args));
    }
}
