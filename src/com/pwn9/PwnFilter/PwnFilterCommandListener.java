package com.pwn9.PwnFilter;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
	public static HashMap<String, String> messages = new HashMap<String, String>();

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup player messages on quit
		if (event.getPlayer() != null && messages.containsKey(event.getPlayer().getName())) {
			messages.remove(event.getPlayer().getName());
		}
	}		
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().hasPermission("pwnfilter.bypass")) {
			event.setCancelled(false);
			return;
		}
        Boolean filterSpam = plugin.getConfig().getBoolean("spamfilter");
        if (filterSpam) {
	        if (messages.containsKey(event.getPlayer().getName()) && messages.get(event.getPlayer().getName()).equals(event.getMessage())) {
				event.setCancelled(true);
				return;
			}
			messages.put(event.getPlayer().getName(), event.getMessage());	
        }    	
    	plugin.filterCommand(event);
    }  
}
