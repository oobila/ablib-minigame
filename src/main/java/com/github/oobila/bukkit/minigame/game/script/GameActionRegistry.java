package com.github.oobila.bukkit.minigame.game.script;

import java.util.HashMap;
import java.util.Map;

public final class GameActionRegistry {

    private static final Map<String, GameAction> ACTIONS = new HashMap<>();

    private GameActionRegistry() {
    }

    public static void register(String key, GameAction action) {
        ACTIONS.put(key, action);
    }

    public static GameAction get(String key) {
        GameAction action = ACTIONS.get(key);
        if (action == null) {
            throw new IllegalArgumentException("no game action registered with key: " + key);
        }
        return action;
    }

}
