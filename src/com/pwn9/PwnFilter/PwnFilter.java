package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.listener.*;
import com.pwn9.PwnFilter.rules.RuleSet;
import com.pwn9.PwnFilter.util.PwnFormatter;
import com.pwn9.PwnFilter.util.Tracker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
 * @author tremor77
 **/

// TODO: Add support for Books
// TODO: Enable configuration management /pfset /pfsave
// TODO: It's powerful.  Now, make it easier.
// TODO: Allow multiple config files so rules can be grouped.
// TODO: Make 'base' files that users can pull in to get started quickly (eg: swearing.txt, hate.txt, etc.)
// TODO: Multiverse-support? (Different configs for different worlds)

public class PwnFilter extends JavaPlugin {

    public static Boolean pwnMute = false;
    public List<String> cmdlist;
    public List<String> cmdblist;

    public enum EventType {
        CHAT,
        SIGN,
        COMMAND,
        ITEM,
    }
    public enum DebugModes {
        off, // Off
        low, // Some debugging
        medium, // More debugging
        high, // You're crazy. :)
    }

    public static boolean decolor;
    public static DebugModes debugMode;
    public ConcurrentHashMap<Player, String> killedPlayers = new ConcurrentHashMap<Player,String>();
    public static Logger logger;
    public Level ruleLogLevel;
    FileHandler logfileHandler;
    public static EventPriority cmdPriority, chatPriority, signPriority, invPriority;
    public static HashMap<Player, String> lastMessage = new HashMap<Player, String>();
    public static EnumSet<EventType> enabledEvents = EnumSet.allOf(EventType.class); // The list of active Events
    public static Economy economy = null;
    public static Tracker matchTracker;

    public static DataCache dataCache;
    public static RuleSet ruleset;


    public void onEnable() {
        // Set up logging
        if (logger == null) {
            logger = this.getLogger();
        }

        // Initialize default configuration
        saveDefaultConfig();

        // Now get our configuration
        configurePlugin();

        // Set up a Vault economy for actions like "fine" (optional)
        setupEconomy();

        // Create a new RuleSet object, loading in the rulesFile
        ruleset = new RuleSet(this);
        ruleset.init(getRulesFile());

        // Start the dataCache
        dataCache = new DataCache(this, ruleset.permList);

        // Now activate our listeners
        registerListeners();

        // Activate Plugin Metrics
        activateMetrics();

    }

    public void activateMetrics() {
        // Activate Plugin Metrics
        try {
            Metrics metrics = new Metrics(this);

            metrics.addCustomData(new Metrics.Plotter("Total Number of Server Rules") {
                @Override
                public int getValue() {
                    return ruleset.ruleCount();
                }
            });

            Metrics.Graph graph = metrics.createGraph("Rules by Event");

            for (final EventType r : EventType.values()) {
                graph.addPlotter(new Metrics.Plotter(r.toString()) {
                    @Override
                    public int getValue() {
                        return ruleset.ruleCount(r); // Number of rules for this event type
                    }
                });
            }

            Metrics.Graph matchGraph = metrics.createGraph("Matches");
            matchTracker = new Tracker("Matches");

            matchGraph.addPlotter(matchTracker);

            metrics.start();

        } catch (IOException e) {
            logger.fine(e.getMessage());
        }

    }
    public void registerListeners() {

        // Register Chat Handler (Always enabled)
        new PwnFilterPlayerListener(this);
        new PwnFilterEntityListener(this);

        // Register Configured Handlers
        if (enabledEvents.contains(EventType.COMMAND)) new PwnFilterCommandListener(this);
        if (enabledEvents.contains(EventType.SIGN)) new PwnFilterSignListener(this);
        if (enabledEvents.contains(EventType.ITEM)) new PwnFilterInvListener(this);

    }

