package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.listener.FilterListener;
import com.pwn9.PwnFilter.listener.ListenerManager;
import com.pwn9.PwnFilter.rules.RuleChain;
import com.pwn9.PwnFilter.util.PwnFormatter;
import com.pwn9.PwnFilter.util.Tracker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.HashMap;
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
// TODO: Make 'base' files that users can pull in to get started quickly (eg: swearing.txt, hate.txt, etc.)
// TODO: Multiverse-support? (Different configs for different worlds)

public class PwnFilter extends JavaPlugin {

    private ListenerManager listenerManager;

    private Metrics metrics;
    private static Tracker matchTracker;

    public static ConcurrentHashMap<Player, String> killedPlayers = new ConcurrentHashMap<Player,String>();

    public enum DebugModes {
        off, // Off
        low, // Some debugging
        medium, // More debugging
        high, // You're crazy. :)
    }
    // TODO: Update logging.  Debug logger should be a static method of PwnFilter class
    // other classes should be able to call: PwnFilter.debugLogger.low/medium/high

    // Logging variables
    public static DebugModes debugMode = DebugModes.off;
    public static Logger logger;
    public static Level ruleLogLevel;
    FileHandler logfileHandler;

    // Filter switches
    public static boolean decolor;
    public static Boolean pwnMute = false;

    public static EventPriority cmdPriority, chatPriority, signPriority, invPriority, consolePriority;
    public static HashMap<Player, String> lastMessage = new HashMap<Player, String>();
    public static Economy economy = null;

    private static File ruleDir;
    public static RuleChain ruleset;
    private Metrics.Graph eventGraph;

    @Override
    public void onLoad() {
        // Set up logging
        if (logger == null) {
            logger = this.getLogger();
        }

        // Initialize the DataCache
        DataCache.getInstance(this);

        // Initialize the manager for FilterListeners
        listenerManager = ListenerManager.getInstance(this);

        // Set up a Vault economy for actions like "fine" (optional)
        setupEconomy();

    }

    public void onEnable() {

        // Initialize Configuration
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        // Now get our configuration
        configurePlugin();

        // Activate Plugin Metrics
        activateMetrics();

    }

    public void activateMetrics() {
        // Activate Plugin Metrics
        try {
            metrics = new Metrics(this);

            eventGraph = metrics.createGraph("Rules by Event");
            updateMetrics();

            Metrics.Graph matchGraph = metrics.createGraph("Matches");
            matchTracker = new Tracker("Matches");

            matchGraph.addPlotter(matchTracker);

            metrics.start();

        } catch (IOException e) {
            logger.fine(e.getMessage());
        }

    }

    public void updateMetrics() {

        ArrayList<String> activeListenerNames = new ArrayList<String>();
        for (FilterListener f : listenerManager.getActiveListeners()) {
            activeListenerNames.add(f.getShortName());
        }

        // Remove old plotters
        for (Metrics.Plotter p : eventGraph.getPlotters()) {
            if (!activeListenerNames.contains(p.getColumnName())) {
                eventGraph.removePlotter(p);
            }
        }

        // Add new plotters
        for (final FilterListener f : listenerManager.getActiveListeners()) {
            final String eventName = f.getShortName();
            eventGraph.addPlotter(new Metrics.Plotter(eventName) {
                @Override
                public int getValue() {
                    RuleChain r = f.getRuleChain();
                    if (r != null) {
                        return r.ruleCount(); // Number of rules for this event type
                    } else
                        return 0;
                }
            });
        }

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

        ruleDir = new File(getDataFolder(),"rules");
        if (!ruleDir.exists()) {
            ruleDir.mkdir();
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

    }

    public void onDisable() {

        ruleset = null;
        if (logfileHandler != null) {
            logfileHandler.close();
            logger.removeHandler(logfileHandler);
            logfileHandler = null;
        }

        // Shutdown all our listeners, first.
        listenerManager.disableListeners();

        // Shutdown the DataCache
        DataCache.getInstance().stop();

    }

    private void setupLogfile() {
        if (logfileHandler == null) {
            try {
                // For now, one logfile, like the old way.
                String fileName =  new File(getDataFolder(), "pwnfilter.log").toString();
                logfileHandler = new FileHandler(fileName, true);
                SimpleFormatter f = new PwnFormatter();
                logfileHandler.setFormatter(f);
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
            DataCache.getInstance().stop();

            reloadConfig();
            configurePlugin();
            logger.config("Reloaded config.yml as requested by " + sender.getName());

            for (RuleChain ruleSet : ruleSets.values()) {
                if (ruleset.loadConfigFile()) {
                    logger.config(ruleset.getConfigName() + " reloaded by " + sender.getName());
                } else {
                    logger.warning("failed to reload" + ruleset.getConfigName() + " as requested by " + sender.getName());
                }
            }


            // Start the DataCache again
            DataCache.getInstance().start();

            // Re-register our listeners
            listenerManager.startup();

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
            DataCache.getInstance().dumpCache(logger);
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

    public File getRuleDir() {
        return ruleDir;
    }

    public RuleChain getRuleset(String name) {
        return ruleSets.get(name);
    }


    public static Level getRuleLogLevel() {
        return ruleLogLevel;
    }

    public File getRulesFile(String fname) {

        File rulesFile;

        rulesFile = new File(ruleDir,fname);
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

    public void loadRuleSets() throws FileSystemException {
        File ruleDir = getRuleDir();
        if (!ruleDir.exists()) {
            throw new FileSystemException("Could not find Rule Dir.") ;
        }
         // TODO: Adapt this to use the RuleManager properly.  We probably only need to keep track
        // of the "base" rulesets (eg, used by our event handlers).  Other plugins will keep track of
        // their own.  The RuleManager will track all.

        for (File f : ruleDir.listFiles()) {
            RuleChain newRuleSet = ruleManager.getRuleChain(f.getName());
            ruleSets.put(f.getName(),newRuleSet);
            DataCache.getInstance().addPermissions(newRuleSet.getPermissionList());

            logger.info("Loaded config file: " + f.getName());
        }

    }

    public static void logLow(String message) {
        if (debugMode.compareTo(PwnFilter.DebugModes.low) >= 0) {
            logger.finer(message);
        }
    }

    public static void logMedium(String message) {
        if (debugMode.compareTo(PwnFilter.DebugModes.medium) >= 0) {
            logger.finer(message);
        }
    }

    public static void logHigh(String message) {
        if (debugMode.compareTo(PwnFilter.DebugModes.high) >= 0) {
            logger.finer(message);
        }
    }

    //TODO: Handle this better
    public static void addKilledPlayer(Player p, String message) {
        killedPlayers.put(p, message);
    }

}

