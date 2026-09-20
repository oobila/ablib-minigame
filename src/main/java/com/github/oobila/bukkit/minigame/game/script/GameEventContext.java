package com.github.oobila.bukkit.minigame.game.script;

import java.util.HashMap;
import java.util.Map;

/**
 * The payload passed to triggers/actions when the engine fires an event. {@code key}
 * identifies which event this is (a {@link TriggerBinding} only reacts to a matching key);
 * {@code data} carries whatever is relevant to that event, e.g. the victim/killer of a
 * {@link #DEATH} event. This is transient, in-memory only - never persisted.
 */
public class GameEventContext {

    public static final String ENTER = "enter";
    public static final String TICK = "tick";
    public static final String DEATH = "death";
    public static final String JOIN = "join";
    public static final String LEAVE = "leave";

    private final String key;
    private final Map<String, Object> data;

    public GameEventContext(String key) {
        this(key, new HashMap<>());
    }

    public GameEventContext(String key, Map<String, Object> data) {
        this.key = key;
        this.data = data != null ? data : new HashMap<>();
    }

    public String getKey() {
        return key;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String field) {
        return (T) data.get(field);
    }
}
