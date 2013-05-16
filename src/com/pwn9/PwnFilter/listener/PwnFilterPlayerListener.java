package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

/**
* Listen for Chat events and apply the filter.
*/

public class PwnFilterPlayerListener implements Listener {
    private final PwnFilter plugin;

	public PwnFilterPlayerListener(PwnFilter p) {
        plugin = p;
        PluginManager pm = Bukkit.getServer().getPluginManager();

        /* Hook up the Listener for PlayerChat events */
        pm.registerEvent(AsyncPlayerChatEvent.class, this, p.chatPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerChat((AsyncPlayerChatEvent)e); }
                },
                plugin);

        pm.registerEvent(PlayerQuitEvent.class, this, p.chatPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerQuit((PlayerQuitEvent)e); }
                },
                plugin);

        plugin.logger.config("Activated PlayerListener with Priority Setting: " + p.chatPriority.toString());
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup player messages on quit
        if (event.getPlayer() != null && plugin.lastMessage.containsKey(event.getPlayer().getName())) {
            plugin.lastMessage.remove(event.getPlayer().getName());
        }
    }

    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        String pName = player.getName();
        String message = event.getMessage();

        // Permissions Check, if player has bypass permissions, then skip everything.
        if (player.hasPermission("pwnfilter.bypass.chat")) {
            return;
        }

        if (plugin.getConfig().getBoolean("spamfilter") && !player.hasPermission("pwnfilter.bypass.spam")) {
            // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
            if (plugin.lastMessage.containsKey(pName) && plugin.lastMessage.get(pName).equals(message)) {
                event.setCancelled(true);
                return;
            }
            plugin.lastMessage.put(pName, message);

        }
        plugin.filterChat(event);

    }

}