package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.arena.ArenaStatus;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import static com.github.oobila.bukkit.common.ABCommon.message;

public class RemoveArenaCommand extends Command {

    public RemoveArenaCommand(ReadAndWriteCache<ABID, Arena> cache) {
        super("remove", "remove the arena you are standing in");
        aliases("delete", "r");
        // player-only: the arena to remove is whichever cached arena's bounds contain
        // the player's current location
        command((player, command, s, args) -> {
            Arena arena = null;
            for (CacheItem<ABID, Arena> cacheItem : cache.values()) {
                if (cacheItem.getData().contains(player.getLocation())) {
                    arena = cacheItem.getData();
                    break;
                }
            }
            if (arena == null) {
                message("you must be standing inside an arena to remove it", player);
                return;
            }
            if (!arena.getStatus().equals(ArenaStatus.READY)) {
                message("this arena must be in a ready status to be removed", player);
                return;
            }
            cache.remove(arena.getId());
            message("arena removed", player);
        });
    }
}
