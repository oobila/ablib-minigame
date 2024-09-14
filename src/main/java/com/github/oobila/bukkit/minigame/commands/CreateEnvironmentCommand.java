package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.DataCache;

import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

public class CreateEnvironmentCommand extends Command {

    public CreateEnvironmentCommand(DataCache<ABID, Environment> dataCache) {
        super("create", "");
        arg("name");
        combinedCommand((player, command, s, args) -> {
            try {
                Environment environment = new Environment(args[0]);
                dataCache.put(environment.getId(), environment);
            } catch (ABIDException e) {
                message("failed to create environment, please try again", player);
                log(Level.WARNING, "failed to create environment");
                log(Level.WARNING, e);
            }
        });
    }
}
