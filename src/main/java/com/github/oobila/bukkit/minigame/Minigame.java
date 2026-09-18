package com.github.oobila.bukkit.minigame;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

public interface Minigame {

    ReadAndWriteCache<ABID, Environment> getEnvironmentCache();

    ReadAndWriteCache<ABID, Arena> getArenaCache();

}
