package com.github.oobila.bukkit.minigame.game;

import com.github.oobila.bukkit.common.utils.model.BlockColor;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Team extends PersistedObject {

    private String name;
    private TeamColor teamColor;
    private List<Player> players = new ArrayList<>();

    public Team(String name, TeamColor color) {
        this.name = name;
        this.teamColor = color;
    }

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

        private BlockColor blockColor;
    }
}
