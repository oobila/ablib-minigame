package com.github.oobila.bukkit.minigame.game.script.triggers;

import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.arena.markers.CheckpointMarker;
import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.game.script.GameTrigger;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;

/**
 * Drives a checkpoint race: a player's score is how many checkpoints they've passed, and thus
 * which {@link CheckpointMarker} (ordered by {@code order}) they need to reach next. Attributes:
 * {@code radius} - how close a player needs to be to count as "reached" (default 2.0 blocks).
 * Advancing a player's score is a side effect of testing this trigger, not just its result.
 */
public class CheckpointProximityTrigger implements GameTrigger {

    public static final String KEY = "checkpoint-proximity";

    @Override
    public boolean test(Game game, GameActionAttributes attributes, GameEventContext context) {
        Arena arena = game.getArena();
        if (arena == null) {
            return false;
        }
        List<CheckpointMarker> checkpoints = arena.getMarkers(CheckpointMarker.class).stream()
                .sorted(Comparator.comparingInt(CheckpointMarker::getOrder))
                .toList();
        if (checkpoints.isEmpty()) {
            return false;
        }
        double radius = attributes.getDouble("radius", 2.0);
        boolean anyReached = false;
        for (OfflinePlayer offlinePlayer : game.getActivePlayers()) {
            int next = game.getScore(offlinePlayer);
            if (next >= checkpoints.size()) {
                continue;
            }
            Player player = offlinePlayer.getPlayer();
            if (player == null) {
                continue;
            }
            Location checkpoint = checkpoints.get(next).getLocation();
            if (isWithin(player.getLocation(), checkpoint, radius)) {
                game.addScore(offlinePlayer, 1);
                anyReached = true;
            }
        }
        return anyReached;
    }

    private boolean isWithin(Location a, Location b, double radius) {
        return a.getWorld() != null && a.getWorld().equals(b.getWorld()) && a.distanceSquared(b) <= radius * radius;
    }

}
