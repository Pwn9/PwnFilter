package com.pwn9.PwnFilter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.Listener;

/**
* A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
* Handle events for all Player related events
* @author tremor77
*/

public class PwnFilterPlayerListenerHighest implements Listener {
    private final PwnFilter plugin;
	public PwnFilterPlayerListenerHighest(PwnFilter plugin) {
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);    
	    this.plugin = plugin;
	}
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
    	plugin.filterChat(event);
    }  
}