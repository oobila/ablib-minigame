package com.github.oobila.bukkit.minigame.game.script;

import com.github.oobila.bukkit.minigame.game.Game;

public interface GameAction {


    // the return value depends on the result of the game action. Most actions will only have one result
    // so where this doesn't branch out into multiple options, it should stay as 0
    int run(Game game, GameActionAttributes attributes);

}