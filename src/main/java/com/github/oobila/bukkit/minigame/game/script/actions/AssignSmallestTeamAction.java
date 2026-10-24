package com.github.oobila.bukkit.minigame.game.script.actions;

import com.github.oobila.bukkit.minigame.game.Game;
import com.github.oobila.bukkit.minigame.game.script.GameAction;
import com.github.oobila.bukkit.minigame.game.script.GameActionAttributes;
import com.github.oobila.bukkit.minigame.game.script.GameEventContext;
import com.github.oobila.bukkit.minigame.team.Team;
import org.bukkit.OfflinePlayer;

import java.util.Comparator;

/**
 * The team-assignment rule for balanced game modes (team deathmatch, ...): puts the joining
 * player on whichever team currently has the fewest members. Bind this to the {@code join}
 * event; a different game type (e.g. one team of 1 versus everyone else) would bind a different
 * action instead, since "which team should a new player get" isn't the same rule everywhere.
 * Attributes: {@code who} - the context key naming the joining player (default "player").
 */
public class AssignSmallestTeamAction implements GameAction {

    public static final String KEY = "assign-smallest-team";

    @Override
    public int run(Game game, GameActionAttributes attributes, GameEventContext context) {
        OfflinePlayer who = context.get(attributes.getString("who", "player"));
        if (who == null || game.getTeam(who) != null) {
            return 0;
        }
        game.getTeams().stream()
                .min(Comparator.comparingInt(Team::getPlayerCount))
                .ifPresent(team -> team.addPlayer(who));
        return 0;
    }

}
