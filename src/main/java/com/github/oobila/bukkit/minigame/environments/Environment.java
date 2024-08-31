package com.github.oobila.bukkit.minigame.environments;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.GameStatus;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Environment extends PersistedObject {

    private final ABID id;
    private String name;
    private Game game;
    private EnvironmentStatus status = EnvironmentStatus.CLOSED;

    public Environment(String name) throws ABIDException {
        this.id = new ABID();
        this.name = name;
    }

    private Environment(ABID id, String name) {
        this.id = id;
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

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("name", name);
        return map;
    }

    public static Environment deserialize(Map<String, Object> args) {
        return new Environment(
                ABID.fromString((String) args.get("id")),
                (String) args.get("name")
        );
    }
}
