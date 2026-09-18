package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.command.arguments.StringArg;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.minigame.environments.Portal;
import com.github.oobila.bukkit.minigame.items.AreaSelectionTool;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Set;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

public class EnvironmentFeatureAddPortal extends Command {

    public EnvironmentFeatureAddPortal(ReadAndWriteCache<ABID, Environment> cache) {
        super("portal", "add a portal to this environment");
        aliases("p");
        StringArg nameArg = new StringArg("name");
        nameArg.suggestionCallable((player, s) -> CommandUtils.getEnvironmentNames(cache));
        arg(nameArg);
        // player-only: the portal's min/max locations come from the player's current
        // AreaSelectionTool selection
        command((player, command, s, args) -> {
            Environment environment = CommandUtils.getEnvironment(args[0], player, cache);
            if (environment == null) {
                return;
            }
            Location min = AreaSelectionTool.getMinSelection(player);
            Location max = AreaSelectionTool.getMaxSelection(player);
            if (min == null || max == null) {
                message("you must select an area with the area selection tool before creating a portal", player);
                return;
            }
            if (!containsAnyMaterial(min, max, Material.END_PORTAL, Material.END_PORTAL_FRAME)) {
                message("your selection must contain an end portal or end portal frame", player);
                return;
            }
            try {
                Portal portal = new Portal(min, max);
                environment.addPortal(portal);
                message(new Message("added portal '{}' to '{}'", portal.getId().toString(), args[0]), player);
            } catch (ABIDException e) {
                message("failed to create portal, please try again", player);
                log(Level.WARNING, "failed to create portal");
                log(Level.WARNING, e);
            }
        });
    }

    private static boolean containsAnyMaterial(Location min, Location max, Material... materials) {
        Set<Material> targets = Set.of(materials);
        World world = min.getWorld();

        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (targets.contains(world.getBlockAt(x, y, z).getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
