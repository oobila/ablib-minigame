package com.github.oobila.bukkit.minigame.game.script;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.GameStatus;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Getter
@SerializableAs("GameScript")
public class GameScript implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(GameScript.class);
    }

    private final String name;
    private final Map<GameStatus, GamePhaseScript> phases;

    public GameScript(String name, Map<GameStatus, GamePhaseScript> phases) {
        this.name = name;
        this.phases = phases != null ? phases : new EnumMap<>(GameStatus.class);
    }

    public GamePhaseScript getPhase(GameStatus status) {
        return phases.get(status);
    }

    public void enter(Game game, GameStatus status) {
        GamePhaseScript phase = getPhase(status);
        if (phase != null) {
            phase.enter(game);
        }
    }

    public void fireEvent(Game game, GameStatus status, GameEventContext context) {
        GamePhaseScript phase = getPhase(status);
        if (phase != null) {
            phase.fireEvent(game, context);
        }
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        Map<String, Object> serializedPhases = new HashMap<>();
        phases.forEach((status, phase) -> serializedPhases.put(status.name(), phase));
        map.put("phases", serializedPhases);
        return map;
    }

    @NotNull
    public static GameScript deserialize(@NotNull Map<String, Object> args) {
        Map<GameStatus, GamePhaseScript> phases = new EnumMap<>(GameStatus.class);
        Object rawPhases = args.get("phases");
        if (rawPhases instanceof Map<?, ?> map) {
            map.forEach((key, value) -> {
                if (value instanceof GamePhaseScript phaseScript) {
                    phases.put(GameStatus.valueOf((String) key), phaseScript);
                }
            });
        }
        return new GameScript((String) args.get("name"), phases);
    }
}
