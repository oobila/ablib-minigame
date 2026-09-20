package com.github.oobila.bukkit.minigame.game.script.triggers;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.game.script.GameTrigger;

/**
 * Attributes: {@code seconds} - how long the current phase must have been active (default 60).
 * Used for a lobby countdown ("after 15s in PRE_GAME_LOBBY, start") and for time-limit win
 * conditions ("after 10 minutes in IN_PROGRESS, end the game").
 */
public class TimeElapsedTrigger implements GameTrigger {

    public static final String KEY = "time-elapsed";

    @Override
    public boolean test(Game game, GameActionAttributes attributes, GameEventContext context) {
        long seconds = attributes.getInt("seconds", 60);
        return game.getMillisSincePhaseEntered() >= seconds * 1000L;
    }

}
