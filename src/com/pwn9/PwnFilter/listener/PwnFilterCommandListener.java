package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
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

        pm.registerEvent(PlayerCommandPreprocessEvent.class, this, p.cmdPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerCommandPreprocess((PlayerCommandPreprocessEvent)e); }
                },
                plugin);
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
    	plugin.filterCommand(event);
    }  
}
