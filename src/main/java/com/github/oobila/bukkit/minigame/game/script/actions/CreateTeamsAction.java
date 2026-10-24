package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.team.Team;
import com.github.oobila.bukkit.minigame.team.TeamConfig;

/**
 * Builds one empty {@link Team} per {@link TeamConfig} in the game's {@code GameConfig}. Doesn't
 * put any players on them - that happens per player, at join time, via whichever action a script
 * binds to the {@code join} event (see {@link AssignSmallestTeamAction}), since who goes where
 * differs by game type. A no-op for team-less game modes (FFA, checkpoint race) since their
 * {@code GameConfig} simply has no team configs.
 */
public class CreateTeamsAction implements GameAction {

    public static final String KEY = "create-teams";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        if (game.getGameConfig() == null || !game.getTeams().isEmpty()) {
            return 0;
        }
        game.getGameConfig().getTeams().forEach(teamConfig -> game.getTeams().add(new Team(teamConfig)));
        return 0;
    }

}
