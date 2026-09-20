package com.github.oobila.bukkit.minigame.game.script;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@SerializableAs("GameActionAttributes")
public class GameActionAttributes implements ConfigurationSerializable {

    private final Map<String, String> attributes;

    public GameActionAttributes() {
        this(new HashMap<>());
    }

    private GameActionAttributes(Map<String, String> attributes) {
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("attributes", new HashMap<>(attributes));
        return map;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static GameActionAttributes deserialize(@NotNull Map<String, Object> args) {
        Object raw = args.get("attributes");
        return new GameActionAttributes(raw instanceof Map<?, ?> ? (Map<String, String>) raw : null);
    }
}
