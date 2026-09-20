package com.github.oobila.bukkit.minigame.game.script.triggers;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.game.script.GameTrigger;

/**
 * Unconditionally fires its bound actions - for events like "death" where the point is simply
 * reacting to the event itself, not gating on some extra condition.
 */
public class AlwaysTrigger implements GameTrigger {

    public static final String KEY = "always";

    @Override
    public boolean test(Game game, GameActionAttributes attributes, GameEventContext context) {
        return true;
    }

}
