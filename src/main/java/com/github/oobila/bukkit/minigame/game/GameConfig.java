package com.github.oobila.bukkit.minigame.game;

import com.github.oobila.bukkit.minigame.game.script.GameScript;
import com.github.oobila.bukkit.minigame.team.TeamAssignment;
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
 * The reusable, file-authored template for a game type: what teams it has (if any), how
 * players get assigned to them, and the {@link GameScript} that drives the match. An Arena
 * is paired with a GameConfig to produce a runnable {@code Game}.
 */
@Getter
@SerializableAs("GameConfig")
public class GameConfig implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(GameConfig.class);
    }

    private final List<TeamConfig> teams;
    private final TeamAssignment teamAssignment;
    private final GameScript script;

    public GameConfig(List<TeamConfig> teams, TeamAssignment teamAssignment, GameScript script) {
        this.teams = teams != null ? teams : new ArrayList<>();
        this.teamAssignment = teamAssignment;
        this.script = script;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("teams", new ArrayList<>(teams));
        if (teamAssignment != null) {
            map.put("teamAssignment", teamAssignment.name());
        }
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
        Object rawAssignment = args.get("teamAssignment");
        TeamAssignment teamAssignment = rawAssignment != null ? TeamAssignment.valueOf((String) rawAssignment) : null;
        return new GameConfig(teams, teamAssignment, (GameScript) args.get("script"));
    }
}
