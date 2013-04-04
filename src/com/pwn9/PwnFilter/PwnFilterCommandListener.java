package com.pwn9.PwnFilter;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
* A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
* Handle events for all Player related events
* @author tremor77
*/

public class PwnFilterCommandListener implements Listener {
    private final PwnFilter plugin;
	public PwnFilterCommandListener(PwnFilter plugin) {
	    this.plugin = plugin;
	}
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	plugin.filterCommand(event);
    }  
}
