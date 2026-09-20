package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.team.Team;
import org.bukkit.OfflinePlayer;

/**
 * Attributes: {@code who} - the context key naming the player to award (default "player");
 * {@code amount} - score to add (default 1); {@code friendlyFire} - if false, skips the award
 * when {@code who} and the context key named by {@code against} are on the same team.
 */
public class AwardScoreAction implements GameAction {

    public static final String KEY = "award-score";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        OfflinePlayer who = context.get(attributes.getString("who", "player"));
        if (who == null) {
            return 0;
        }
        if (!attributes.getBoolean("friendlyFire", true)) {
            Object against = context.get(attributes.getString("against", "against"));
            if (against instanceof OfflinePlayer otherPlayer) {
                Team whoTeam = game.getTeam(who);
                if (whoTeam != null && whoTeam.equals(game.getTeam(otherPlayer))) {
                    return 0;
                }
            }
        }
        game.addScore(who, attributes.getInt("amount", 1));
        return 0;
    }

}