    public void configurePlugin() {

        if (getConfig().getBoolean("logfile")) {
            setupLogfile();
        } else { // Needed during configuration reload to turn off logging if the option changes
            if (logfileHandler != null) {
                logfileHandler.close();
                logger.removeHandler(logfileHandler);
                logfileHandler = null;
            }
        }

        try {
            ruleLogLevel = Level.parse(getConfig().getString("loglevel","info").toUpperCase());
        } catch (IllegalArgumentException e ) {
            ruleLogLevel = Level.INFO;
        }

        decolor = getConfig().getBoolean("decolor");

        try {
            debugMode = DebugModes.valueOf(getConfig().getString("debug"));
        } catch (IllegalArgumentException e) {
            debugMode = DebugModes.off;
        }

        cmdlist = getConfig().getStringList("cmdlist");
        cmdblist = getConfig().getStringList("cmdblist");

        enabledEvents.clear(); // Reset the enabled event types.
        for ( EventType e : EventType.values()) {
            switch (e) {
                case CHAT:
                    chatPriority = EventPriority.valueOf(getConfig()
                            .getString("chatpriority","LOWEST").toUpperCase());
                    enabledEvents.add(EventType.CHAT);
                    break;

                case COMMAND:
                    if (getConfig().getBoolean("commandfilter",false)) {
                        cmdPriority = EventPriority.valueOf(getConfig()
                                .getString("cmdpriority","LOWEST").toUpperCase());
                        enabledEvents.add(EventType.COMMAND);
                    }
                    break;

                case SIGN:
                    if(getConfig().getBoolean("signfilter",false)) {
                        signPriority = EventPriority.valueOf(getConfig()
                                .getString("signpriority","LOWEST").toUpperCase());
                        enabledEvents.add(EventType.SIGN);
                    }
                    break;

                case ITEM:
                    if(getConfig().getBoolean("itemfilter",false)) {
                        invPriority = EventPriority.valueOf(getConfig()
                                .getString("invpriority","LOWEST").toUpperCase());
                        enabledEvents.add(EventType.ITEM);
                    }
                    break;
            }

        }

    }

    public void onDisable() {

        ruleset = null;
        if (logfileHandler != null) {
            logfileHandler.close();
            logger.removeHandler(logfileHandler);
            logfileHandler = null;
        }
        // Remove all our listeners, first.
        HandlerList.unregisterAll(this);

        // Shutdown the DataCache
        dataCache.stop();
        dataCache = null;

    }

    private void setupLogfile() {
        if (logfileHandler == null) {
            try {
                // For now, one logfile, like the old way.
                String fileName =  new File(getDataFolder(), "pwnfilter.log").toString();
                logfileHandler = new FileHandler(fileName, true);
                SimpleFormatter f = new PwnFormatter();
                logfileHandler.setFormatter(f);
                getConfig().addDefault("logfileLevel", "fine");
                logfileHandler.setLevel(Level.FINEST); // Catch all log messages
                logger.addHandler(logfileHandler);
                logger.info("Now logging to " + fileName );

            } catch (IOException e) {
                logger.warning("Unable to open logfile.");
            } catch (SecurityException e) {
                logger.warning("Security Exception while trying to add file Handler");
            }
        }

    }

    private void setupEconomy() {

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
                logger.info("Vault found. Enabling actions requiring Vault");
                return;
            }
        }
        logger.info("Vault dependency not found.  Disabling actions requiring Vault");

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args ) {

        if (cmd.getName().equalsIgnoreCase("pfreload")) {
            sender.sendMessage(ChatColor.RED + "Reloading config.yml and rules.txt");

            // Remove all our listeners, first.
            HandlerList.unregisterAll(this);

            // Shut down the DataCache
            dataCache.stop();

            reloadConfig();
            configurePlugin();

            ruleset = new RuleSet(this);
            if (ruleset.init(getRulesFile())) {
                logger.config("rules.txt and config.yml reloaded by " + sender.getName());
            } else {
                logger.warning("failed to reload rules.txt as requested by " + sender.getName());
            }

            // Start the DataCache again
            dataCache = new DataCache(this, ruleset.permList);

            // Re-register our listeners
            registerListeners();

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
        }  else if (cmd.getName().equalsIgnoreCase("pfdumpcache")) {
            dataCache.dumpCache(logger);
            sender.sendMessage(ChatColor.RED + "Dumped PwnFilter cache to log.");
            logger.info("Dumped PwnFilter cache to log by " + sender.getName());
        }
        return false;
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
                logger.warning("created sample rules file '" + fname + "'");
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return rulesFile;
    }

}

