package com.github.oobila.bukkit.minigame.team;

import com.github.oobila.bukkit.common.utils.model.BlockColor;
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
    private final BlockColor teamColor;
    private final List<Player> players = new ArrayList<>();

    public Team(String name, BlockColor color) {
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
                BlockColor.valueOf((String) args.get("teamColor"))
        );
    }
}
