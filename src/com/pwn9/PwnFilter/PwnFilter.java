package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.listener.*;
import com.pwn9.PwnFilter.rules.RuleSet;
import com.pwn9.PwnFilter.util.PwnFormatter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
* A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
* @author tremor77
**/

// TODO: Add support for Anvils and Books
// TODO: Enable configuration management /pfset /pfsave and /pfreload config

public class PwnFilter extends JavaPlugin {

    public static Boolean pwnMute = false;
    public List<String> cmdlist;
    public List<String> cmdblist;
    public static boolean decolor, debugMode;
    public HashMap<Player, String> killedPlayers = new HashMap<Player,String>();
    public static Logger logger;
    public Level ruleLogLevel;
    FileHandler fh;
    public static EventPriority cmdPriority, chatPriority, signPriority, invPriority;
    public static HashMap<String, String> lastMessage = new HashMap<String, String>();
    public static Economy economy = null;


    public static RuleSet ruleset;

	public void onEnable() {

        // Initialize and load configuration
        saveDefaultConfig();

        // Set up logging
        logger = this.getLogger();

        if (getConfig().getBoolean("logfile")) {
            setupLogfile();
        }

        // See if we have Vault
        if (!setupEconomy() ) {
            logger.info("Vault dependency not found.  Disabling actions requiring Vault");
        } else {
            logger.info("Vault found. Enabling actions requiring Vault");
        }


        // Create a new RuleSet object, loading in the rulesFile
        ruleset = new RuleSet(this);
        ruleset.init(getRulesFile());

        getConfig().addDefault("logLevel","info");
        try {
            ruleLogLevel = Level.parse(getConfig().getString("loglevel").toUpperCase());
        } catch (IllegalArgumentException e ) {
            ruleLogLevel = Level.INFO;
        }

        decolor = getConfig().getBoolean("decolor");

        debugMode = getConfig().getBoolean("debug");

        cmdlist = getConfig().getStringList("cmdlist");
        cmdblist = getConfig().getStringList("cmdblist");

        getConfig().addDefault("cmdpriority","LOWEST");
        getConfig().addDefault("chatpriority","LOWEST");
        getConfig().addDefault("signpriority","LOWEST");
        getConfig().addDefault("invpriority","LOWEST");

        cmdPriority = EventPriority.valueOf(getConfig().getString("cmdpriority").toUpperCase());
        chatPriority = EventPriority.valueOf(getConfig().getString("chatpriority").toUpperCase());
        signPriority = EventPriority.valueOf(getConfig().getString("signpriority").toUpperCase());
        invPriority = EventPriority.valueOf(getConfig().getString("invpriority").toUpperCase());

        // Register Chat Handler
        new PwnFilterPlayerListener(this);
        new PwnFilterEntityListener(this);

        // Register Command Handler, if configured
        if (getConfig().getBoolean("commandfilter")) new PwnFilterCommandListener(this);

        // Register Sign Handler, if configured
        if (getConfig().getBoolean("signfilter")) new PwnFilterSignListener(this);

        // Put flag in to enable/disable this.
        new PwnFilterInvListener(this);

    }

    public void onDisable() {
    	ruleset = null;
        if (fh != null) {
            fh.close();
            logger.removeHandler(fh);
            fh = null;
        }
    }

    private void setupLogfile() {
        if (fh == null) {
            try {
                // For now, one logfile, like the old way.
                String fileName =  new File(getDataFolder(), "pwnfilter.log").toString();
                fh = new FileHandler(fileName, true);
                SimpleFormatter f = new PwnFormatter();
                fh.setFormatter(f);
                getConfig().addDefault("logfileLevel", "fine");
                fh.setLevel(Level.FINEST); // Catch all log messages
                logger.addHandler(fh);
                logger.info("Now logging to " + fileName );

            } catch (IOException e) {
                logger.warning("Unable to open logfile.");
            } catch (SecurityException e) {
                logger.warning("Security Exception while trying to add file Handler");
            }
        }

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @Override   
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args ) {

        if (cmd.getName().equalsIgnoreCase("pfreload")) { 		   		
            sender.sendMessage(ChatColor.RED + "Reloading rules.txt");
            ruleset = new RuleSet(this);
            if (ruleset.init(getRulesFile())) {
                logger.config("rules.txt reloaded by " + sender.getName());
            } else {
                logger.warning("failed to reload rules.txt as requested by " + sender.getName());
            }
            return true;
        }
		else if (cmd.getName().equalsIgnoreCase("pfcls")) {  
            sender.sendMessage(ChatColor.RED + "Clearing chat screen");
            logger.info("chat screen cleared by " + sender.getName());
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
	            logger.info("global mute cancelled by " + sender.getName());
	            pwnMute = false;
			}
			else {
				getServer().broadcastMessage(ChatColor.RED + "Global mute initiated by " + sender.getName());
	            logger.info("global mute initiated by " + sender.getName());
	            pwnMute = true;
			}
    		return true;
    	} 
        return false;
    } 


/*
<--
12:32:53          @Amaranth | Sage905: For anvils just use InventoryClickEvent. If the slot is for an anvil and is of
 type result the player has crafted the item and you should be able to modify the itemmeta
 */

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
        File rulesFile;
        String fname = "rules.txt";

        // Ensure that directory exists
        if(!dataFolder.exists()) {
            if (dataFolder.mkdirs()) {
                logger.info("created directory '" + dataFolder.getName() + "'");
            } else {
                return null;
            }
        }

        rulesFile = new File(dataFolder,fname);
        // Check to see if rules file exists.  If not, create a basic file from template
        if (!rulesFile.exists()) {
            try{
                //noinspection ResultOfMethodCallIgnored
                rulesFile.createNewFile();
                BufferedInputStream fin = new BufferedInputStream(this.getResource(fname));
                FileOutputStream fout = new FileOutputStream(rulesFile);
                byte[] data = new byte[1024];
                int c;
                while ((c = fin.read(data, 0, 1024)) != -1)
                    fout.write(data, 0, c);
                fin.close();
                fout.close();
                logger.warning("created config file '" + fname + "'");
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return rulesFile;
    }
}

