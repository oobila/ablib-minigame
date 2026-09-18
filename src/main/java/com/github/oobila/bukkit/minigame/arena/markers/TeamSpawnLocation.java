package com.github.oobila.bukkit.minigame.arena.markers;

import com.github.oobila.bukkit.common.utils.model.BlockColor;
import com.github.oobila.bukkit.minigame.arena.ArenaMarker;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@SerializableAs("TeamSpawnLocation")
public class TeamSpawnLocation extends ArenaMarker {

    static {
        ConfigurationSerialization.registerClass(TeamSpawnLocation.class);
    }

    private final boolean canSpawnHere = true;
    private final BlockColor teamColour;

    public TeamSpawnLocation(Location location, BlockColor teamColour) {
        this(location, teamColour, new HashMap<>());
    }

    private TeamSpawnLocation(Location location, BlockColor teamColour, Map<String, String> metadata) {
        super(location, metadata);
        this.teamColour = teamColour;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("teamColour", teamColour.name());
        return map;
    }

    @NotNull
    public static TeamSpawnLocation deserialize(@NotNull Map<String, Object> args) {
        return new TeamSpawnLocation(
                extractLocation(args),
                BlockColor.valueOf((String) args.get("teamColour")),
                extractMetadata(args)
        );
    }
}
