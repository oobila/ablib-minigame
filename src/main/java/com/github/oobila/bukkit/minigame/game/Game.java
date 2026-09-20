package com.github.oobila.bukkit.minigame.game;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.team.Team;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    private final Map<OfflinePlayer, Integer> scores = new HashMap<>();
    private final Set<OfflinePlayer> eliminated = new HashSet<>();
    @Setter
    private GameConfig gameConfig;
    private long phaseEnteredAt = System.currentTimeMillis();

    protected Game(String name) throws ABIDException {
        this.id = new ABID();
        this.name = name;
    }

    protected Game(ABID id, String name, Arena arena) {
        this.id = id;
        this.name = name;
        this.arena = arena;
    }

    protected Game(ABID id, String name, Arena arena, GameStatus status, GameConfig gameConfig,
                    List<Team> teams, List<OfflinePlayer> players, Map<OfflinePlayer, Integer> scores,
                    Set<OfflinePlayer> eliminated, long phaseEnteredAt) {
        this.id = id;
        this.name = name;
        this.arena = arena;
        this.status = status;
        this.gameConfig = gameConfig;
        this.teams.addAll(teams);
        this.players.addAll(players);
        this.scores.putAll(scores);
        this.eliminated.addAll(eliminated);
        this.phaseEnteredAt = phaseEnteredAt;
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

    /**
     * Kicks off the phase state machine. What actually happens (team assignment, teleporting
     * players to lobby markers, how long the lobby lasts, when it moves on) is entirely up to
     * the bound {@link GameConfig}'s script for the {@link GameStatus#PRE_GAME_LOBBY} phase.
     */
    public void start() {
        transitionTo(GameStatus.PRE_GAME_LOBBY);
    }

    public void transitionTo(GameStatus newStatus) {
        this.status = newStatus;
        this.phaseEnteredAt = System.currentTimeMillis();
        if (gameConfig != null && gameConfig.getScript() != null) {
            gameConfig.getScript().enter(this, newStatus);
        }
    }

    public void tick() {
        fireEvent(new GameEventContext(GameEventContext.TICK));
    }

    public void fireEvent(GameEventContext context) {
        if (gameConfig != null && gameConfig.getScript() != null) {
            gameConfig.getScript().fireEvent(this, status, context);
        }
    }

    public long getMillisSincePhaseEntered() {
        return System.currentTimeMillis() - phaseEnteredAt;
    }

    public void addScore(OfflinePlayer player, int amount) {
        scores.merge(player, amount, Integer::sum);
    }

    public int getScore(OfflinePlayer player) {
        return scores.getOrDefault(player, 0);
    }

    public int getTeamScore(Team team) {
        return team.sumPlayers(this::getScore);
    }

    public void eliminate(OfflinePlayer player) {
        eliminated.add(player);
    }

    public boolean isEliminated(OfflinePlayer player) {
        return eliminated.contains(player);
    }

    public List<OfflinePlayer> getActivePlayers() {
        return players.stream().filter(player -> !isEliminated(player)).toList();
    }

    public List<Team> getActiveTeams() {
        return teams.stream().filter(team -> team.sumPlayers(player -> isEliminated(player) ? 0 : 1) > 0).toList();
    }

    public Team getTeam(OfflinePlayer player) {
        return teams.stream().filter(team -> team.hasPlayer(player)).findFirst().orElse(null);
    }

    public OfflinePlayer getLeadingPlayer() {
        return players.stream().max(Comparator.comparingInt(this::getScore)).orElse(null);
    }

    public Team getLeadingTeam() {
        return teams.stream().max(Comparator.comparingInt(this::getTeamScore)).orElse(null);
    }

    public abstract void close();
    public abstract boolean open();
    public abstract void forceEnd();
    public abstract boolean canJoin();
    public abstract boolean canRejoin();
    public abstract void onJoin(Player player);
    public abstract void onLeave(OfflinePlayer player);
    public abstract String getDetailedStatusMessage();

    // Arena isn't included here: Arena already owns the reverse reference and persists the
    // Game it holds inline (see Arena#serialize), so persisting it from both sides would be
    // redundant. Arena#deserialize wires the back-reference via setGame() after reconstruction.
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("name", name);
        map.put("status", status.name());
        map.put("teams", new ArrayList<>(teams));
        map.put("players", players.stream().map(player -> player.getUniqueId().toString()).toList());
        Map<String, Integer> scoreMap = new HashMap<>();
        scores.forEach((player, score) -> scoreMap.put(player.getUniqueId().toString(), score));
        map.put("scores", scoreMap);
        map.put("eliminated", eliminated.stream().map(player -> player.getUniqueId().toString()).toList());
        map.put("phaseEnteredAt", phaseEnteredAt);
        if (gameConfig != null) {
            map.put("gameConfig", gameConfig);
        }
        return map;
    }

    protected static ABID extractId(Map<String, Object> args) {
        return ABID.fromString((String) args.get("id"));
    }

    protected static String extractName(Map<String, Object> args) {
        return (String) args.get("name");
    }

    protected static GameStatus extractStatus(Map<String, Object> args) {
        return GameStatus.valueOf((String) args.get("status"));
    }

    protected static GameConfig extractGameConfig(Map<String, Object> args) {
        return (GameConfig) args.get("gameConfig");
    }

    protected static List<Team> extractTeams(Map<String, Object> args) {
        List<Team> teams = new ArrayList<>();
        Object raw = args.get("teams");
        if (raw instanceof List<?> list) {
            for (Object entry : list) {
                if (entry instanceof Team team) {
                    teams.add(team);
                }
            }
        }
        return teams;
    }

    protected static List<OfflinePlayer> extractPlayers(Map<String, Object> args) {
        List<OfflinePlayer> players = new ArrayList<>();
        Object raw = args.get("players");
        if (raw instanceof List<?> list) {
            for (Object entry : list) {
                players.add(Bukkit.getOfflinePlayer(UUID.fromString((String) entry)));
            }
        }
        return players;
    }

    @SuppressWarnings("unchecked")
    protected static Map<OfflinePlayer, Integer> extractScores(Map<String, Object> args) {
        Map<OfflinePlayer, Integer> scores = new HashMap<>();
        Object raw = args.get("scores");
        if (raw instanceof Map<?, ?> map) {
            map.forEach((key, value) -> scores.put(Bukkit.getOfflinePlayer(UUID.fromString((String) key)), (Integer) value));
        }
        return scores;
    }

    protected static Set<OfflinePlayer> extractEliminated(Map<String, Object> args) {
        Set<OfflinePlayer> eliminated = new HashSet<>();
        Object raw = args.get("eliminated");
        if (raw instanceof List<?> list) {
            for (Object entry : list) {
                eliminated.add(Bukkit.getOfflinePlayer(UUID.fromString((String) entry)));
            }
        }
        return eliminated;
    }

    protected static long extractPhaseEnteredAt(Map<String, Object> args) {
        Object raw = args.get("phaseEnteredAt");
        return raw instanceof Number number ? number.longValue() : System.currentTimeMillis();
    }
}
