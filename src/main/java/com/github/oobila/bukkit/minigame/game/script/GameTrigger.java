package com.github.oobila.bukkit.minigame.game.script;

import com.github.oobila.bukkit.minigame.game.Game;

public interface GameTrigger {

    // some triggers (e.g. checkpoint proximity) advance per-player progress as a side effect
    // of being tested, not just report a yes/no - that's why this takes the live Game, not a snapshot
    boolean test(Game game, GameActionAttributes attributes, GameEventContext context);

}
