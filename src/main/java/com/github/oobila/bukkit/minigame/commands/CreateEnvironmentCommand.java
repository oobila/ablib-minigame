package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

public class CreateEnvironmentCommand extends Command {

    public CreateEnvironmentCommand(ReadAndWriteCache<ABID, Environment> cache) {
        super("create", "");
        arg("name");
        combinedCommand((player, command, s, args) -> {
            try {
                for (CacheItem<ABID, Environment> cacheItem : cache.values()) {
                    if (cacheItem.getData().getName().equalsIgnoreCase(args[0])) {
                        message("an environment already exists with this name", player);
                        return;
                    }
                }
                Environment environment = new Environment(args[0]);
                cache.putValue(environment.getId(), environment);
            } catch (ABIDException e) {
                message("failed to create environment, please try again", player);
                log(Level.WARNING, "failed to create environment");
                log(Level.WARNING, e);
            }
        });
    }
}
