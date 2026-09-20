package com.github.oobila.bukkit.minigame.team;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
@SerializableAs("Team")
public class Team implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(Team.class);
    }

    @Getter
    private final TeamConfig teamConfig;
    private final List<OfflinePlayer> players = new ArrayList<>();

    public Team(TeamConfig teamConfig) {
        this.teamConfig = teamConfig;
    }

    public void addPlayer(OfflinePlayer player) {
        players.add(player);
        if (teamConfig.isGlowing()) {
            player.getPlayer().setGlowing(true);
        }
    }

    public void removePlayer(OfflinePlayer player) {
        player.getPlayer().setGlowing(false);
        players.remove(player);
    }

    public boolean hasPlayer(OfflinePlayer player) {
        return players.contains(player);
    }

    public int getPlayerCount() {
        return players.size();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("teamConfig", teamConfig);
        map.put("players", players.stream().map(player -> player.getUniqueId().toString()).toList());
        return map;
    }

    @NotNull
    public static Team deserialize(@NotNull Map<String, Object> args) {
        Team team = new Team((TeamConfig) args.get("teamConfig"));
        Object rawPlayers = args.get("players");
        if (rawPlayers instanceof List<?> list) {
            for (Object entry : list) {
                team.players.add(Bukkit.getOfflinePlayer(UUID.fromString((String) entry)));
            }
        }
        return team;
    }
}
