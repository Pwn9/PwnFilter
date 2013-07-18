package com.pwn9.PwnFilter.listener;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pwn9.PwnFilter.PwnFilter;

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
	public static HashMap<String, String> messages = new HashMap<String, String>();

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup player messages on quit
		if (event.getPlayer() != null && messages.containsKey(event.getPlayer().getName())) {
			messages.remove(event.getPlayer().getName());
		}
	}	
	
    public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (event.getPlayer().hasPermission("pwnfilter.bypass.chat")) {
			event.setCancelled(false);
			return;
		}
        Boolean filterSpam = plugin.getConfig().getBoolean("chatspamfilter");
        
        if (filterSpam) {
        	if (!(event.getPlayer().hasPermission("pwnfilter.bypass.spam"))) {
		        if (messages.containsKey(event.getPlayer().getName()) && messages.get(event.getPlayer().getName()).equals(event.getMessage())) {
					event.setCancelled(true);
					return;
				}
				messages.put(event.getPlayer().getName(), event.getMessage());
        	}
        }
    	plugin.filterChat(event);
    }  
}