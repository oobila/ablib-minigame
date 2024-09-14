package com.github.oobila.bukkit.minigame.environments;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Environment extends PersistedObject {

    private final ABID id;
    private final String name;
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
        if (!this.status.equals(EnvironmentStatus.CLOSED)) {
            return false;
        }
        if (this.game != null) {
            if (this.game.isRunning()) {
                return false;
            }
            this.game.setEnvironment(null);
        }
        this.game = game;
        game.setEnvironment(this);
        return true;
    }

    public boolean open() {
        if (game == null || !game.canOpen()) {
            return false;
        }
        status = EnvironmentStatus.OPEN;
        game.open();
        return true;
    }

    public boolean close() {
        if (status.equals(EnvironmentStatus.OPEN)) {
            if (game.canClose()) {
                game.close();
                status = EnvironmentStatus.CLOSING;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void notifyClosed() {
        if (status.equals(EnvironmentStatus.CLOSING)) {
            status = EnvironmentStatus.CLOSED;
        }
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
