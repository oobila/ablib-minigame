package com.github.oobila.bukkit.minigame.game;

import com.github.oobila.bukkit.common.utils.model.BlockColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Team {

    private TeamType teamType;
    private List<Player> players = new ArrayList<>();

    public Team(TeamType teamType) {
        this.teamType = teamType;
    }
}
