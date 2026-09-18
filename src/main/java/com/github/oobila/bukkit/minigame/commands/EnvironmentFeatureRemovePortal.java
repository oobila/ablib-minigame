package com.github.oobila.bukkit.minigame.commands;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.command.arguments.StringArg;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

import static com.github.oobila.bukkit.common.ABCommon.message;

public class EnvironmentFeatureRemovePortal extends Command {

    public EnvironmentFeatureRemovePortal(ReadAndWriteCache<ABID, Environment> cache) {
        super("portals", "removes all portals from this environment");
        aliases("p");
        StringArg nameArg = new StringArg("name");
        nameArg.suggestionCallable((player, s) -> CommandUtils.getEnvironmentNames(cache));
        arg(nameArg);
        combinedCommand((player, command, s, args) -> {
            Environment environment = CommandUtils.getEnvironment(args[0], player, cache);
            if (environment == null) {
                return;
            }
            environment.removeAllPortals();
            message(new Message("removed all portals from '{}'", args[0]), player);
        });
    }
}
