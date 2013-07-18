package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
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

        PwnFilter.logger.info("Activated PlayerListener with Priority Setting: " + PwnFilter.chatPriority.toString());
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup player messages on quit
        if (event.getPlayer() != null && PwnFilter.lastMessage.containsKey(event.getPlayer())) {
            PwnFilter.lastMessage.remove(event.getPlayer());
        }
    }

    public void onPlayerChat(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        DataCache dCache = PwnFilter.dataCache;

        // Permissions Check, if player has bypass permissions, then skip everything.
        if (dCache.hasPermission(player,"pwnfilter.bypass.chat")) return;

        String message = event.getMessage();

        // Global mute
        if ((PwnFilter.pwnMute) && (!(dCache.hasPermission(player, "pwnfilter.bypass.mute")))) {
            event.setCancelled(true);
            return; // No point in continuing.
        }

        if (plugin.getConfig().getBoolean("spamfilter") && !dCache.hasPermission(player,"pwnfilter.bypass.spam")) {
            // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
            if (PwnFilter.lastMessage.containsKey(player) && PwnFilter.lastMessage.get(player).equals(message)) {
                event.setCancelled(true);
                return;
            }
            PwnFilter.lastMessage.put(player, message);

        }

        FilterState state = new FilterState(plugin, message, event.getPlayer(), PwnFilter.EventType.CHAT);

        // Global decolor
        if ((PwnFilter.decolor) && !(dCache.hasPermission(player, "pwnfilter.color"))) {
            // We are changing the state of the message.  Let's do that before any rules processing.
            state.message.decolor();
        }

        // Take the message from the ChatEvent and send it through the filter.
        PwnFilter.ruleset.runFilter(state);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setMessage(state.message.getColoredString());
        }
        if (state.cancel) event.setCancelled(true);
    }

}


