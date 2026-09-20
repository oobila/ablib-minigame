package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.team.Team;
import com.github.oobila.bukkit.minigame.team.TeamAssignment;
import com.github.oobila.bukkit.minigame.team.TeamConfig;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builds one {@link Team} per {@link TeamConfig} in the game's {@code GameConfig}, then - if
 * the config's {@link TeamAssignment} is RANDOM - shuffles the current players and round-robins
 * them across those teams. A no-op for team-less game modes (FFA, checkpoint race) since their
 * {@code GameConfig} simply has no team configs.
 */
public class AssignTeamsAction implements GameAction {

    public static final String KEY = "assign-teams";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        if (game.getGameConfig() == null || !game.getTeams().isEmpty()) {
            return 0;
        }
        List<TeamConfig> teamConfigs = game.getGameConfig().getTeams();
        if (teamConfigs.isEmpty()) {
            return 0;
        }
        teamConfigs.forEach(teamConfig -> game.getTeams().add(new Team(teamConfig)));
        if (game.getGameConfig().getTeamAssignment() == TeamAssignment.RANDOM) {
            List<OfflinePlayer> shuffled = new ArrayList<>(game.getPlayers());
            Collections.shuffle(shuffled);
            List<Team> teams = game.getTeams();
            for (int i = 0; i < shuffled.size(); i++) {
                teams.get(i % teams.size()).addPlayer(shuffled.get(i));
            }
        }
        return 0;
    }

}
