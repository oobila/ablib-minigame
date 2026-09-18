package com.github.oobila.bukkit.minigame.listeners;

import com.github.oobila.bukkit.minigame.MinigameManager;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EndPortalListener implements Listener {

    @EventHandler
    public void onEndPortal(PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL &&
                event.getCause() != PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            return;
        }

        Location location = event.getPlayer().getLocation();
        boolean withinMinigamePortal = MinigameManager.getMinigames().stream()
                .flatMap(minigame -> minigame.getEnvironmentCache().values().stream()
                        .flatMap(cacheItem -> cacheItem.getData().getPortals().stream())
                )
                .anyMatch(portal -> portal.contains(location));
        if (withinMinigamePortal) {
            event.setCancelled(true);
            //TODO: handle player entering a minigame environment's portal
            event.getPlayer().sendMessage("you entered a portal");
        }
    }

}
