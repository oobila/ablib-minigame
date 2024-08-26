package com.github.oobila.bukkit.minigame.game;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.environments.Environment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public abstract class Game {

    private final ABID id = new ABID();
    private GameStatus status = GameStatus.CLOSED;
    private Arena area;
    @Setter
    private Environment environment;
    private List<Team> teams;

    protected Game(Arena area) throws ABIDException {
        this.area = area;
    }

    public void stop() {
//        if (status.equals(GameStatus.IN_PROGRESS)) {
//            targetState = GameStatus.STOPPED;
//        }
    }

    public void start() {

    }
}
