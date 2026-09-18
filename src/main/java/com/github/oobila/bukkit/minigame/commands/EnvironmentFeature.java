package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

/**
 * Root of the {@code /environment feature} command tree, which adds and removes the individual
 * pieces (currently just portals) attached to an environment, e.g. {@code /environment feature add portal}
 * and {@code /environment feature remove portal}.
 */
public class EnvironmentFeature extends Command {

    public EnvironmentFeature(ReadAndWriteCache<ABID, Environment> cache) {
        super("feature", "manage the features attached to this environment");
        aliases("f");
        subCommand(new EnvironmentFeatureAdd(cache));
        subCommand(new EnvironmentFeatureRemove(cache));
    }
}
