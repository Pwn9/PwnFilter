package com.pwn9.PwnFilter;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.*;
import java.util.logging.Logger;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
* A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
* @author tremor77
**/
public class PwnFilter extends JavaPlugin {
    
    String baseDir = "plugins/PwnFilter";
    Boolean pwnMute = false;
    
    public CopyOnWriteArrayList<String> rules = new CopyOnWriteArrayList<String>();
    private ConcurrentHashMap<String, Pattern> patterns = new ConcurrentHashMap<String, Pattern>(); 
	public final Logger logger = Logger.getLogger("Minecraft.PwnFilter");    
	
	public void onEnable() {	
    	loadRules();	
    	this.saveDefaultConfig();
    	String priority = getConfig().getString("priority");
    	this.getLogger().info("Priority Setting: "+priority);  
    	if (priority.equals("lowest")) {
    		new PwnFilterPlayerListenerLowest(this);
    	}
    	else if (priority.equals("low")) {
			new PwnFilterPlayerListenerLow(this);
    	}
    	else if (priority.equals("normal")) {
			new PwnFilterPlayerListenerNormal(this);
    	}
    	else if (priority.equals("high")) {
			new PwnFilterPlayerListenerHigh(this);
    	}
    	else if (priority.equals("highest")) {
			new PwnFilterPlayerListenerHighest(this);
    	}
    	else {
			new PwnFilterPlayerListenerLowest(this);
    	}
    }
    
    public void onDisable() {
    	rules.clear();
    	patterns.clear();
    }

