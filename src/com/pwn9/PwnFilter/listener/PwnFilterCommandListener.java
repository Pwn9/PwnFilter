package com.pwn9.PwnFilter.listener;

import java.util.HashMap;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.pwn9.PwnFilter.PwnFilter;

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
    	 String message = event.getMessage();
         //Gets the actual command as a string
         String cmdmessage = message.substring(1).split(" ")[0];
         Boolean filterSpam = plugin.getConfig().getBoolean("commandspamfilter");
         List<String> cmdblist;
         cmdblist = plugin.getConfig().getStringList("cmdblist");

    	if (event.getPlayer().hasPermission("pwnfilter.bypass.commands")) {
			//event.setCancelled(false);
			return;
		}
    	else if (cmdblist.contains(cmdmessage)) {
			//event.setCancelled(false);
			return;    				
    	}
    	else {
    		if (filterSpam) {
	        	// Player has spam bypass permission, don't filter
	        	if (!(event.getPlayer().hasPermission("pwnfilter.bypass.spam"))) {
			        if (messages.containsKey(event.getPlayer().getName()) && messages.get(event.getPlayer().getName()).equals(event.getMessage())) {
						event.setCancelled(true);
						// Could add a warning message here
						return;
					}
					messages.put(event.getPlayer().getName(), event.getMessage());	
	        	}
    		}
    		plugin.filterCommand(event);
    	}
    }  
}
