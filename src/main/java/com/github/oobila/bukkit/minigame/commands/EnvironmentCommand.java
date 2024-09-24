package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class EnvironmentCommand extends Command {

    public EnvironmentCommand(Plugin plugin, ReadAndWriteCache<ABID, Environment> dataCache) {
        super("environment", "");
        subCommand(new CreateEnvironmentCommand(dataCache));
        subCommand(new OpenEnvironmentCommand(dataCache));
        subCommand(new CloseEnvironmentCommand(dataCache));
        subCommand(new RemoveEnvironmentCommand(dataCache));
        subCommand(new ViewEnvironmentsCommand(plugin, dataCache));
    }
}
