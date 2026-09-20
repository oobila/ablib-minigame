package com.github.oobila.bukkit.minigame.game.script.triggers;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.game.script.GameTrigger;

/**
 * Attributes: {@code target} - "player" (default) fires once only one non-eliminated player
 * remains out of more than one starter; "team" does the same at the team level.
 */
public class LastEntityStandingTrigger implements GameTrigger {

    public static final String KEY = "last-entity-standing";

    @Override
    public boolean test(Game game, GameActionAttributes attributes, GameEventContext context) {
        if (attributes.getString("target", "player").equals("team")) {
            return game.getTeams().size() > 1 && game.getActiveTeams().size() <= 1;
        }
        return game.getPlayers().size() > 1 && game.getActivePlayers().size() <= 1;
    }

}
