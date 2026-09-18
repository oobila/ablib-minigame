package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

/**
 * Parent for "remove a feature from an environment" commands, e.g. {@code /environment feature remove portal}.
 * Each feature type (currently just {@link EnvironmentFeatureRemovePortal}) is registered here as its
 * own subcommand, so adding a new feature type means adding one more subCommand call.
 */
public class EnvironmentFeatureRemove extends Command {

    public EnvironmentFeatureRemove(ReadAndWriteCache<ABID, Environment> cache) {
        super("remove", "remove a feature from this environment");
        aliases("r");
        subCommand(new EnvironmentFeatureRemovePortal(cache));
    }
}
