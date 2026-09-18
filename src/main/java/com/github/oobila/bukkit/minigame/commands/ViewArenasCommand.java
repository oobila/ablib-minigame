package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.gui.ViewArenasGui;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.plugin.Plugin;

public class ViewArenasCommand extends Command {

    public ViewArenasCommand(Plugin plugin, ReadAndWriteCache<ABID, Arena> cache) {
        super("view", "view all arenas");
        aliases("v");
        command((player, command, s, strings) ->
            new ViewArenasGui(plugin, player, cache).open()
        );
    }
}
