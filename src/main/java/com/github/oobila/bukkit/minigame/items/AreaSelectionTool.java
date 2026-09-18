package com.github.oobila.bukkit.minigame.items;

import com.github.oobila.bukkit.itemstack.ItemStackProxy;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.oobila.bukkit.chat.Message.message;

public class AreaSelectionTool implements Listener {

    private static final Material MATERIAL = Material.BREEZE_ROD;
    private static final String DISPLAY_NAME = ChatColor.AQUA + "Area Selection Tool";
    private static final String TOOL_TAG = "area-selection-tool";
    private static final String AREA_SELECTION_POSITION_MARKED = "Area selection: {0} set to {1}, {2}, {3}";

    private static final Map<UUID, Pair<Location, Location>> playerAreaSelections = new HashMap<>();

    public static ItemStack create() {
        return ItemStackProxy.item(MATERIAL, DISPLAY_NAME)
                .addMeta(TOOL_TAG, "true")
                .setEnchanted()
                .build();
    }

    public static Location getMinSelection(Player player) {
        Pair<Location, Location> selection = playerAreaSelections.get(player.getUniqueId());
        if (selection == null || selection.getLeft() == null || selection.getRight() == null) {
            return null;
        }
        return compare(selection.getLeft(), selection.getRight()) <= 0 ? selection.getLeft() : selection.getRight();
    }

    public static Location getMaxSelection(Player player) {
        Pair<Location, Location> selection = playerAreaSelections.get(player.getUniqueId());
        if (selection == null || selection.getLeft() == null || selection.getRight() == null) {
            return null;
        }
        return compare(selection.getLeft(), selection.getRight()) >= 0 ? selection.getLeft() : selection.getRight();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !isSelectionTool(event.getItem())) {
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();
        Location location = event.getClickedBlock().getLocation();
        Pair<Location, Location> existing = playerAreaSelections.get(playerId);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            playerAreaSelections.put(playerId, Pair.of(location, existing == null ? null : existing.getRight()));
            message(AREA_SELECTION_POSITION_MARKED)
                    .arg("position 1")
                    .arg(location.getBlockX())
                    .arg(location.getBlockY())
                    .arg(location.getBlockZ())
                    .send(event.getPlayer());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            playerAreaSelections.put(playerId, Pair.of(existing == null ? null : existing.getLeft(), location));
            message(AREA_SELECTION_POSITION_MARKED)
                    .arg("position 2")
                    .arg(location.getBlockX())
                    .arg(location.getBlockY())
                    .arg(location.getBlockZ())
                    .send(event.getPlayer());
        }
    }

    private static boolean isSelectionTool(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == MATERIAL && itemStack.getItemMeta() != null &&
                "true".equals(new ItemStackProxy(itemStack).getMetaString(TOOL_TAG));
    }

    private static int compare(Location a, Location b) {
        int result = Double.compare(a.getX(), b.getX());
        if (result == 0) {
            result = Double.compare(a.getZ(), b.getZ());
        }
        if (result == 0) {
            result = Double.compare(a.getY(), b.getY());
        }
        return result;
    }

}
