package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

/**
* Apply the filter to commands.
*/

public class PwnFilterCommandListener implements Listener {
    private final PwnFilter plugin;

    public PwnFilterCommandListener(PwnFilter p) {
	    plugin = p;
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvent(PlayerCommandPreprocessEvent.class, this, PwnFilter.cmdPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerCommandPreprocess((PlayerCommandPreprocessEvent)e); }
                },
                plugin);
        PwnFilter.logger.info("Activated CommandListener with Priority Setting: " + PwnFilter.cmdPriority.toString());

    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        DataCache dCache = PwnFilter.dataCache;

        if (dCache.hasPermission(player, "pwnfilter.bypass.commands")) return;

        String message = event.getMessage();

        //Gets the actual command as a string
        String cmdmessage = message.substring(1).split(" ")[0];

        if (!plugin.cmdlist.isEmpty() && !plugin.cmdlist.contains(cmdmessage)) return;
        if (plugin.cmdblist.contains(cmdmessage)) return;

        // Global mute
        if ((PwnFilter.pwnMute) && (!(dCache.hasPermission(player, "pwnfilter.bypass.mute")))) {
            event.setCancelled(true);
            return;
        }

        // Simple Spam filter TODO: Make # of repeat messages configurable (Will help with booscooldowns)
        if (plugin.getConfig().getBoolean("commandspamfilter") && !player.hasPermission("pwnfilter.bypass.spam")) {
            // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
            if (PwnFilter.lastMessage.containsKey(player) && PwnFilter.lastMessage.get(player).equals(message)) {
                event.setCancelled(true);
                return;
            }
            PwnFilter.lastMessage.put(player, message);

        }


        FilterState state = new FilterState(plugin, message, player,
                PwnFilter.EventType.COMMAND);

        // Global decolor
        if ((PwnFilter.decolor) && !(dCache.hasPermission(player,"pwnfilter.color"))) {
            state.message.decolor();
        }


        // Take the message from the Command Event and send it through the filter.

        PwnFilter.ruleset.runFilter(state);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setMessage(state.message.getColoredString());
        }

        if (state.cancel) event.setCancelled(true);

    }
}