    @Override   
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args ) {
        if (cmd.getName().equalsIgnoreCase("pfreload")) { 		   		
            sender.sendMessage(ChatColor.RED + "[PwnFilter] Reloading rules.txt");
            this.getLogger().info("rules.txt reloaded by " + sender.getName());        
    		rules.clear();
    		patterns.clear();
    		loadRules();
    		return true;
        }
		else if (cmd.getName().equalsIgnoreCase("pfcls")) {  
            sender.sendMessage(ChatColor.RED + "[PwnFilter] Clearing chat screen");
            this.getLogger().info("chat screen cleared by " + sender.getName());
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
	            this.getLogger().info("global mute cancelled by " + sender.getName());	
	            pwnMute = false;
			}
			else {
				getServer().broadcastMessage(ChatColor.RED + "Global mute initiated by " + sender.getName());
	            this.getLogger().info("global mute initiated by " + sender.getName());
	            pwnMute = true;
			}
    		return true;
    	} 
        return false;
    } 
    
    private void loadRules() {
    	String fname = "plugins/PwnFilter/rules.txt";
    	File f;
    	// Ensure that directory exists
    	String pname = "plugins/PwnFilter";
    	f = new File(pname);
    	if (!f.exists()) {
    		if (f.mkdir()) {
    			this.getLogger().info("created directory '" + pname + "'" );
    		}
    	}
    	// Ensure that rules.txt exists
    	f = new File(fname);
    	if (!f.exists()) {
			BufferedWriter output;
			String newline = System.getProperty("line.separator");
			try {
				output = new BufferedWriter(new FileWriter(fname));
				output.write("# PwnFilter rules.txt - Define Regular Expression Rules" + newline);
				output.write("# SAMPLE RULES http://dev.bukkit.org/server-mods/pwnfilter/" + newline);
				output.write("# NOTE: ALL MATCHES AUTOMATICALLY IGNORE CASE" + newline);
				output.write("# Each rule must have one 'match' statement and atleast one 'then' statement" + newline);
				output.write("# match <regular expression>" + newline);
				output.write("# ignore|require <user|permission|string> *(optional)" + newline);				
				output.write("# then <replace|rewrite|warn|log|deny|debug|kick|kill|burn|command|console> <string>" + newline);
				output.write("# For more details visit http://dev.bukkit.org/server-mods/pwnfilter/" + newline);
				output.write("" + newline);
				output.write("# EXAMPLES" + newline);
				output.write("" + newline);
				output.write("# Replace F Bomb variants with fudge. Also catches ffffuuuccckkk" + newline);
				output.write("match f+u+c+k+|f+u+k+|f+v+c+k+|f+u+q+" + newline);
				output.write("then replace fudge" + newline);
				output.write("then warn Watch your language please" + newline);
				output.write("then log" + newline);
				output.write("" + newline);
				output.write("# Replace a list of naughty words with meep! Let a certain permission swear." + newline);
				output.write("match cunt|whore|fag|slut|queer|bitch|bastard" + newline);
				output.write("ignore permission permission.node" + newline);
				output.write("then replace meep" + newline);
				output.write("" + newline);
				output.write("# FIX the .command typo with /command" + newline);
				output.write("match ^\\.(?=[a-z]+)" + newline);
				output.write("then replace" + newline);
				output.write("then command" + newline);
				output.write("" + newline);
				output.write("# Fun: rewrite tremor with pretty colors. Only let player tremor77 use it" + newline);
				output.write("match \\btremor+\\b|\\btrem+\\b" + newline);
				output.write("require user tremor77" + newline);
				output.write("then rewrite &bt&cREM&bor&f" + newline);
				output.close();
				this.getLogger().info("created config file '" + fname + "'" );
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	try {
        	BufferedReader input =  new BufferedReader(new FileReader(fname));
    		String line = null;
    		while (( line = input.readLine()) != null) {
    			line = line.trim();
    			if (!line.matches("^#.*") && !line.matches("")) {
    				rules.add(line);
    				if (line.startsWith("match ") || line.startsWith("replace ") || line.startsWith("rewrite ")) {
    					String[] parts = line.split(" ", 2);
    					compilePattern(parts[1]);
    				}
    			}
    		}
    		input.close();
    	}
    	catch (FileNotFoundException e) {
    		this.getLogger().warning("error reading config file '" + fname + "': " + e.getLocalizedMessage());
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
       
    private void compilePattern(String re) {
    	// Do not re-compile if we already have this pattern 
    	if (patterns.get(re) == null) {
    		try {
    			Pattern pattern = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
    			patterns.put(re, pattern);
    			this.getLogger().fine("successfully compiled regex: " + re);
    		}
    		catch (PatternSyntaxException e) {
    			this.getLogger().warning("failed to compile regex: " + re);
    			this.getLogger().warning(e.getMessage());
    		}
    		catch (Exception e) {
    			this.getLogger().severe("unexpected error while compiling expression '" + re + "'");
    			e.printStackTrace();
    		}
    	}
    }  
    
    public Boolean matchPattern(String msg, String re_from) {
    	Pattern pattern_from = patterns.get(re_from);
    	if (pattern_from == null) {
    		// Pattern failed to compile, ignore
    		this.getLogger().info("ignoring invalid regex: " + re_from);
    		return false;
    	}
    	Matcher matcher = pattern_from.matcher(msg);
    	return matcher.find();
    }
    
    public String replacePattern(String msg, String re_from, String to) {
    	Pattern pattern_from = patterns.get(re_from);
    	if (pattern_from == null) {
    		// Pattern failed to compile, ignore
    		return msg;
    	}
    	Matcher matcher = pattern_from.matcher(msg);
    	return matcher.replaceAll(to);
    }

    public String replacePatternLower(String msg, String re_from) {
    	String text = msg;
    	Matcher m = Pattern.compile(re_from).matcher(text);
    	
    	StringBuilder sb = new StringBuilder();
    	int last = 0;
    	while (m.find()) {
    		sb.append(text.substring(last, m.start()));
    		sb.append(m.group(0).toLowerCase());
    		last = m.end();
    	}
    	sb.append(text.substring(last));
    	return sb.toString();
    }

    public String replacePatternRandom(String msg, String re_from, String to) {
    	Pattern pattern_from = patterns.get(re_from);
    	if (pattern_from == null) {
    		// Pattern failed to compile, ignore
    		return msg;
    	}
    	Matcher matcher = pattern_from.matcher(msg);
    	
    	String[] toRand = to.split("\\|");
    	
		Random random = new Random();
		int randomInt = random.nextInt(toRand.length);
    	return matcher.replaceAll(toRand[randomInt]);
    }
    
    public void filterChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        String pname = player.getName();

        // Permissions Check, if player has bypass permissions, then skip everything.
        if (!(player.hasPermission("pwnfilter.bypass"))) {
	    	// Booleans
        	Boolean cancel = false;
	    	Boolean kick = false;
	    	Boolean kill = false;
	    	Boolean burn = false;
	    	Boolean warn = false;
	    	Boolean console = false;
	    	Boolean command = false;
	    	Boolean commandchain = false;	    	
	    	Boolean matched = false;
	    	Boolean log = false;
	    	Boolean aborted = false;
	    	Boolean valid;	
	    	
	    	// Strings	    	
	    	String consolecmd = "";
	    	String commandcmd = "";
	    	String regex = "";
	    	String matched_msg = "";
	    	
	    	// More Strings (for warns, kick, etc)
	    	String warnmsg = getConfig().getString("warnmsg");
	    	String kickmsg = getConfig().getString("kickmsg");
	    	String killmsg = getConfig().getString("killmsg");
	    	String burnmsg = getConfig().getString("burnmsg");
	    	
	    	if (pwnMute) {
	    		event.setCancelled(true);
	    	}
	    	
	    	// Apply rules 
	    	for (String line : this.rules) {
	    		if (aborted) { break; } 
	    		valid = false;
	    		    
	    		if (line.startsWith("match ")) {
	    			regex = line.substring(6); 			
	    			matched = this.matchPattern(ChatColor.stripColor(message.replaceAll("\\$([0-9a-fk-or])", "\u00A7$1")), regex); 			
	    			if (matched) {
	    				matched_msg = message;
	    			}
	    			valid = true;
	    		}
	    		if (matched) {  
	    			// Check for any ignore statements, made faster by grouping together v2.1.1
	    			if (line.startsWith("ignore")) {		
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
	    			}
	    			// Check for any require statements
	    			if (line.startsWith("require")) {
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
	    			}
	    			// Finally check for any then statements
	    			if (line.startsWith("then")) {
						if (line.startsWith("then replace ")) {	
							// clean out the color codes for the replacement
							message = ChatColor.stripColor(message.replaceAll("\\$([0-9a-fk-or])", "\u00A7$1"));
							// check and replace cleaned message for pattern matches
							message = this.replacePattern(message, regex, line.substring(13));
							// re-write message with &color replacements
							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");						
			    			valid = true;
						}
						if (line.matches("then replace")) {	
							// clean out the color codes for the replacement
							message = ChatColor.stripColor(message.replaceAll("\\$([[0-9a-fk-or]])", "\u00A7$1"));						
							// check and replace cleaned message for pattern matches
							message = this.replacePattern(message, regex, "");						
							// re-write message with &color replacements
							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
							valid = true;
						}
						if (line.startsWith("then rewrite ")) {									
							// check and replace message for pattern matches while ignoring color codes
							message = this.replacePattern(message, regex, line.substring(13));
							// re-write message with &color replacements
							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
			    			valid = true;
						}
						if (line.matches("then rewrite")) {
							// check and replace message for pattern matches while ignoring color codes
							message = this.replacePattern(message, regex, "");
							// re-write message with &color replacements
							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
							valid = true;
						}							
						if (line.startsWith("then randrep ")) {									
							// check and replace message for pattern matches while ignoring color codes
							message = this.replacePatternRandom(message, regex, line.substring(13));
							// re-write message with &color replacements
							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
			    			valid = true;
						}							
						if (line.startsWith("then lower")) {
							// check and replace message for pattern matches while ignoring color codes
							message = this.replacePatternLower(message, regex);
							// re-write message with &color replacements
							message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
							valid = true;
						}
						if (line.startsWith("then deny")) {
							cancel = true;
			    			valid = true;
						}
						
						// aliasing, commands and console
						if (line.startsWith("then command ")) {		
							commandcmd = line.substring(13);
							command = true;
			    			valid = true;
						}
						if (line.matches("then command")) {
							commandcmd = message;
							command = true;
			    			valid = true;
						}
						if (line.startsWith("then cmdchain ")) {		
							commandcmd = line.substring(14);
							commandchain = true;
			    			valid = true;
						}	
						if (line.startsWith("then console ")) {
							consolecmd = line.substring(13);
							console = true;
							valid = true;
						}					
						
						// Punishment stuffs start here
						if (line.startsWith("then warn ")) {
							warnmsg = line.substring(10);
							warn = true;
			    			valid = true;
						}
						if (line.matches("then warn")) {
							warn = true;
			    			valid = true;
						}						
						if (line.startsWith("then kick ")) {
							kickmsg = line.substring(10);
							kick = true;
			    			valid = true;
						}
						if (line.matches("then kick")) {
							kick = true;
			    			valid = true;
						}
						if (line.startsWith("then kill ")) {
							killmsg = line.substring(10);
							kill = true;
			    			valid = true;							
						}
						if (line.matches("then kill")) {							
							kill = true;
			    			valid = true;
						}		
						if (line.startsWith("then burn ")) {
							burnmsg = line.substring(10);
							burn = true;
			    			valid = true;
						}
						if (line.matches("then burn")) {
							burn = true;
			    			valid = true;
						}	
						
						// abort, log, debug stuff
						if (line.startsWith("then abort")) {
							aborted = true;
			    			valid = true;
						}
						if (line.matches("then log")) {
							log = true;
			    			valid = true;
						}	
						if (line.matches("then debug")) {
							System.out.println("[PwnFilter] Debug match: " + regex);
							System.out.println("[PwnFilter] Debug original: " + event.getMessage());
							System.out.println("[PwnFilter] Debug matched: " + matched_msg);
							System.out.println("[PwnFilter] Debug current: " + message);
							System.out.println("[PwnFilter] Debug log: " + (log?"yes":"no"));
							System.out.println("[PwnFilter] Debug deny: " + (cancel?"yes":"no"));
			    			valid = true;
						}						
	    			}
		    		if (valid == false) {
		    			this.getLogger().warning("ignored syntax error in rules.txt: " + line);    			
		    		}	    		
	    		}
	    	}	
	    	// Perform flagged actions
	    	if (log) {
	    		this.getLogger().info(player.getName() + "> " + event.getMessage());
	    	}	
	    	if (cancel) {
	    		event.setCancelled(true);
	    	}   	
	    	if (command) {
				event.setCancelled(true);
	    		commandcmd = commandcmd.replaceAll("&world", player.getLocation().getWorld().getName());
	            commandcmd = commandcmd.replaceAll("&player", player.getName());
	            commandcmd = commandcmd.replaceAll("&string", message);           
	            this.getLogger().info("helped " + player.getName() + " execute command: " + commandcmd);				
				player.chat("/" + commandcmd);		
			}
	    	if (commandchain) {
				event.setCancelled(true);
	    		commandcmd = commandcmd.replaceAll("&world", player.getLocation().getWorld().getName());
	            commandcmd = commandcmd.replaceAll("&player", player.getName());
	            commandcmd = commandcmd.replaceAll("&string", message);           
	            String cmdchain[] = commandcmd.split("\\|");
	            for (String cmds : cmdchain) {
		            this.getLogger().info("helped " + player.getName() + " execute command: " + cmds);				
					player.chat("/" + cmds);	            	
	            }
			}  	
	    	else {
				event.setMessage(message);
			}	
	    	
	    	if (console) {
	    		consolecmd = consolecmd.replaceAll("&world", player.getLocation().getWorld().getName());
	            consolecmd = consolecmd.replaceAll("&player", player.getName());
	            consolecmd = consolecmd.replaceAll("&string", message);
	            this.getLogger().info("sending console command: " + consolecmd);
	    		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consolecmd);
	    	}	    	
	    	if (warn) {
				warnmsg = warnmsg.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
	    		final Player fplayer = player;
	    		final String fwarning = warnmsg;
	            Bukkit.getScheduler().runTask(this, new Runnable() {
	                public void run() {
	                	logger.info("[PwnFilter] warned " + fplayer.getName() + ": " + fwarning);
	        	    	if (!fwarning.matches("")) {
	        	    		fplayer.sendMessage(fwarning);
	        	    	}
	                }
	            });	 
	    	}	    	
	    	if (kick) {	
				kickmsg = kickmsg.replaceAll("&([0-9a-fk-or])", "\u00A7$1");	    		
	    		final Player fplayer = player;
	    		final String freason = kickmsg;
	            Bukkit.getScheduler().runTask(this, new Runnable() {
	                public void run() {
	                	fplayer.kickPlayer(freason);
	                	logger.info("[PwnFilter] kicked " + fplayer.getName() + ": " + freason);
	                }
	            });	    		
	    	}
	    	if (kill) {	
				killmsg = killmsg.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
	    		final Player fplayer = player;
	    		final String fwarning = killmsg;
	            Bukkit.getScheduler().runTask(this, new Runnable() {
	                public void run() {
	                	fplayer.setHealth(0);
	                	logger.info("[PwnFilter] killed " + fplayer.getName() + ": " + fwarning);
	        	    	if (!fwarning.matches("")) {
	        	    		fplayer.sendMessage(fwarning);
	        	    	}
	                }
	            });	    		
	    	}	
	    	if (burn) {	
				burnmsg = burnmsg.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
	    		final Player fplayer = player;
	    		final String fwarning = burnmsg;
	            Bukkit.getScheduler().runTask(this, new Runnable() {
	                public void run() {
	                	fplayer.setFireTicks(5000);
	                	logger.info("[PwnFilter] burned " + fplayer.getName() + ": " + fwarning);
	        	    	if (!fwarning.matches("")) {
	        	    		fplayer.sendMessage(fwarning);
	        	    	}
	                }
	            });	    		
	    	}	    		    	
        }   	
    }      
}

