package com.github.oobila.bukkit.minigame.game;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.environments.Environment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@Getter
public abstract class Game implements ConfigurationSerializable {

    private final ABID id;
    private final String name;
    @Setter
    private Arena area;
    @Setter
    private Environment environment;
    @Setter(AccessLevel.PROTECTED)
    private GameStatus status = GameStatus.PREPARING;
    private final List<OfflinePlayer> players = new ArrayList<>();

    protected Game(String name) throws ABIDException {
        this.id = new ABID();
        this.name = name;
    }

    protected Game(ABID id, String name, Arena area) {
        this.id = id;
        this.name = name;
        this.area = area;
    }

    public void join(Player player) {
        players.add(player);
        onJoin(player);
    }

    public void leave(OfflinePlayer player) {
        onLeave(player);
        players.remove(player);
    }

    public abstract void close();
    public abstract boolean open();
    public abstract void forceEnd();
    public abstract boolean canJoin();
    public abstract boolean canRejoin();
    public abstract void onJoin(Player player);
    public abstract void onLeave(OfflinePlayer player);
    public abstract String getDetailedStatusMessage();
}
