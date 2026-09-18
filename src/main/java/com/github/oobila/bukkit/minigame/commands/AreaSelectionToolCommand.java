package com.github.oobila.bukkit.minigame.commands;

import com.github.oobila.bukkit.command.Command;
import com.github.oobila.bukkit.itemstack.ItemStackUtil;
import com.github.oobila.bukkit.minigame.items.AreaSelectionTool;

import static com.github.oobila.bukkit.common.ABCommon.message;

public class AreaSelectionToolCommand extends Command {

    public AreaSelectionToolCommand() {
        super("tool", "gives you an area selection tool");
        aliases("t", "w", "wand");
        command((player, command, s, args) -> {
            ItemStackUtil.givePlayer(player, AreaSelectionTool.create());
            message("you have been given an area selection tool", player);
        });
    }
}
