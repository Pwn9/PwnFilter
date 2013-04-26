package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.listener.PwnFilterCommandListener;
import com.pwn9.PwnFilter.listener.PwnFilterEntityListener;
import com.pwn9.PwnFilter.listener.PwnFilterPlayerListener;
import com.pwn9.PwnFilter.listener.PwnFilterSignListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
* A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
* @author tremor77
**/

// TODO: Add support for Anvils and Books
/*
<--
12:32:53          @Amaranth | Sage905: For anvils just use InventoryClickEvent. If the slot is for an anvil and is of
 type result the player has crafted the item and you should be able to modify the itemmeta
 */

public class PwnFilter extends JavaPlugin {
    
    public Boolean pwnMute = false;
    List<String> cmdlist;
    List<String> cmdblist;
    public boolean decolor, logfileEnable, debugEnable;
    public HashMap<Player, String> killedPlayers = new HashMap<Player,String>();

    private RuleSet ruleset;

	public void onEnable() {

        // Initialize and load configuration
        saveDefaultConfig();

        // Create a new RuleSet object, loading in the rulesFile
        ruleset = new RuleSet(this);
        ruleset.init(getRulesFile());


        logfileEnable = getConfig().getBoolean("logfile");

        decolor = getConfig().getBoolean("decolor");
        debugEnable = getConfig().getBoolean("debug");

        cmdlist = getConfig().getStringList("cmdlist");
        cmdblist = getConfig().getStringList("cmdblist");

        // Register Chat Handler
        new PwnFilterPlayerListener(this);
        new PwnFilterEntityListener(this);

        // Register Command Handler, if configured
        if (getConfig().getBoolean("commandfilter")) new PwnFilterCommandListener(this);

        // Register Sign Handler, if configured
        if (getConfig().getBoolean("signfilter")) new PwnFilterSignListener(this);

    }
    
    public void onDisable() {
    	ruleset = null;
        closeLog();
    }

