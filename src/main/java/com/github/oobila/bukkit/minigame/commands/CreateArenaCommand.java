package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.items.AreaSelectionTool;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.Location;

import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

public class CreateArenaCommand extends Command {

    public CreateArenaCommand(ReadAndWriteCache<ABID, Arena> cache) {
        super("create", "create a game arena from your current selection");
        aliases("c");
        // player-only: the arena's min/max locations come from the player's current
        // AreaSelectionTool selection
        command((player, command, s, args) -> {
            Location min = AreaSelectionTool.getMinSelection(player);
            Location max = AreaSelectionTool.getMaxSelection(player);
            if (min == null || max == null) {
                message("you must select an area with the area selection tool before creating an arena", player);
                return;
            }
            try {
                Arena arena = new Arena(min, max);
                cache.putValue(arena.getId(), arena);
                message(new Message("created arena '{}'", arena.getId().toString()), player);
            } catch (ABIDException e) {
                message("failed to create arena, please try again", player);
                log(Level.WARNING, "failed to create arena");
                log(Level.WARNING, e);
            }
        });
    }
}
