package com.github.oobila.bukkit.minigame.game.script;

import com.github.oobila.bukkit.minigame.game.Game;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@SerializableAs("ActionBinding")
public class ActionBinding implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(ActionBinding.class);
    }

    private final String actionKey;
    private final GameActionAttributes attributes;

    public ActionBinding(String actionKey, GameActionAttributes attributes) {
        this.actionKey = actionKey;
        this.attributes = attributes != null ? attributes : new GameActionAttributes();
    }

    public int run(Game game, GameEventContext context) {
        return GameActionRegistry.get(actionKey).run(game, attributes, context);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("actionKey", actionKey);
        map.put("attributes", attributes.getAttributes());
        return map;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static ActionBinding deserialize(@NotNull Map<String, Object> args) {
        Object rawAttributes = args.get("attributes");
        GameActionAttributes attributes = new GameActionAttributes(
                rawAttributes instanceof Map<?, ?> ? (Map<String, Object>) rawAttributes : null);
        return new ActionBinding((String) args.get("actionKey"), attributes);
    }
}
