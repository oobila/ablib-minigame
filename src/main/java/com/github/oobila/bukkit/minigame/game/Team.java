package com.github.oobila.bukkit.minigame.game;

import com.github.oobila.bukkit.common.utils.model.BlockColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Getter
public class Team implements ConfigurationSerializable {

    private final String name;
    private final TeamColor teamColor;
    private final List<Player> players = new ArrayList<>();

    public Team(String name, TeamColor color) {
        this.name = name;
        this.teamColor = color;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("teamColor", teamColor);
        return map;
    }

    public static Team deserialize(Map<String, Object> args) {
        return new Team(
                (String) args.get("name"),
                TeamColor.valueOf((String) args.get("teamColor"))
        );
    }

    @AllArgsConstructor
    @Getter
    public enum TeamColor {
        WHITE(BlockColor.WHITE),
        GRAY(BlockColor.GRAY),
        RED(BlockColor.RED),
        YELLOW(BlockColor.YELLOW),
        GREEN(BlockColor.LIME),
        CYAN(BlockColor.LIGHT_BLUE),
        PINK(BlockColor.PINK);

        private final BlockColor blockColor;
    }
}
