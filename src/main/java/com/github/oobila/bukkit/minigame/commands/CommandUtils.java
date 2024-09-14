package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.DataCache;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.message;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandUtils {

    public static Environment getEnvironment(
            String id,
            Player player,
            DataCache<ABID, Environment> dataCache
    ) {
        ABID abid = ABID.fromString(id);
        Environment environment = dataCache.get(abid);
        if (environment == null) {
            log(Level.WARNING, "environment does not exist with id: {}", id);
            message(new Message("environment does not exist with id: {}", id), player);
            return null;
        } else {
            return environment;
        }
    }

}
