package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        String pName = player.getName();
        String message = event.getMessage();

        //Gets the actual command as a string
        String cmdmessage = event.getMessage().substring(1).split(" ")[0];

        if (event.getPlayer().hasPermission(("pwnfilter.bypass.commands"))) return;

        if ((plugin.cmdlist.isEmpty()) || (plugin.cmdlist.contains(cmdmessage))
                && !(plugin.cmdblist.contains(cmdmessage))) {

            if (plugin.getConfig().getBoolean("commandspamfilter") && !player.hasPermission("pwnfilter.bypass.spam")) {
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
                return;
            }

            // Global decolor
            if ((PwnFilter.decolor) && (!(player.hasPermission("pwnfilter.color")))) {
                event.setMessage(ChatColor.stripColor(message));
            }

            plugin.filterCommand(event);

        }
    }
}
