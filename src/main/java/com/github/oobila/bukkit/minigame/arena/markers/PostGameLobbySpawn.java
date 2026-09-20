package com.github.oobila.bukkit.minigame.arena.markers;

import com.github.oobila.bukkit.minigame.arena.ArenaMarker;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@SerializableAs("PostGameLobbySpawn")
public class PostGameLobbySpawn extends ArenaMarker {

    static {
        ConfigurationSerialization.registerClass(PostGameLobbySpawn.class);
    }

    public PostGameLobbySpawn(Location location) {
        super(location);
    }

    private PostGameLobbySpawn(Location location, Map<String, String> metadata) {
        super(location, metadata);
    }

    @NotNull
    public static PostGameLobbySpawn deserialize(@NotNull Map<String, Object> args) {
        return new PostGameLobbySpawn(extractLocation(args), extractMetadata(args));
    }
}
