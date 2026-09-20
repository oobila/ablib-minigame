package com.github.oobila.bukkit.minigame.game;

import com.github.oobila.bukkit.minigame.team.TeamAssignment;
import com.github.oobila.bukkit.minigame.team.TeamConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class GameConfig {

    private final List<TeamConfig> teams = new ArrayList<>();
    private final TeamAssignment teamAssignment;

}
