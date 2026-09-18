package com.github.oobila.bukkit.minigame.environments;

import com.github.alastairbooth.abid.ABID;
import com.github.alastairbooth.abid.ABIDException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@SerializableAs("Portal")
public class Portal implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(Portal.class);
    }

    private final ABID id;
    @Setter
    private Location minLocation;
    @Setter
    private Location maxLocation;

    public Portal(Location minLocation, Location maxLocation) throws ABIDException {
        this(new ABID(), minLocation, maxLocation);
    }

    private Portal(ABID id, Location minLocation, Location maxLocation) {
        this.id = id;
        this.minLocation = minLocation;
        this.maxLocation = maxLocation;
    }

    public boolean contains(Location location) {
        if (location.getWorld() == null || !location.getWorld().equals(minLocation.getWorld())) {
            return false;
        }
        double minX = Math.min(minLocation.getX(), maxLocation.getX());
        double maxX = Math.max(minLocation.getX(), maxLocation.getX());
        double minY = Math.min(minLocation.getY(), maxLocation.getY());
        double maxY = Math.max(minLocation.getY(), maxLocation.getY());
        double minZ = Math.min(minLocation.getZ(), maxLocation.getZ());
        double maxZ = Math.max(minLocation.getZ(), maxLocation.getZ());
        return location.getX() >= minX && location.getX() <= maxX
                && location.getY() >= minY && location.getY() <= maxY
                && location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("minLocation", minLocation);
        map.put("maxLocation", maxLocation);
        return map;
    }

    @NotNull
    public static Portal deserialize(@NotNull Map<String, Object> args) {
        return new Portal(
                ABID.fromString((String) args.get("id")),
                (Location) args.get("minLocation"),
                (Location) args.get("maxLocation")
        );
    }
}
