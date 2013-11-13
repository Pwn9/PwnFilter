package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listeners for the DataCache
 * User: ptoal
 * Date: 13-11-13
 * Time: 10:34 AM
 */
public class PlayerCacheListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup player messages on quit
        if (event.getPlayer() != null && PwnFilter.lastMessage.containsKey(event.getPlayer())) {
            PwnFilter.lastMessage.remove(event.getPlayer());
        }
        DataCache.getInstance().removePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        DataCache.getInstance().addPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        DataCache.getInstance().updatePlayerWorld(event.getPlayer());
    }


}
