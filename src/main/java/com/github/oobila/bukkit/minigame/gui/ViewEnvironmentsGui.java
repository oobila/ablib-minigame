package com.github.oobila.bukkit.minigame.gui;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.gui.cells.model.ButtonCell;
import com.github.oobila.bukkit.gui.screens.SimpleGui;
import com.github.oobila.bukkit.itemstack.CustomItemStack;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.DataCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Comparator;

import static com.github.oobila.bukkit.itemstack.ItemStackProxy.skull;

public class ViewEnvironmentsGui extends SimpleGui {

    public ViewEnvironmentsGui(Plugin plugin, Player player, DataCache<ABID, Environment> dataCache) {
        super("View Environments", plugin, player);
        addAll(dataCache.get().stream().map(
                environment -> ButtonCell.builder()
                        .itemStack(CustomItemStack.builder(skull(environment.getStatus().getTexture()).getItemStack())
                                .setDisplayName(environment.getName())
                                .addLore(environment.getId().toString())
                                .addLore(environment.getStatus().name())
                                .addLore(environment.getGame() != null ? environment.getGame().getName() : "NO GAME")
                                .build())
                        .build()
        )
                .sorted(Comparator.comparing((ButtonCell o) -> o.getDisplayName()))
                .toList());
    }
}
