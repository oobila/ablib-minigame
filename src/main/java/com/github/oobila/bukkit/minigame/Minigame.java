package com.github.oobila.bukkit.minigame;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.real.CombiCache;

public interface Minigame {

    CombiCache<ABID, Environment> getEnvironmentCache();

    CombiCache<ABID, Arena> getArenaCache();

}
