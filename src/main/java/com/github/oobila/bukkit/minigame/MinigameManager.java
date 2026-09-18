package com.github.oobila.bukkit.minigame;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class MinigameManager {

    @Getter
    private static final Set<Minigame> minigames = new HashSet<>();

    public void register(Minigame minigame) {
        minigames.add(minigame);
    }

}
