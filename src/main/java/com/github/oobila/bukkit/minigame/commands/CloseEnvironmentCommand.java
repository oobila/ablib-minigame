package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.minigame.environments.EnvironmentStatus;
import com.github.oobila.bukkit.persistence.caches.DataCache;

import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

public class CloseEnvironmentCommand extends Command {

    public CloseEnvironmentCommand(DataCache<ABID, Environment> dataCache) {
        super("cloe", "");
        aliases("c");
        arg("id");
        combinedCommand((player, command, s, args) -> {
            Environment environment = CommandUtils.getEnvironment(args[0], player, dataCache);
            if (environment == null) {
                return;
            }

            EnvironmentStatus status = environment.getStatus();
            if (status.equals(EnvironmentStatus.CLOSED)) {
                log(Level.INFO, "environment is already closed");
                message("environment is already closed", player);
            } else if (status.equals(EnvironmentStatus.OPEN)) {
                environment.close();
            } else {
                log(Level.INFO, "environment is in a moving state");
                message("environment is in a moving state", player);
            }
        });
    }
}
