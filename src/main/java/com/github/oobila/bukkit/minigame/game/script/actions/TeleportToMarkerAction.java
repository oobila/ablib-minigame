package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.arena.ArenaMarker;
import com.github.oobila.bukkit.minigame.arena.markers.PostGameLobbySpawn;
import com.github.oobila.bukkit.minigame.arena.markers.PreGameLobbySpawn;
import com.github.oobila.bukkit.minigame.arena.markers.SoloSpawnLocation;
import com.github.oobila.bukkit.minigame.arena.markers.TeamSpawnLocation;
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
 * Attributes: {@code marker} - which marker type to send players to: "solo-spawn" (default,
 * round-robins players across every {@link SoloSpawnLocation}), "team-spawn" (each team goes to
 * the {@link TeamSpawnLocation} matching its configured colour), "pre-game-lobby" or
 * "post-game-lobby" (everyone goes to the same lobby marker).
 */
public class TeleportToMarkerAction implements GameAction {

    public static final String KEY = "teleport-to-marker";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        Arena arena = game.getArena();
        if (arena == null) {
            return 0;
        }
        switch (attributes.getString("marker", "solo-spawn")) {
            case "team-spawn" -> teleportTeams(game, arena);
            case "pre-game-lobby" -> teleportAll(game, arena.getMarkers(PreGameLobbySpawn.class));
            case "post-game-lobby" -> teleportAll(game, arena.getMarkers(PostGameLobbySpawn.class));
            default -> teleportRoundRobin(game.getPlayers(), arena.getMarkers(SoloSpawnLocation.class));
        }
        return 0;
    }

    private void teleportTeams(Game game, Arena arena) {
        List<TeamSpawnLocation> spawns = arena.getMarkers(TeamSpawnLocation.class);
        for (Team team : game.getTeams()) {
            spawns.stream()
                    .filter(spawn -> spawn.getTeamColour().equals(team.getTeamConfig().getTeamColor()))
                    .findFirst()
                    .ifPresent(spawn -> team.forEachPlayer(player -> teleport(player, spawn.getLocation())));
        }
    }

    private void teleportAll(Game game, List<? extends ArenaMarker> markers) {
        if (markers.isEmpty()) {
            return;
        }
        Location location = markers.get(0).getLocation();
        game.getPlayers().forEach(player -> teleport(player, location));
    }

    private void teleportRoundRobin(List<OfflinePlayer> players, List<? extends ArenaMarker> markers) {
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
