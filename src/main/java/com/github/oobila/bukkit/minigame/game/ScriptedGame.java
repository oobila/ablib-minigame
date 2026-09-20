package com.github.oobila.bukkit.minigame.game;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.team.Team;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The single, generic {@link Game} implementation. Every game type (1v1 duel, FFA, TDM,
 * checkpoint race, CTF, ...) is the same ScriptedGame - what makes them different is entirely
 * the {@link GameConfig}/{@code GameScript} bound to them, not the Java class.
 */
@SerializableAs("ScriptedGame")
public class ScriptedGame extends Game {

    static {
        ConfigurationSerialization.registerClass(ScriptedGame.class);
    }

    public ScriptedGame(String name) throws ABIDException {
        super(name);
    }

    private ScriptedGame(ABID id, String name, Arena arena, GameStatus status, GameConfig gameConfig,
                          List<Team> teams, List<OfflinePlayer> players, Map<OfflinePlayer, Integer> scores,
                          Set<OfflinePlayer> eliminated, long phaseEnteredAt) {
        super(id, name, arena, status, gameConfig, teams, players, scores, eliminated, phaseEnteredAt);
    }

    @Override
    public void close() {
        transitionTo(GameStatus.ENDED);
    }

    @Override
    public boolean open() {
        if (getStatus() != GameStatus.READY) {
            return false;
        }
        start();
        return true;
    }

    @Override
    public void forceEnd() {
        transitionTo(GameStatus.ENDED);
    }

    @Override
    public boolean canJoin() {
        return getStatus() == GameStatus.READY || getStatus() == GameStatus.PRE_GAME_LOBBY;
    }

    @Override
    public boolean canRejoin() {
        return getStatus() == GameStatus.IN_PROGRESS;
    }

    @Override
    public void onJoin(Player player) {
        fireEvent(new GameEventContext(GameEventContext.JOIN, Map.of("player", player)));
    }

    @Override
    public void onLeave(OfflinePlayer player) {
        fireEvent(new GameEventContext(GameEventContext.LEAVE, Map.of("player", player)));
    }

    @Override
    public String getDetailedStatusMessage() {
        return getName() + " [" + getStatus() + "] players=" + getPlayers().size();
    }

    @NotNull
    public static ScriptedGame deserialize(@NotNull Map<String, Object> args) {
        return new ScriptedGame(
                extractId(args),
                extractName(args),
                null,
                extractStatus(args),
                extractGameConfig(args),
                extractTeams(args),
                extractPlayers(args),
                extractScores(args),
                extractEliminated(args),
                extractPhaseEnteredAt(args)
        );
    }
}
