package com.github.oobila.bukkit.minigame.gui;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.gui.cells.model.ButtonCell;
import com.github.oobila.bukkit.gui.screens.SimpleGui;
import com.github.oobila.bukkit.itemstack.CustomItemStack;
import com.github.oobila.bukkit.minigame.arena.Arena;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Comparator;

import static com.github.oobila.bukkit.itemstack.ItemStackProxy.skull;

public class ViewArenasGui extends SimpleGui {

    public ViewArenasGui(Plugin plugin, Player player, ReadAndWriteCache<ABID, Arena> cache) {
        super("View Arenas", plugin, player);
        addAll(cache.values().stream().map(
                cacheItem -> ButtonCell.builder()
                        .itemStack(CustomItemStack.builder(skull(cacheItem.getData().getStatus().getTexture()).getItemStack())
                                .setDisplayName(cacheItem.getData().getId().toString())
                                .addLore(cacheItem.getData().getStatus().name())
                                .build())
                        .build()
        )
                .sorted(Comparator.comparing((ButtonCell o) -> o.getDisplayName()))
                .toList());
    }
}
