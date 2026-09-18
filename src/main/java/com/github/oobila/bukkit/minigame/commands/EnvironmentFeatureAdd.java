package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

/**
 * Parent for "add a feature to an environment" commands, e.g. {@code /environment feature add portal}.
 * Each feature type (currently just {@link EnvironmentFeatureAddPortal}) is registered here as its
 * own subcommand, so adding a new feature type means adding one more subCommand call.
 */
public class EnvironmentFeatureAdd extends Command {

    public EnvironmentFeatureAdd(ReadAndWriteCache<ABID, Environment> cache) {
        super("add", "add a feature to this environment");
        aliases("a");
        subCommand(new EnvironmentFeatureAddPortal(cache));
    }
}
