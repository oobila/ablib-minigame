package com.github.oobila.bukkit.minigame.game.script;

import java.util.HashMap;
import java.util.Map;

public final class GameTriggerRegistry {

    private static final Map<String, GameTrigger> TRIGGERS = new HashMap<>();

    private GameTriggerRegistry() {
    }

    public static void register(String key, GameTrigger trigger) {
        TRIGGERS.put(key, trigger);
    }

    public static GameTrigger get(String key) {
        GameTrigger trigger = TRIGGERS.get(key);
        if (trigger == null) {
            throw new IllegalArgumentException("no game trigger registered with key: " + key);
        }
        return trigger;
    }

}
