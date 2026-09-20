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
@SerializableAs("TriggerBinding")
public class TriggerBinding implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(TriggerBinding.class);
    }

    // NOT named "on": YAML 1.1 (what SnakeYAML/Bukkit's config uses) parses the bareword
    // "on" as the boolean true, not the string "on" - a field/key named that always came
    // back null.
    private final String event;
    private final String triggerKey;
    private final GameActionAttributes attributes;
    private final List<ActionBinding> actions;
    // repeatable=false bindings (the usual case for win conditions) only fire once per phase entry,
    // so ending the game doesn't get re-triggered on every subsequent tick
    private final boolean repeatable;
    private boolean fired;

    public TriggerBinding(String event, String triggerKey, GameActionAttributes attributes,
                           List<ActionBinding> actions, boolean repeatable) {
        this.event = event;
        this.triggerKey = triggerKey;
        this.attributes = attributes != null ? attributes : new GameActionAttributes();
        this.actions = actions != null ? actions : new ArrayList<>();
        this.repeatable = repeatable;
    }

    public void reset() {
        fired = false;
    }

    public void evaluate(Game game, GameEventContext context) {
        if (!event.equals(context.getKey()) || (fired && !repeatable)) {
            return;
        }
        if (GameTriggerRegistry.get(triggerKey).test(game, attributes, context)) {
            fired = true;
            actions.forEach(action -> action.run(game, context));
        }
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("event", event);
        map.put("triggerKey", triggerKey);
        map.put("attributes", attributes.getAttributes());
        map.put("actions", new ArrayList<>(actions));
        map.put("repeatable", repeatable);
        return map;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static TriggerBinding deserialize(@NotNull Map<String, Object> args) {
        List<ActionBinding> actions = new ArrayList<>();
        Object rawActions = args.get("actions");
        if (rawActions instanceof List<?> list) {
            for (Object entry : list) {
                if (entry instanceof ActionBinding actionBinding) {
                    actions.add(actionBinding);
                }
            }
        }
        Object rawAttributes = args.get("attributes");
        GameActionAttributes attributes = new GameActionAttributes(
                rawAttributes instanceof Map<?, ?> ? (Map<String, Object>) rawAttributes : null);
        return new TriggerBinding(
                (String) args.get("event"),
                (String) args.get("triggerKey"),
                attributes,
                actions,
                Boolean.TRUE.equals(args.get("repeatable"))
        );
    }
}
