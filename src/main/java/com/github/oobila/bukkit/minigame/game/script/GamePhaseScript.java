package com.github.oobila.bukkit.minigame.game.script;

import com.github.oobila.bukkit.minigame.game.Game;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@SerializableAs("GamePhaseScript")
public class GamePhaseScript implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(GamePhaseScript.class);
    }

    private final List<ActionBinding> onEnter;
    private final List<TriggerBinding> triggers;

    public GamePhaseScript(List<ActionBinding> onEnter, List<TriggerBinding> triggers) {
        this.onEnter = onEnter != null ? onEnter : new ArrayList<>();
        this.triggers = triggers != null ? triggers : new ArrayList<>();
    }

    public void enter(Game game) {
        triggers.forEach(TriggerBinding::reset);
        GameEventContext context = new GameEventContext(GameEventContext.ENTER);
        onEnter.forEach(action -> action.run(game, context));
    }

    public void fireEvent(Game game, GameEventContext context) {
        triggers.forEach(trigger -> trigger.evaluate(game, context));
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("onEnter", new ArrayList<>(onEnter));
        map.put("triggers", new ArrayList<>(triggers));
        return map;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static GamePhaseScript deserialize(@NotNull Map<String, Object> args) {
        List<ActionBinding> onEnter = new ArrayList<>();
        Object rawOnEnter = args.get("onEnter");
        if (rawOnEnter instanceof List<?> list) {
            for (Object entry : list) {
                if (entry instanceof ActionBinding actionBinding) {
                    onEnter.add(actionBinding);
                }
            }
        }
        List<TriggerBinding> triggers = new ArrayList<>();
        Object rawTriggers = args.get("triggers");
        if (rawTriggers instanceof List<?> list) {
            for (Object entry : list) {
                if (entry instanceof TriggerBinding triggerBinding) {
                    triggers.add(triggerBinding);
                }
            }
        }
        return new GamePhaseScript(onEnter, triggers);
    }
}
