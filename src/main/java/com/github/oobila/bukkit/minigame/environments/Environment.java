package com.github.oobila.bukkit.minigame.environments;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.GameStatus;
import lombok.Getter;

@Getter
public class Environment {

    private ABID id = new ABID();
    private String name;
    private Game game;
    private EnvironmentStatus status = EnvironmentStatus.CLOSED;

    public Environment(String name) throws ABIDException {
        this.name = name;
    }

    public boolean setGame(Game game) {
        if (!this.status.equals(EnvironmentStatus.CLOSED) || this.game.getStatus().equals(GameStatus.OPEN)) {
            return false;
        }
        this.game = game;
        return true;
    }

//    public boolean close() {
//        if (status.equals(EnvironmentStatus.OPEN)) {
//            if (game != null && game.getStatus().equals(GameStatus.OPEN)) {
//                return false;
//            } else {
//                status = EnvironmentStatus.CLOSING;
//                game.stop();
//                return true;
//            }
//        }
//
//    }

    public void open() {

    }
}
