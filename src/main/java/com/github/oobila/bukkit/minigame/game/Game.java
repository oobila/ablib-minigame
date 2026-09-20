package com.github.oobila.bukkit.minigame.game;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.team.Team;
import com.github.oobila.bukkit.minigame.team.TeamAssignment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@Getter
public abstract class Game implements ConfigurationSerializable {

    private final ABID id;
    private final String name;
    @Setter
    private Arena arena;
    @Setter(AccessLevel.PROTECTED)
    private GameStatus status = GameStatus.READY;
    private final List<Team> teams = new ArrayList<>();
    private final List<OfflinePlayer> players = new ArrayList<>();
    private GameConfig gameConfig;

    protected Game(String name) throws ABIDException {
        this.id = new ABID();
        this.name = name;
    }

    protected Game(ABID id, String name, Arena arena) {
        this.id = id;
        this.name = name;
        this.arena = arena;
    }

    public void join(Player player) {
        players.add(player);
        onJoin(player);
        if (status == GameStatus.IN_PROGRESS && !teams.isEmpty()) {
            teams.stream()
                    .min(Comparator.comparingInt(Team::getPlayerCount))
                    .ifPresent(team -> team.addPlayer(player));
        }
    }

    public void leave(OfflinePlayer player) {
        onLeave(player);
        players.remove(player);
        if (status == GameStatus.IN_PROGRESS) {
            teams.stream()
                    .filter(team -> team.hasPlayer(player))
                    .findFirst()
                    .ifPresent(team -> team.removePlayer(player));
        }
    }

    public void start() {
        gameConfig.getTeams().forEach(teamConfig -> teams.add(new Team(teamConfig)));
        if (gameConfig.getTeamAssignment() == TeamAssignment.RANDOM && !teams.isEmpty()) {
            List<OfflinePlayer> shuffledPlayers = new ArrayList<>(players);
            Collections.shuffle(shuffledPlayers);
            for (int i = 0; i < shuffledPlayers.size(); i++) {
                teams.get(i % teams.size()).addPlayer(shuffledPlayers.get(i));
            }
        }
        status = GameStatus.IN_PROGRESS;
    }

    public abstract void close();
    public abstract boolean open();
    public abstract void forceEnd();
    public abstract boolean canJoin();
    public abstract boolean canRejoin();
    public abstract void onJoin(Player player);
    public abstract void onLeave(OfflinePlayer player);
    public abstract String getDetailedStatusMessage();

}
