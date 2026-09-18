package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class MinigameCommand extends Command {

    public MinigameCommand(
            Plugin plugin,
            ReadAndWriteCache<ABID, Environment> environmentCache,
            ReadAndWriteCache<ABID, Arena> arenaCache
    ) {
        super("minigame", "manage minigames");
        subCommand(new EnvironmentCommand(plugin, environmentCache));
        subCommand(new ArenaCommand(plugin, arenaCache));
        subCommand(new GameCommand());
        subCommand(new AreaSelectionToolCommand());
    }
}
