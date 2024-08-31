package com.github.oobila.bukkit.minigame.game;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Arena extends PersistedObject {

    private final ABID id;
    private Location minLocation;
    private Location maxLocation;
    private ArenaStatus status;
    private List<Location> soloSpawns = new ArrayList<>();
    private Map<Integer, List<Location>> teamSpawns = new HashMap<>();

    public Arena(Location minLocation, Location maxLocation) throws ABIDException {
        this.id = new ABID();
        this.minLocation = minLocation;
        this.maxLocation = maxLocation;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("minLocation", minLocation);
        map.put("maxLocation", maxLocation);
        map.put("status", status.name());
        map.put("soloSpawns", soloSpawns.toArray());
        map.put("teamSpawns", teamSpawns);
        return map;
    }

    public static Arena deserialize(Map<String, Object> args) {
        return new Arena(
                ABID.fromString((String) args.get("id")),
                (Location) args.get("minLocation"),
                (Location) args.get("maxLocation"),
                ArenaStatus.valueOf((String) args.get("status")),
                (List<Location>) args.get("soloSpawns"),
                (Map<Integer, List<Location>>) args.get("teamSpawns")
        );
    }
}
