package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        pm.registerEvent(AsyncPlayerChatEvent.class, this, PwnFilter.chatPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerChat((AsyncPlayerChatEvent)e); }
                },
                plugin);

        pm.registerEvent(PlayerQuitEvent.class, this, PwnFilter.chatPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerQuit((PlayerQuitEvent)e); }
                },
                plugin);

        PwnFilter.logger.config("Activated PlayerListener with Priority Setting: " + PwnFilter.chatPriority.toString());
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup player messages on quit
        if (event.getPlayer() != null && PwnFilter.lastMessage.containsKey(event.getPlayer().getName())) {
            PwnFilter.lastMessage.remove(event.getPlayer().getName());
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
            if (PwnFilter.lastMessage.containsKey(pName) && PwnFilter.lastMessage.get(pName).equals(message)) {
                event.setCancelled(true);
                return;
            }
            PwnFilter.lastMessage.put(pName, message);

        }

        // Global mute
        if ((PwnFilter.pwnMute) && (!(player.hasPermission("pwnfilter.bypass.mute")))) {
            event.setCancelled(true);
            return; // No point in continuing.
        }

        // Global decolor
        if ((PwnFilter.decolor) && (!(player.hasPermission("pwnfilter.color")))) {
            // We are changing the state of the message.  Let's do that before any rules processing.
            event.setMessage(ChatColor.stripColor(event.getMessage()));
        }

        // Take the message from the ChatEvent and send it through the filter.
        FilterState state = new FilterState(plugin, event.getMessage(),event.getPlayer());

        PwnFilter.ruleset.runFilter(state, "chat");

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setMessage(state.message.getColoredString());
        }
        if (state.cancel) event.setCancelled(true);
    }

}