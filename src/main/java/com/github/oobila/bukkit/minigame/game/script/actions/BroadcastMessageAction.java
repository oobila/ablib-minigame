package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Attributes: {@code message} - the literal text sent to every current participant.
 */
public class BroadcastMessageAction implements GameAction {

    public static final String KEY = "broadcast-message";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        String text = attributes.getString("message", "");
        for (OfflinePlayer offlinePlayer : game.getPlayers()) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                Message.send(player, text);
            }
        }
        return 0;
    }

}
