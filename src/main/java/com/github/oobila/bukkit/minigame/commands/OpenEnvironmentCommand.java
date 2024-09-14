package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.command.arguments.StringArg;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.minigame.environments.EnvironmentStatus;
import com.github.oobila.bukkit.persistence.caches.DataCache;

import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

public class OpenEnvironmentCommand extends Command {

    public OpenEnvironmentCommand(DataCache<ABID, Environment> dataCache) {
        super("open", "");
        aliases("o");
        StringArg nameArg = new StringArg("name");
        nameArg.suggestionCallable((player, s) -> CommandUtils.getEnvironmentNames(dataCache));
        arg(nameArg);
        combinedCommand((player, command, s, args) -> {
            Environment environment = CommandUtils.getEnvironment(args[0], player, dataCache);
            if (environment == null) {
                return;
            }

            EnvironmentStatus status = environment.getStatus();
            if (status.equals(EnvironmentStatus.OPEN)) {
                log(Level.INFO, "environment is already open");
                message("environment is already open", player);
            } else if (status.equals(EnvironmentStatus.CLOSED)) {
                environment.open();
            } else {
                log(Level.INFO, "environment is in a moving state");
                message("environment is in a moving state", player);
            }
        });
    }
}
