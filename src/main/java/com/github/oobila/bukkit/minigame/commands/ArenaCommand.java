package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class ArenaCommand extends Command {

    public ArenaCommand(Plugin plugin, ReadAndWriteCache<ABID, Arena> cache) {
        super("arena", "manage minigame arenas");
        aliases("a");
        subCommand(new CreateArenaCommand(cache));
        subCommand(new RemoveArenaCommand(cache));
        subCommand(new ViewArenasCommand(plugin, cache));
    }
}
