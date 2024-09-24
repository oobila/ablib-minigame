package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.command.arguments.StringArg;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.minigame.environments.EnvironmentStatus;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

public class RemoveEnvironmentCommand extends Command {

    public RemoveEnvironmentCommand(ReadAndWriteCache<ABID, Environment> cache) {
        super("remove", "");
        aliases("delete", "r");
        StringArg nameArg = new StringArg("name");
        nameArg.suggestionCallable((player, s) -> CommandUtils.getEnvironmentNames(cache));
        arg(nameArg);
        combinedCommand((player, command, s, args) -> {
            Environment environment = CommandUtils.getEnvironment(args[0], player, cache);
            if (environment != null) {
                if (environment.getStatus().equals(EnvironmentStatus.CLOSED)) {
                    cache.remove(environment.getId());
                } else {
                    log(Level.INFO, "could not remove environment as it is still in use");
                    message("could not remove environment as it is still in use", player);
                }
            } else {
                log(Level.INFO, "environment could not be found");
                message("environment could not be found", player);
            }
        });
    }
}
