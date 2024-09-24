package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.minigame.gui.ViewEnvironmentsGui;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.plugin.Plugin;

public class ViewEnvironmentsCommand extends Command {

    public ViewEnvironmentsCommand(Plugin plugin, ReadAndWriteCache<ABID, Environment> cache) {
        super("view", "");
        aliases("v");
        command((player, command, s, strings) ->
            new ViewEnvironmentsGui(plugin, player, cache).open()
        );
    }
}
