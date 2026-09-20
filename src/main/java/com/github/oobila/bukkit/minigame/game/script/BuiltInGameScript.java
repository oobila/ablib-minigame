package com.github.oobila.bukkit.minigame.game.script;

import com.github.oobila.bukkit.minigame.game.script.actions.AnnounceWinnerAction;
import com.github.oobila.bukkit.minigame.game.script.actions.AssignTeamsAction;
import com.github.oobila.bukkit.minigame.game.script.actions.AwardScoreAction;
import com.github.oobila.bukkit.minigame.game.script.actions.BroadcastMessageAction;
import com.github.oobila.bukkit.minigame.game.script.actions.EliminateAction;
import com.github.oobila.bukkit.minigame.game.script.actions.EndGameAction;
import com.github.oobila.bukkit.minigame.game.script.actions.TeleportToMarkerAction;
import com.github.oobila.bukkit.minigame.game.script.actions.TransitionPhaseAction;
import com.github.oobila.bukkit.minigame.game.script.triggers.AlwaysTrigger;
import com.github.oobila.bukkit.minigame.game.script.triggers.CheckpointProximityTrigger;
import com.github.oobila.bukkit.minigame.game.script.triggers.LastEntityStandingTrigger;
import com.github.oobila.bukkit.minigame.game.script.triggers.ScoreThresholdTrigger;
import com.github.oobila.bukkit.minigame.game.script.triggers.TimeElapsedTrigger;

/**
 * Registers every action/trigger this library ships out of the box. Host plugins must call
 * {@link #register()} once during startup, before loading any saved games or scripts, so the
 * action/trigger keys referenced from script files resolve.
 */
public final class BuiltInGameScript {

    private BuiltInGameScript() {
    }

    public static void register() {
        GameActionRegistry.register(AwardScoreAction.KEY, new AwardScoreAction());
        GameActionRegistry.register(EliminateAction.KEY, new EliminateAction());
        GameActionRegistry.register(BroadcastMessageAction.KEY, new BroadcastMessageAction());
        GameActionRegistry.register(TeleportToMarkerAction.KEY, new TeleportToMarkerAction());
        GameActionRegistry.register(AssignTeamsAction.KEY, new AssignTeamsAction());
        GameActionRegistry.register(TransitionPhaseAction.KEY, new TransitionPhaseAction());
        GameActionRegistry.register(EndGameAction.KEY, new EndGameAction());
        GameActionRegistry.register(AnnounceWinnerAction.KEY, new AnnounceWinnerAction());

        GameTriggerRegistry.register(AlwaysTrigger.KEY, new AlwaysTrigger());
        GameTriggerRegistry.register(ScoreThresholdTrigger.KEY, new ScoreThresholdTrigger());
        GameTriggerRegistry.register(LastEntityStandingTrigger.KEY, new LastEntityStandingTrigger());
        GameTriggerRegistry.register(CheckpointProximityTrigger.KEY, new CheckpointProximityTrigger());
        GameTriggerRegistry.register(TimeElapsedTrigger.KEY, new TimeElapsedTrigger());
    }

}
