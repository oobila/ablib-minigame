package com.github.oobila.bukkit.minigame.arena.markers;

import com.github.oobila.bukkit.minigame.arena.ArenaMarker;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@SerializableAs("CheckpointMarker")
public class CheckpointMarker extends ArenaMarker {

    static {
        ConfigurationSerialization.registerClass(CheckpointMarker.class);
    }

    // 0-based position of this checkpoint in the race; a player's score is how many
    // checkpoints they've passed, so it also doubles as "which order comes next for them"
    private final int order;

    public CheckpointMarker(Location location, int order) {
        this(location, order, new HashMap<>());
    }

    private CheckpointMarker(Location location, int order, Map<String, String> metadata) {
        super(location, metadata);
        this.order = order;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("order", order);
        return map;
    }

    @NotNull
    public static CheckpointMarker deserialize(@NotNull Map<String, Object> args) {
        return new CheckpointMarker(
                extractLocation(args),
                (Integer) args.get("order"),
                extractMetadata(args)
        );
    }
}
