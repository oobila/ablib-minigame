package com.github.oobila.bukkit.minigame.team;

import com.github.oobila.bukkit.common.utils.model.BlockColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@SerializableAs("TeamConfig")
public class TeamConfig implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(TeamConfig.class);
    }

    private final String name;
    private final BlockColor teamColor;
    private final boolean isGlowing;

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("teamColor", teamColor.name());
        map.put("isGlowing", isGlowing);
        return map;
    }

    @NotNull
    public static TeamConfig deserialize(@NotNull Map<String, Object> args) {
        return new TeamConfig(
                (String) args.get("name"),
                BlockColor.valueOf((String) args.get("teamColor")),
                Boolean.TRUE.equals(args.get("isGlowing"))
        );
    }
}
