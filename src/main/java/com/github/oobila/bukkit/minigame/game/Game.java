package com.github.oobila.bukkit.minigame.game;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.environments.Environment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
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
    private final List<Team> teams = new ArrayList<>();

    protected Game(String name) throws ABIDException {
        this.id = new ABID();
        this.name = name;
    }

    protected Game(ABID id, String name, Arena area) {
        this.id = id;
        this.name = name;
        this.area = area;
    }

    public abstract boolean canClose();
    public abstract boolean close();
    public abstract boolean canOpen();
    public abstract boolean open();
    public abstract boolean isRunning();
    public abstract boolean canJoin();
    public abstract String getStatusMessage();
}
