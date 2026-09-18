package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.command.arguments.StringArg;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

import static com.github.oobila.bukkit.common.ABCommon.message;

public class SetExitLocationCommand extends Command {

    public SetExitLocationCommand(ReadAndWriteCache<ABID, Environment> cache) {
        super("exit", "set the exit location for this environment");
        aliases("e");
        StringArg nameArg = new StringArg("name");
        nameArg.suggestionCallable((player, s) -> CommandUtils.getEnvironmentNames(cache));
        arg(nameArg);
        // player-only: the exit location is taken from where the command is run
        command((player, command, s, args) -> {
            Environment environment = CommandUtils.getEnvironment(args[0], player, cache);
            if (environment == null) {
                return;
            }
            environment.setExitLocation(player.getLocation());
            message(new Message("exit location for '{}' set to your current location", args[0]), player);
        });
    }
}
