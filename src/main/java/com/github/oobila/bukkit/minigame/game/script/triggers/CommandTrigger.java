package com.github.oobila.bukkit.minigame.game.script.triggers;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.game.script.GameTrigger;

/**
 * An alternative to {@link TimeElapsedTrigger} for phases that should advance on an operator's
 * say-so rather than a fixed countdown - e.g. a pre-game lobby that waits until an admin decides
 * everyone's ready, instead of always waiting out a fixed number of seconds. Fires on a
 * {@link GameEventContext#COMMAND} event whose {@code name} matches this binding's {@code name}
 * attribute (both default to "" so a script that doesn't care about naming can omit it on both
 * sides). Something outside the engine - a plugin command - is what actually fires that event;
 * this trigger only decides whether a fired command event is the one this binding is listening for.
 */
public class CommandTrigger implements GameTrigger {

    public static final String KEY = "command";

    @Override
    public boolean test(Game game, GameActionAttributes attributes, GameEventContext context) {
        String expected = attributes.getString("name", "");
        Object actual = context.get("name");
        return expected.equals(actual != null ? actual : "");
    }

}
