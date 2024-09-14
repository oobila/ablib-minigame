package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.minigame.environments.EnvironmentStatus;
import com.github.oobila.bukkit.persistence.caches.DataCache;

import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

public class RemoveEnvironmentCommand extends Command {

    public RemoveEnvironmentCommand(DataCache<ABID, Environment> dataCache) {
        super("remove", "");
        aliases("delete", "r");
        arg("id");
        combinedCommand((player, command, s, args) -> {
            ABID abid = ABID.fromString(args[0]);
            if (dataCache.contains(abid)) {
                Environment environment = dataCache.get(abid);
                if (environment.getStatus().equals(EnvironmentStatus.CLOSED)) {
                    dataCache.remove(abid);
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
