package com.pwn9.PwnFilter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.Listener;

/**
* A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
*
* Handle events for all Player related events
*
* @author tremor77
*/

public class PwnFilterPlayerListener implements Listener {
    private final PwnFilter plugin;

    public PwnFilterPlayerListener(PwnFilter instance) {
        plugin = instance;
    }
    
    // Insert Player related code here  Set to lowest instead of highest? 
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        String pname = player.getName();

        // Permissions Check, if player has bypass permissions, then skip everything.
        if (!(player.hasPermission("pwnfilter.bypass"))) {
	    	Boolean cancel = false;
	    	Boolean kick = false;
	    	Boolean console = false;
	    	String consolecmd = "";
	    	String reason = "PwnFilter";
	    	Boolean command = false;
	    	String commandcmd = "";
	    	Boolean matched = false;
	    	String regex = "";
	    	String matched_msg = "";
	    	Boolean log = false;
	    	String warning = "";
	    	Boolean aborted = false;
	
	    	Boolean valid;	
	    	
	    	// Apply rules 
	    	for (String line : plugin.rules) {
	    		if (aborted) { break; } 
	    		valid = false;
	    		    
	    		if (line.startsWith("match ")) {
	    			regex = line.substring(6); 			
	    			//matched = plugin.matchPattern(message, regex);
	    			//replaced ^ with this to get matches with color codes inside
	    			matched = plugin.matchPattern(ChatColor.stripColor(message.replaceAll("\\$([0-9a-fk-or])", "\u00A7$1")), regex); 			
	    			if (matched) {
	    				matched_msg = message;
	    			}
	    			valid = true;
	    		}
	    		if (matched) {  	
	    			//if a match occurs what do we do now!?  			
	        		if (line.startsWith("ignore user ")) {
	        			String users = line.substring(12);
	    				valid = true;
	        			for (String check : users.split(" ")) {
	        				if (pname.equalsIgnoreCase(check)) {
	        					matched = false;
	        					break;
	        				}
	        			}
	        		}
	        		if (line.startsWith("ignore permission ")) {
	        			String perms = line.substring(18);
	    				valid = true;
	    				for (String check : perms.split(" ")) {
	    					if (player.hasPermission(check)) {
		        				matched = false;
		        				break;
	    					}
	        			}
	        		} 		
	        		// New for 1.4 - Ignore String, test 2
	        		if (line.startsWith("ignore string ")) {
	        			String ignorestring = line.substring(14);
	    				valid = true;
	    				for (String check : ignorestring.split("\\|")) {
	    					if (ChatColor.stripColor(message.replaceAll("\\$([0-9a-fk-or])", "\u00A7$1")).toUpperCase().indexOf(check.toUpperCase()) != -1) {
		        				matched = false;
		        				break;
	                        }	
	        			}
	        		}       	
	        		if (line.startsWith("require user ")) {
	        			String users = line.substring(13);
	    				valid = true;
	    				Boolean found = false;
	        			for (String check : users.split(" ")) {
	        				if (pname.equalsIgnoreCase(check)) {
	        					found = true;
	        					break;
	        				}
	        			}
	        			matched = found;
	        		}
	        		if (line.startsWith("require permission ")) {
	        			String perms = line.substring(19);
	    				valid = true;
	    				Boolean found = false;
	    				for (String check : perms.split(" ")) {
		        			if (player.hasPermission(check)) {
		        				found = true;
		        				break;
		        			}
	    				}
	        			matched = found;
	        		}             		
					if (line.startsWith("then replace ")) {	
						// clean out the color codes for the replacement
						message = ChatColor.stripColor(message.replaceAll("\\$([0-9a-fk-or])", "\u00A7$1"));
						// check and replace cleaned message for pattern matches
						message = plugin.replacePattern(message, regex, line.substring(13));
						// re-write message with &color replacements
						message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");						
		    			valid = true;
					}
					if (line.matches("then replace")) {	
						// clean out the color codes for the replacement
						message = ChatColor.stripColor(message.replaceAll("\\$([[0-9a-fk-or]])", "\u00A7$1"));						
						// check and replace cleaned message for pattern matches
						message = plugin.replacePattern(message, regex, "");						
						// re-write message with &color replacements
						message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
						valid = true;
					}
					if (line.startsWith("then rewrite ")) {									
						// check and replace message for pattern matches while ignoring color codes
						message = plugin.replacePattern(message, regex, line.substring(13));
						// re-write message with &color replacements
						message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
		    			valid = true;
					}
					if (line.matches("then rewrite")) {
						// check and replace message for pattern matches while ignoring color codes
						message = plugin.replacePattern(message, regex, "");
						// re-write message with &color replacements
						message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
						valid = true;
					}					
					if (line.startsWith("then warn ")) {
						warning = line.substring(10);
		    			valid = true;
					}
					if (line.matches("then warn")) {
						warning = event.getMessage();
		    			valid = true;
					}
					if (line.matches("then log")) {
						log = true;
		    			valid = true;
					}
					if (line.startsWith("then command ")) {		
						commandcmd = line.substring(13);
						command = true;
		    			valid = true;
					}
					if (line.matches("then command")) {
						commandcmd = event.getMessage();
						command = true;
		    			valid = true;
					}
					if (line.matches("then debug")) {
						System.out.println("[PwnFilter] Debug match: " + regex);
						System.out.println("[PwnFilter] Debug original: " + event.getMessage());
						System.out.println("[PwnFilter] Debug matched: " + matched_msg);
						System.out.println("[PwnFilter] Debug current: " + message);
						System.out.println("[PwnFilter] Debug warning: " + (warning.equals("")?"(none)":warning));
						System.out.println("[PwnFilter] Debug log: " + (log?"yes":"no"));
						System.out.println("[PwnFilter] Debug deny: " + (cancel?"yes":"no"));
		    			valid = true;
					}
					if (line.startsWith("then deny")) {
						cancel = true;
		    			valid = true;
					}
					if (line.startsWith("then kick ")) {
						reason = line.substring(10);
		    			valid = true;
					}
					if (line.startsWith("then kick")) {
						kick = true;
		    			valid = true;
					}
					if (line.startsWith("then console ")) {
						consolecmd = line.substring(13);
						console = true;
						valid = true;
					}
					if (line.startsWith("then abort")) {
						aborted = true;
		    			valid = true;
					}
		    		if (valid == false) {
		    			plugin.logger.warning("[PwnFilter] Ignored syntax error in rules.txt: " + line);    			
		    		}
	    		}
	    	}	
	    	// Perform flagged actions
	    	if (log) {
	    		plugin.logger.info("[PwnFilter] " +  player.getName() + "> " + event.getMessage());
	    	}	
	    	if (!warning.matches("")) {
	    		player.sendMessage(ChatColor.RED + "[PwnFilter] " + warning);
	    	}
	    	if (cancel == true) {
	    		event.setCancelled(true);
	    	}   	
	    	if (command == true) {
				event.setCancelled(true);
	    		commandcmd = commandcmd.replaceAll("&world", player.getLocation().getWorld().getName());
	            commandcmd = commandcmd.replaceAll("&player", player.getName());
	            commandcmd = commandcmd.replaceAll("&string", message);           
				plugin.logger.info("[PwnFilter] Helped " + player.getName() + " execute command: " + commandcmd);				
				player.chat("/" + commandcmd);		
			} 
	    	else {
				event.setMessage(message);
			}     	
	    	if (kick) {
	    		player.kickPlayer(reason);
	    		plugin.logger.info("[PwnFilter] Kicked " + player.getName() + ": " + reason);
	    	}  	
	    	if (console) {
	    		consolecmd = consolecmd.replaceAll("&world", player.getLocation().getWorld().getName());
	            consolecmd = consolecmd.replaceAll("&player", player.getName());
	            consolecmd = consolecmd.replaceAll("&string", message);
	    		plugin.logger.info("[PwnFilter] sending console command: " + consolecmd);
	    		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consolecmd);
	    	}       	   	  	   	
        }   	
    }    
}