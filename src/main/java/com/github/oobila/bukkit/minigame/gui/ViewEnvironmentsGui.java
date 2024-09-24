package com.github.oobila.bukkit.minigame.gui;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.gui.cells.model.ButtonCell;
import com.github.oobila.bukkit.gui.screens.SimpleGui;
import com.github.oobila.bukkit.itemstack.CustomItemStack;
import com.github.oobila.bukkit.minigame.environments.Environment;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Comparator;

import static com.github.oobila.bukkit.itemstack.ItemStackProxy.skull;

public class ViewEnvironmentsGui extends SimpleGui {

    public ViewEnvironmentsGui(Plugin plugin, Player player, ReadAndWriteCache<ABID, Environment> cache) {
        super("View Environments", plugin, player);
        addAll(cache.values().stream().map(
                cacheItem -> ButtonCell.builder()
                        .itemStack(CustomItemStack.builder(skull(cacheItem.getData().getStatus().getTexture()).getItemStack())
                                .setDisplayName(cacheItem.getData().getName())
                                .addLore(cacheItem.getData().getId().toString())
                                .addLore(cacheItem.getData().getStatus().name())
                                .addLore(cacheItem.getData().getGame() != null ? cacheItem.getData().getGame().getName() : "NO GAME")
                                .build())
                        .build()
        )
                .sorted(Comparator.comparing((ButtonCell o) -> o.getDisplayName()))
                .toList());
    }
}
