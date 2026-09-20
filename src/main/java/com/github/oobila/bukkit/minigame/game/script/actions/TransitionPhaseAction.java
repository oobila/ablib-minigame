package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.GameStatus;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;

/**
 * Attributes: {@code phase} - the {@link GameStatus} name to transition into.
 */
public class TransitionPhaseAction implements GameAction {

    public static final String KEY = "transition-phase";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        game.transitionTo(GameStatus.valueOf(attributes.getString("phase", GameStatus.IN_PROGRESS.name())));
        return 0;
    }

}
