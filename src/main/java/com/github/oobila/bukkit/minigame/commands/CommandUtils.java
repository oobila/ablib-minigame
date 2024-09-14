package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.DataCache;
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
            DataCache<ABID, Environment> dataCache
    ) {
        for (Environment environment : dataCache.get()) {
            if (environment.getName().equalsIgnoreCase(name)) {
                return environment;
            }
        }
        log(Level.WARNING, "environment does not exist with name: {}", name);
        message(new Message("environment does not exist with name: {}", name), player);
        return null;
    }

    public static List<String> getEnvironmentNames(DataCache<ABID, Environment> dataCache) {
        return dataCache.get().stream().map(Environment::getName).toList();
    }

}
