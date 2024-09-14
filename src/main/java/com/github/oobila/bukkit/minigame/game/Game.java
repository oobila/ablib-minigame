package com.github.oobila.bukkit.minigame.game;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Game extends PersistedObject {

    private final ABID id;
    private final String name;
    private Arena area;
    @Setter
    private Environment environment;
    private List<Team> teams = new ArrayList<>();

    protected Game(String name, Arena area) throws ABIDException {
        this.id = new ABID();
        this.name = name;
        this.area = area;
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
}
