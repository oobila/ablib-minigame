package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandUtils {

    public static Environment getEnvironment(
            String name,
            Player player,
            ReadAndWriteCache<ABID, Environment> cache
    ) {
        for (CacheItem<ABID,Environment> cacheItem : cache.values()) {
            if (cacheItem.getData().getName().equalsIgnoreCase(name)) {
                return cacheItem.getData();
            }
        }
        log(Level.WARNING, "environment does not exist with name: {}", name);
        message(new Message("environment does not exist with name: {}", name), player);
        return null;
    }

    public static List<String> getEnvironmentNames(ReadAndWriteCache<ABID, Environment> cache) {
        return cache.values().stream().map(cacheItem -> cacheItem.getData().getName()).toList();
    }

}
