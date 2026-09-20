package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import org.bukkit.OfflinePlayer;

/**
 * Attributes: {@code who} - the context key naming the player to eliminate (default "player").
 */
public class EliminateAction implements GameAction {

    public static final String KEY = "eliminate";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        OfflinePlayer who = context.get(attributes.getString("who", "player"));
        if (who != null) {
            game.eliminate(who);
        }
        return 0;
    }

}
