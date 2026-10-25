package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.arena.ArenaMarker;
import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.team.Team;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Attributes: {@code marker} - which marker type to send players to: {@link
 * ArenaMarker#SOLO_SPAWN} (default, round-robins players across every marker of that type),
 * {@link ArenaMarker#TEAM_SPAWN} (each team goes to whichever marker's {@code teamColour}
 * metadata matches its configured colour), {@link ArenaMarker#PRE_GAME_LOBBY} or {@link
 * ArenaMarker#POST_GAME_LOBBY} (everyone goes to the same lobby marker). Any other string is
 * looked up as-is and round-robinned like {@code solo-spawn} - a host plugin's own marker type
 * works here with no change needed in this library.
 */
public class TeleportToMarkerAction implements GameAction {

    public static final String KEY = "teleport-to-marker";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        Arena arena = game.getArena();
        if (arena == null) {
            return 0;
        }
        String markerType = attributes.getString("marker", ArenaMarker.SOLO_SPAWN);
        switch (markerType) {
            case ArenaMarker.TEAM_SPAWN -> teleportTeams(game, arena);
            case ArenaMarker.PRE_GAME_LOBBY, ArenaMarker.POST_GAME_LOBBY ->
                    teleportAll(game, arena.getMarkers(markerType));
            default -> teleportRoundRobin(game.getPlayers(), arena.getMarkers(markerType));
        }
        return 0;
    }

    private void teleportTeams(Game game, Arena arena) {
        List<ArenaMarker> spawns = arena.getMarkers(ArenaMarker.TEAM_SPAWN);
        for (Team team : game.getTeams()) {
            String teamColour = team.getTeamConfig().getTeamColor().name();
            spawns.stream()
                    .filter(spawn -> teamColour.equals(spawn.getMetadata().get("teamColour")))
                    .findFirst()
                    .ifPresent(spawn -> team.forEachPlayer(player -> teleport(player, spawn.getLocation())));
        }
    }

    private void teleportAll(Game game, List<ArenaMarker> markers) {
        if (markers.isEmpty()) {
            return;
        }
        Location location = markers.get(0).getLocation();
        game.getPlayers().forEach(player -> teleport(player, location));
    }

    private void teleportRoundRobin(List<OfflinePlayer> players, List<ArenaMarker> markers) {
        if (markers.isEmpty()) {
            return;
        }
        for (int i = 0; i < players.size(); i++) {
            teleport(players.get(i), markers.get(i % markers.size()).getLocation());
        }
    }

    private void teleport(OfflinePlayer offlinePlayer, Location location) {
        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            player.teleport(location);
        }
    }

}
