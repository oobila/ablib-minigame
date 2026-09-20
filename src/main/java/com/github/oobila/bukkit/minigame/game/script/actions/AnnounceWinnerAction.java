package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.team.Team;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Attributes: {@code target} - "player" (default) announces the highest-scoring player,
 * "team" announces the highest-scoring team.
 */
public class AnnounceWinnerAction implements GameAction {

    public static final String KEY = "announce-winner";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        Message message = buildMessage(game, attributes);
        for (OfflinePlayer offlinePlayer : game.getPlayers()) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                message.send(player);
            }
        }
        return 0;
    }

    private Message buildMessage(Game game, GameActionAttributes attributes) {
        if (attributes.getString("target", "player").equals("team") && !game.getTeams().isEmpty()) {
            Team winner = game.getLeadingTeam();
            return winner != null
                    ? new Message("{0} team wins!").arg(winner.getTeamConfig().getName())
                    : new Message("no winner");
        }
        OfflinePlayer winner = game.getLeadingPlayer();
        return winner != null ? new Message("{0} wins!").arg(winner) : new Message("no winner");
    }

}
