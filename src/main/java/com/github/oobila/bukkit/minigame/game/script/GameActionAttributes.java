package com.github.oobila.bukkit.minigame.game.script;

import java.util.HashMap;
import java.util.Map;

/**
 * A key/value config bag for a bound {@link GameAction}/{@link GameTrigger}, e.g.
 * {@code threshold: 10} on a score-threshold trigger. Values are stored as raw {@code Object}
 * rather than {@code String} because a hand-authored YAML file will often leave numbers/booleans
 * unquoted, and SnakeYAML then hands them back as real Integer/Boolean instances rather than
 * String - the typed getters below coerce whatever comes back into the requested type.
 * <p>
 * This is a plain nested map on {@link ActionBinding}/{@link TriggerBinding} rather than its own
 * {@code ConfigurationSerializable}, so it needs no separate registration and doesn't add an
 * extra layer of {@code ==: GameActionAttributes} nesting to hand-written script files.
 */
public class GameActionAttributes {

    private final Map<String, Object> attributes;

    public GameActionAttributes() {
        this(new HashMap<>());
    }

    public GameActionAttributes(Map<String, Object> attributes) {
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getString(String key, String defaultValue) {
        Object value = attributes.get(key);
        return value != null ? String.valueOf(value) : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        Object value = attributes.get(key);
        return value != null ? Integer.parseInt(String.valueOf(value)) : defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        Object value = attributes.get(key);
        return value != null ? Double.parseDouble(String.valueOf(value)) : defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = attributes.get(key);
        return value != null ? Boolean.parseBoolean(String.valueOf(value)) : defaultValue;
    }
}
