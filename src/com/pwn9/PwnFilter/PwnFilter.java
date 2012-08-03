package com.pwn9.PwnFilter;

import java.io.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.*;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;


/**
* A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
*
* @author tremor77
*/
public class PwnFilter extends JavaPlugin {
    
    String baseDir = "plugins/PwnFilter";
    String configFile = "config.yml";

    public CopyOnWriteArrayList<String> rules = new CopyOnWriteArrayList<String>();
    private ConcurrentHashMap<String, Pattern> patterns = new ConcurrentHashMap<String, Pattern>(); 
	public final Logger logger = Logger.getLogger("Minecraft.PwnFilter");
	
    public void onDisable() {
    	rules.clear();
    	patterns.clear();

        // NOTE: All registered events are automatically unregistered when a plugin is disabled
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
    	PluginDescriptionFile pdfFile = this.getDescription();
		logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }

    public void onEnable() {
    	loadRules();
    	
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PwnFilterPlayerListener(this), this);
        
    	// EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
		logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
     
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args ) {
    	String cmdname = cmd.getName().toLowerCase();
        Player player = null;
        if (sender instanceof Player) {
        	player = (Player)sender;
        }
        
        // Reload rules.txt command
        if (cmdname.equals("pwnfilter") && args.length > 0) {
        	if (player == null || player.isOp() || player.hasPermission("pwnfilter.reload")) {
	        	if (args[0].equalsIgnoreCase("reload")) {
	        		if (player != null) {	        		
	        			player.sendMessage(ChatColor.RED + "[PwnFilter] Reloading rules.txt");       			
		        		logger.info("[PwnFilter] rules.txt reloaded by " + player.getName());
	        		} else {
		        		logger.info("[PwnFilter] rules.txt reloaded from server console");
	        		}
	        		rules.clear();
	        		patterns.clear();
	        		loadRules();
	        	}
        	} else {
        		player.sendMessage(ChatColor.RED + "[PwnFilter] You do not have permission for this command.");
        		logger.info("[PwnFilter] Command access denied for " + player.getName());
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
    			logger.info( "[PwnFilter] Created directory '" + pname + "'" );
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
				output.write("# then <replace|rewrite|warn|log|deny|debug|kick|command|console> <string>" + newline);
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
    			logger.info( "[PwnFilter] Created config file '" + fname + "'" );
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
    		logger.warning("[PwnFilter] Error reading config file '" + fname + "': " + e.getLocalizedMessage());
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
    			logger.fine("[PwnFilter] Successfully compiled regex: " + re);
    		}
    		catch (PatternSyntaxException e) {
    			logger.warning("[PwnFilter] Failed to compile regex: " + re);
    			logger.warning("[PwnFilter] " + e.getMessage());
    		}
    		catch (Exception e) {
    			logger.severe("[PwnFilter] Unexpected error while compiling expression '" + re + "'");
    			e.printStackTrace();
    		}
    	}
    }  
    public Boolean matchPattern(String msg, String re_from) {
    	Pattern pattern_from = patterns.get(re_from);
    	if (pattern_from == null) {
    		// Pattern failed to compile, ignore
			logger.info("[PwnFilter] Ignoring invalid regex: " + re_from);
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
}