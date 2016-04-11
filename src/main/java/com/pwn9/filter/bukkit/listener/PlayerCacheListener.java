package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.bukkit.PwnFilterBukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listeners for the DataCache
 * User: Sage905
 * Date: 13-11-13
 * Time: 10:34 AM
 *
 * @author Sage905
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
        if (event.getPlayer() != null && PwnFilterBukkitPlugin.lastMessage.containsKey(event.getPlayer().getUniqueId())) {
            PwnFilterBukkitPlugin.lastMessage.remove(event.getPlayer().getUniqueId());
        }
    }

//    /**
//     * <p>onPlayerJoin.</p>
//     *
//     * @param event a {@link org.bukkit.event.player.PlayerJoinEvent} object.
//     */
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        // In future, might want to load points data?
//    }

}
