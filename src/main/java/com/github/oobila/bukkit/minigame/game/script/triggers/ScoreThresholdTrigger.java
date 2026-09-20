package com.github.oobila.bukkit.minigame.game.script.triggers;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.game.script.GameTrigger;

/**
 * Attributes: {@code threshold} - the score to reach (default 1); {@code target} - "player"
 * (default) checks each player's individual score, "team" checks each team's aggregate score.
 * Doubles as the win condition for score-based modes (FFA "first to N kills", TDM "first team
 * to N kills") and for a finish line (checkpoint race: threshold = number of checkpoints).
 */
public class ScoreThresholdTrigger implements GameTrigger {

    public static final String KEY = "score-threshold";

    @Override
    public boolean test(Game game, GameActionAttributes attributes, GameEventContext context) {
        int threshold = attributes.getInt("threshold", 1);
        if (attributes.getString("target", "player").equals("team")) {
            return game.getTeams().stream().anyMatch(team -> game.getTeamScore(team) >= threshold);
        }
        return game.getPlayers().stream().anyMatch(player -> game.getScore(player) >= threshold);
    }

}
