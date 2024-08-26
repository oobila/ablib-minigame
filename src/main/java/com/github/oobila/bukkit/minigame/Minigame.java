package com.github.oobila.bukkit.minigame;

import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.Team;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class Minigame {

    private static List<Environment> environments;

    public static Game getCurrentGame(Player player) {
        for (Environment environment : environments) {
            for (Team team : environment.getGame().getTeams()) {
                if (team.getPlayers().contains(player)) {
                    return environment.getGame();
                }
            }
        }
        return null;
    }

    public static Team getTeam(Player player) {
        for (Environment environment : environments) {
            for (Team team : environment.getGame().getTeams()) {
                if (team.getPlayers().contains(player)) {
                    return team;
                }
            }
        }
        return null;
    }

}
