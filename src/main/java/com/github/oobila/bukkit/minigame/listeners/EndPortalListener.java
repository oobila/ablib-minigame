package com.github.oobila.bukkit.minigame.listeners;

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
        boolean withinMinigamePortal = MinigameManager.getEnvironments().stream()
                .flatMap(environment -> environment.getPortals().stream())
                .anyMatch(portal -> portal.contains(location));

        if (withinMinigamePortal) {
            event.setCancelled(true);
            //TODO: handle player entering a minigame environment's portal
            event.getPlayer().sendMessage("you entered a portal");
        }
    }

}