    @Override   
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args ) {

        if (cmd.getName().equalsIgnoreCase("pfreload")) { 		   		
            sender.sendMessage(ChatColor.RED + "[PwnFilter] Reloading rules.txt");
            ruleset = new RuleSet(this);
            if (ruleset.init(getRulesFile())) {
                logToFile("rules.txt reloaded by " + sender.getName());
            } else {
                logToFile("failed to reload rules.txt as requested by " + sender.getName());
            }
            return true;
        }
		else if (cmd.getName().equalsIgnoreCase("pfcls")) {  
            sender.sendMessage(ChatColor.RED + "[PwnFilter] Clearing chat screen");
            logToFile("chat screen cleared by " + sender.getName());
            int i = 0;
            while (i <= 120) {
              getServer().broadcastMessage(" ");
              i++;
            }    
    		return true;
    	}
		else if (cmd.getName().equalsIgnoreCase("pfmute")) {
			if (pwnMute) {
				getServer().broadcastMessage(ChatColor.RED + "Global mute cancelled by " + sender.getName());
	            logToFile("global mute cancelled by " + sender.getName());	
	            pwnMute = false;
			}
			else {
				getServer().broadcastMessage(ChatColor.RED + "Global mute initiated by " + sender.getName());
	            logToFile("global mute initiated by " + sender.getName());
	            pwnMute = true;
			}
    		return true;
    	} 
        return false;
    } 
    

    public void filterChat(AsyncPlayerChatEvent event) {

        final Player player = event.getPlayer();

        // Global mute
        if ((pwnMute) && (!(player.hasPermission("pwnfilter.bypassmute")))) {
            event.setCancelled(true);
            return; // No point in continuing.
        }

        // Global decolor
        if ((decolor) && (!(player.hasPermission("pwnfilter.color")))) {
            // We are changing the state of the message.  Let's do that before any rules processing.
            event.setMessage(ChatColor.stripColor(event.getMessage()));
        }

        // Now apply the rules
        ruleset.apply(event);

    }

    /**
     * The sign handler has extra work to do that the chat doesn't:
     * 1. Take lines of sign and aggregate them into one string for processing
     * 2. Feed them into the filter.
     * 3. Re-split the lines so they can be placed on the sign.
     * @param event
     */
    public void filterSign(SignChangeEvent event) {
        ruleset.apply(event);
    }

    
    public void filterCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String rawmessage = event.getMessage();
        //Gets the actual command as a string
        String cmdmessage = message.substring(1).split(" ")[0];
        Player player = event.getPlayer();
        String pname = player.getName();

        if ((cmdlist.isEmpty()) || (cmdlist.contains(cmdmessage))
                && !(cmdblist.contains(cmdmessage))
                && !(player.hasPermission(("pwnfilter.bypass")))) {

        // Hmm.. If we are under global mute, do we want to prevent players from executing commands?
//	    	// Global mute
//	    	if ((pwnMute) && (!(player.hasPermission("pwnfilter.bypassmute")))) {
//	    			event.setCancelled(true);
//	    	}

	    	// Global decolor
	    	if ((decolor) && (!(player.hasPermission("pwnfilter.color")))) {
	    		message = ChatColor.stripColor(message);
	    	}

            // Now apply the rules
            ruleset.apply(event);

//	    		if (matched) {
//	    			// Check for any ignore statements, made faster by grouping together v2.1.1
//	    			if (line.startsWith("ignore")) {
//		        		if (line.startsWith("ignore user ")) {
//		        			String users = line.substring(12);
//		    				valid = true;
//		        			for (String check : users.split(" ")) {
//		        				if (pname.equalsIgnoreCase(check)) {
//		        					matched = false;
//		        					break;
//		        				}
//		        			}
//		        		}
//		        		if (line.startsWith("ignore permission ")) {
//		        			String perms = line.substring(18);
//		    				valid = true;
//		    				for (String check : perms.split(" ")) {
//		    					if (player.hasPermission(check)) {
//			        				matched = false;
//			        				break;
//		    					}
//		        			}
//		        		}
//		        		if (line.startsWith("ignore string ")) {
//		        			String ignorestring = line.substring(14);
//		    				valid = true;
//		    				for (String check : ignorestring.split("\\|")) {
//		    					if (ChatColor.stripColor(message.replaceAll("&([0-9a-fk-or])", "\u00A7$1")).toUpperCase().contains(check.toUpperCase())) {
//			        				matched = false;
//			        				break;
//		                        }
//		        			}
//		        		}
//		        		if (line.startsWith("ignore command ")) {
//		        			String ignorecmd = line.substring(15);
//		    				valid = true;
//		    				for (String check : ignorecmd.split("\\|")) {
//		    					if (cmdmessage.toUpperCase().equals(check.toUpperCase())) {
//			        				matched = false;
//			        				break;
//		                        }
//		        			}
//		        		}
//	    			}
//	    			// Check for any require statements
//	    			if (line.startsWith("require")) {
//		        		if (line.startsWith("require user ")) {
//		        			String users = line.substring(13);
//		    				valid = true;
//		    				Boolean found = false;
//		        			for (String check : users.split(" ")) {
//		        				if (pname.equalsIgnoreCase(check)) {
//		        					found = true;
//		        					break;
//		        				}
//		        			}
//		        			matched = found;
//		        		}
//		        		if (line.startsWith("require permission ")) {
//		        			String perms = line.substring(19);
//		    				valid = true;
//		    				Boolean found = false;
//		    				for (String check : perms.split(" ")) {
//			        			if (player.hasPermission(check)) {
//			        				found = true;
//			        				break;
//			        			}
//		    				}
//		        			matched = found;
//		        		}
//		        		if (line.startsWith("require command ")) {
//		        			String reqcmd = line.substring(16);
//		    				valid = true;
//		    				Boolean found = false;
//		    				for (String check : reqcmd.split("\\|")) {
//		    					if (cmdmessage.toUpperCase().equals(check.toUpperCase())) {
//			        				found = true;
//			        				break;
//			        			}
//		    				}
//		        			matched = found;
//		        		}
//	    			}
//	    			// Finally check for any then statements
//	    			if (line.startsWith("then")) {
//						if (line.startsWith("then replace ")) {
//							message = ChatColor.stripColor(message.replaceAll("&([0-9a-fk-or])", "\u00A7$1"));
//							message = this.replacePattern(message, regex, line.substring(13));
//							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//			    			valid = true;
//						}
//						if (line.matches("then replace")) {
//							message = ChatColor.stripColor(message.replaceAll("&([0-9a-fk-or])", "\u00A7$1"));
//							message = this.replacePattern(message, regex, "");
//							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//							valid = true;
//						}
//						if (line.startsWith("then rewrite ")) {
//							message = this.replacePattern(message, regex, line.substring(13));
//							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//			    			valid = true;
//						}
//						if (line.matches("then rewrite")) {
//							message = this.replacePattern(message, regex, "");
//							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//							valid = true;
//						}
//						if (line.startsWith("then randrep ")) {
//                            message = ChatColor.stripColor(message.replaceAll("&([0-9a-fk-or])", "\u00A7$1"));
//							message = this.replacePatternRandom(message, regex, line.substring(13));
//							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//			    			valid = true;
//						}
//						if (line.startsWith("then lower")) {
//                            message = ChatColor.stripColor(message.replaceAll("&([0-9a-fk-or])", "\u00A7$1"));
//							message = this.replacePatternLower(message, regex);
//							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//							valid = true;
//						}
//						if (line.startsWith("then deny")) {
//							cancel = true;
//			    			valid = true;
//						}
//						// aliasing, commands and console
//						if (line.startsWith("then command ")) {
//							commandcmd = line.substring(13);
//							command = true;
//			    			valid = true;
//						}
//						if (line.matches("then command")) {
//							commandcmd = message;
//							command = true;
//			    			valid = true;
//						}
//						if (line.startsWith("then cmdchain ")) {
//							commandcmd = line.substring(14);
//							commandchain = true;
//			    			valid = true;
//						}
//						if (line.startsWith("then console ")) {
//							consolecmd = line.substring(13);
//							console = true;
//							valid = true;
//						}
//						if (line.startsWith("then conchain ")) {
//							consolecmd = line.substring(14);
//							consolechain = true;
//			    			valid = true;
//						}
//
//						// Punishment stuffs start here
//						if (line.startsWith("then warn ")) {
//							warnmsg = line.substring(10);
//							warn = true;
//			    			valid = true;
//						}
//						if (line.matches("then warn")) {
//							warn = true;
//			    			valid = true;
//						}
//						if (line.startsWith("then kick ")) {
//							kickmsg = line.substring(10);
//							kick = true;
//			    			valid = true;
//						}
//						if (line.matches("then kick")) {
//							kick = true;
//			    			valid = true;
//						}
//						if (line.startsWith("then kill ")) {
//							killmsg = line.substring(10);
//							kill = true;
//			    			valid = true;
//						}
//						if (line.matches("then kill")) {
//							kill = true;
//			    			valid = true;
//						}
//						if (line.startsWith("then burn ")) {
//							burnmsg = line.substring(10);
//							burn = true;
//			    			valid = true;
//						}
//						if (line.matches("then burn")) {
//							burn = true;
//			    			valid = true;
//						}
//
//						// abort, log, debug stuff
//						if (line.startsWith("then abort")) {
//							aborted = true;
//			    			valid = true;
//						}
//						if (line.matches("then log")) {
//							log = true;
//			    			valid = true;
//						}
//						if (line.matches("then debug")) {
//							System.out.println("[PwnFilter] Debug match: " + regex);
//							System.out.println("[PwnFilter] Debug original: " + rawmessage);
//							System.out.println("[PwnFilter] Debug matched: " + matched_msg);
//							System.out.println("[PwnFilter] Debug current: " + message);
//							System.out.println("[PwnFilter] Debug log: " + (log?"yes":"no"));
//							System.out.println("[PwnFilter] Debug deny: " + (cancel?"yes":"no"));
//			    			valid = true;
//						}
//	    			}
//		    		if (!valid) {
//		    			logger.warning("Ignored syntax error in rules.txt: " + line);
//		    		}
//	    		}
//	    	}
//	    	// Perform flagged actions
//	    	if (log) {
//	    		logToFile(matchLogMsg);
//	    		if (cancel){
//	    			logToFile("SENT <"+player.getName() + "> message cancelled by deny rule.");
//	    		}
//	    		logToFile("SENT <"+player.getName() + "> " + message);
//	    	}
//	    	if (cancel) {
//	    		event.setCancelled(true);
//	    	}
//	    	else {
//				event.setMessage(message);
//			}
//	    	if (command) {
//				event.setCancelled(true);
//	    		commandcmd = commandcmd.replaceAll("&world", player.getLocation().getWorld().getName());
//	            commandcmd = commandcmd.replaceAll("&player", player.getName());
//	            commandcmd = commandcmd.replaceAll("&string", message);
//	            commandcmd = commandcmd.replaceAll("&rawstring", rawmessage);
//	            logToFile("Helped " + player.getName() + " execute command: " + commandcmd);
//				player.chat("/" + commandcmd);
//			}
//	    	if (commandchain) {
//				event.setCancelled(true);
//	    		commandcmd = commandcmd.replaceAll("&world", player.getLocation().getWorld().getName());
//	            commandcmd = commandcmd.replaceAll("&player", player.getName());
//	            commandcmd = commandcmd.replaceAll("&string", message);
//	            commandcmd = commandcmd.replaceAll("&rawstring", rawmessage);
//	            String cmdchain[] = commandcmd.split("\\|");
//	            for (String cmds : cmdchain) {
//		            logToFile("Helped " + player.getName() + " execute command: " + cmds);
//					player.chat("/" + cmds);
//	            }
//			}
//	    	if (console) {
//	    		consolecmd = consolecmd.replaceAll("&world", player.getLocation().getWorld().getName());
//	            consolecmd = consolecmd.replaceAll("&player", player.getName());
//	            consolecmd = consolecmd.replaceAll("&string", message);
//	            consolecmd = consolecmd.replaceAll("&rawstring", rawmessage);
//	            logToFile("Sending console command: " + consolecmd);
//	    		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consolecmd);
//	    	}
//	    	if (consolechain) {
//	    		consolecmd = consolecmd.replaceAll("&world", player.getLocation().getWorld().getName());
//	    		consolecmd = consolecmd.replaceAll("&player", player.getName());
//	    		consolecmd = consolecmd.replaceAll("&string", message);
//	    		consolecmd = consolecmd.replaceAll("&rawstring", rawmessage);
//	            String conchain[] = consolecmd.split("\\|");
//	            for (String cmds : conchain) {
//		            logToFile("Sending console command: " + cmds);
//		    		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds);
//	            }
//			}
//	    	if (warn) {
//				warnmsg = warnmsg.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//	    		final Player fplayer = player;
//	    		final String fwarning = warnmsg;
//	            Bukkit.getScheduler().runTask(this, new Runnable() {
//	                public void run() {
//	                	logToFile("Warned " + fplayer.getName() + ": " + fwarning);
//	        	    	if (!fwarning.matches("")) {
//	        	    		fplayer.sendMessage(fwarning);
//	        	    	}
//	                }
//	            });
//	    	}
//	    	if (kick) {
//				kickmsg = kickmsg.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//	    		final Player fplayer = player;
//	    		final String freason = kickmsg;
//	            Bukkit.getScheduler().runTask(this, new Runnable() {
//	                public void run() {
//	                	fplayer.kickPlayer(freason);
//	                	logToFile("Kicked " + fplayer.getName() + ": " + freason);
//	                }
//	            });
//	    	}
//	    	if (kill) {
//				killmsg = killmsg.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//	    		final Player fplayer = player;
//	    		final String fwarning = killmsg;
//	            Bukkit.getScheduler().runTask(this, new Runnable() {
//	                public void run() {
//	                	fplayer.setHealth(0);
//	                	logToFile("Killed " + fplayer.getName() + ": " + fwarning);
//	        	    	if (!fwarning.matches("")) {
//	        	    		fplayer.sendMessage(fwarning);
//	        	    	}
//	                }
//	            });
//	    	}
//	    	if (burn) {
//				burnmsg = burnmsg.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
//	    		final Player fplayer = player;
//	    		final String fwarning = burnmsg;
//	            Bukkit.getScheduler().runTask(this, new Runnable() {
//	                public void run() {
//	                	fplayer.setFireTicks(5000);
//	                	logToFile("Burned " + fplayer.getName() + ": " + fwarning);
//	        	    	if (!fwarning.matches("")) {
//	        	    		fplayer.sendMessage(fwarning);
//	        	    	}
//	                }
//	            });
//	    	}
//        }
//        }
//        }
//    }

        }
    }

    private static PrintWriter pw = null; // Keep the printwriter persistent, so we don't have to open it each time.

    public void logToFile(String message) {
    	// send to the console as info any logTofiles
    	getLogger().info(message);

    	if (logfileEnable) {
            if (pw == null) {
                try {
                    pw = new PrintWriter(new File(getDataFolder(), "pwnfilter.log"));
                }
                catch (IOException ex)
                {
                    getLogger().log(Level.SEVERE, null, ex);
                }
            }
            pw.println(getDate() +" "+ message);
            pw.flush();
        }
    }

    public static void closeLog()
    {
        if (pw != null)
        {
            pw.close();
            pw = null;
        }
    }


    public String getDate() {
    	  String s;
    	  Format formatter;
    	  Date date = new Date(); 
    	  formatter = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");
    	  s = formatter.format(date);
    	  return s;
    }

    /**
     * Selects string from the first not null of: message, default from config.yml or null.
     * Converts & to u00A7
     * Used by Action.init() methods.
     * @return String containing message to be used.
     */
    public static String prepareMessage(String message, String configName) {
        String retVal;
        if (message.isEmpty()) {
            // TODO: Feels wrong...
            String defmsg = Bukkit.getPluginManager().getPlugin("PwnFilter").getConfig().getString(configName);
            retVal = (defmsg != null) ? defmsg : "";
        } else {
            retVal = message;
        }
        return retVal.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    }

    private File getRulesFile() {

        File dataFolder = getDataFolder();
        File rulesFile = null;
        String fname = "rules.txt";

        // Ensure that directory exists
        if(!dataFolder.exists()) {
            dataFolder.mkdirs();
            logToFile("created directory '" + dataFolder.getName() + "'" );
        }

        rulesFile = new File(dataFolder,fname);
        // Check to see if rules file exists.  If not, create a basic file from template
        if (!rulesFile.exists()) {
            try{
                rulesFile.createNewFile();
                BufferedInputStream fin = new BufferedInputStream(this.getResource(fname));
                FileOutputStream fout = new FileOutputStream(rulesFile);
                byte[] data = new byte[1024];
                int c;
                while ((c = fin.read(data, 0, 1024)) != -1)
                    fout.write(data, 0, c);
                fin.close();
                fout.close();
                logToFile("created config file '" + fname + "'" );
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return rulesFile;
    }
}

