package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;

/**
* Listen for Chat events and apply the filter.
*/

public class PwnFilterPlayerListener implements Listener {
    private final PwnFilter plugin;
    static HashMap<String, String> messages = new HashMap<String, String>();

	public PwnFilterPlayerListener(PwnFilter p) {
        plugin = p;
        PluginManager pm = Bukkit.getServer().getPluginManager();

        String priority = plugin.getConfig().getString("priority");
        if (priority.isEmpty()) priority = "HIGHEST";

        EventPriority chatFilterPriority = EventPriority.valueOf(priority.toUpperCase());

        /* Hook up the Listener for PlayerChat events */
        pm.registerEvent(AsyncPlayerChatEvent.class, this, chatFilterPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerChat((AsyncPlayerChatEvent)e); }
                },
                plugin);

        pm.registerEvent(PlayerQuitEvent.class, this, chatFilterPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerQuit((PlayerQuitEvent)e); }
                },
                plugin);

        plugin.logToFile("Activated PlayerListener with Priority Setting: "+chatFilterPriority);
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup player messages on quit
        if (event.getPlayer() != null && messages.containsKey(event.getPlayer().getName())) {
            messages.remove(event.getPlayer().getName());
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

        if (plugin.getConfig().getBoolean("chatspamfilter") && !player.hasPermission("pwnfilter.bypass.spam")) {
            // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
            if (messages.containsKey(pName) && messages.get(pName).equals(message)) {
                event.setCancelled(true);
                return;
            }
            messages.put(pName, message);

        }
        plugin.filterChat(event);

    }

}