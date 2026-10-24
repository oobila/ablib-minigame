package com.github.oobila.bukkit.minigame.game;

import com.github.oobila.bukkit.minigame.game.script.GameScript;
import com.github.oobila.bukkit.minigame.team.TeamConfig;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The reusable, file-authored template for a game type: what teams it has (if any), whether a
 * new player can join once the match is already {@code IN_PROGRESS}, and the {@link GameScript}
 * that drives the match. An Arena is paired with a GameConfig to produce a runnable
 * {@code Game}. There is deliberately no "how are players assigned to teams" field here - that
 * rule (smallest team, always the underdog side, ...) is expressed as whichever {@code GameAction}
 * a script binds to the {@code join} event, since different game types need different rules.
 */
@Getter
@SerializableAs("GameConfig")
public class GameConfig implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(GameConfig.class);
    }

    private final List<TeamConfig> teams;
    private final boolean midSessionJoinAllowed;
    private final GameScript script;

    public GameConfig(List<TeamConfig> teams, boolean midSessionJoinAllowed, GameScript script) {
        this.teams = teams != null ? teams : new ArrayList<>();
        this.midSessionJoinAllowed = midSessionJoinAllowed;
        this.script = script;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("teams", new ArrayList<>(teams));
        map.put("midSessionJoinAllowed", midSessionJoinAllowed);
        if (script != null) {
            map.put("script", script);
        }
        return map;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static GameConfig deserialize(@NotNull Map<String, Object> args) {
        List<TeamConfig> teams = new ArrayList<>();
        Object rawTeams = args.get("teams");
        if (rawTeams instanceof List<?> list) {
            for (Object entry : list) {
                if (entry instanceof TeamConfig teamConfig) {
                    teams.add(teamConfig);
                }
            }
        }
        return new GameConfig(
                teams,
                Boolean.TRUE.equals(args.get("midSessionJoinAllowed")),
                (GameScript) args.get("script")
        );
    }
}
