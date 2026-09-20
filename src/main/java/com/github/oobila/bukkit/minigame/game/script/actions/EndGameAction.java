package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.GameStatus;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;

public class EndGameAction implements GameAction {

    public static final String KEY = "end-game";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        game.transitionTo(GameStatus.POST_GAME_LOBBY);
        return 0;
    }

}
