package com.pwn9.PwnFilter.bukkit.listener;

import com.pwn9.PwnFilter.bukkit.PwnFilterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listeners for the DataCache
 * User: ptoal
 * Date: 13-11-13
 * Time: 10:34 AM
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class PlayerCacheListener implements Listener {

    /**
     * <p>onPlayerQuit.</p>
     *
     * @param event a {@link org.bukkit.event.player.PlayerQuitEvent} object.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup player messages on quit
        if (event.getPlayer() != null && PwnFilterPlugin.lastMessage.containsKey(event.getPlayer())) {
            PwnFilterPlugin.lastMessage.remove(event.getPlayer());
        }
    }

    /**
     * <p>onPlayerJoin.</p>
     *
     * @param event a {@link org.bukkit.event.player.PlayerJoinEvent} object.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // In future, might want to load points data?
    }

}
