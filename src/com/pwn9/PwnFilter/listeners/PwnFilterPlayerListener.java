package com.pwn9.PwnFilter.listeners;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
* A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
* Handle events for all Player related events
* @author tremor77
*/

public class PwnFilterPlayerListener implements Listener {
    private final PwnFilter plugin;
	public PwnFilterPlayerListener(PwnFilter plugin) {
	    this.plugin = plugin;
	}
    public void onPlayerChat(AsyncPlayerChatEvent event) {
    	plugin.filterChat(event);
    }  
}