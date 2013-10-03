package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.listener.*;
import com.pwn9.PwnFilter.rules.RuleChain;
import com.pwn9.PwnFilter.rules.RuleManager;
import com.pwn9.PwnFilter.util.DefaultMessages;
import com.pwn9.PwnFilter.util.LogManager;
import com.pwn9.PwnFilter.util.Tracker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

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

    // Metrics data
    private Metrics metrics;
    public static Tracker matchTracker;
    private Metrics.Graph eventGraph;

    public static ConcurrentHashMap<Player, String> killedPlayers = new ConcurrentHashMap<Player,String>();

    // Filter switches
    public static boolean decolor = false;
    public static Boolean pwnMute = false;

    public static HashMap<Player, String> lastMessage = new HashMap<Player, String>();
    public static Economy economy = null;

    @Override
    public void onLoad() {

        LogManager.getInstance(getLogger(),getDataFolder());

        // Initialize the manager for FilterListeners
        ListenerManager.getInstance(this);

        // Initialize the ruleManager
        RuleManager.getInstance(this);

    }

    public void onEnable() {

        // Initialize Configuration
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        // Now get our configuration
        configurePlugin();

        // Set up a Vault economy for actions like "fine" (optional)
        setupEconomy();

        // Initialize the DataCache
        DataCache.getInstance(this);

        // Activate Plugin Metrics
        activateMetrics();

        //Load up our listeners
        ListenerManager listenerManager = ListenerManager.getInstance();
        listenerManager.registerListener(new PwnFilterCommandListener(this), this);
        listenerManager.registerListener(new PwnFilterInvListener(this),this);
        listenerManager.registerListener(new PwnFilterPlayerListener(this),this);
        listenerManager.registerListener(new PwnFilterServerCommandListener(this),this);
        listenerManager.registerListener(new PwnFilterSignListener(this),this);


        // And the Entity Death handler, for custom death messages.
        new PwnFilterEntityListener(this);

        // Start the DataCache
        DataCache.getInstance().start();

        // Enable the listeners
        listenerManager.enableListeners();

    }

    public void onDisable() {

        ListenerManager.getInstance().unregisterListeners();

        HandlerList.unregisterAll(this); // Unregister all remaining handlers.

        // Shutdown the DataCache
        DataCache.getInstance().stop();

        LogManager.getInstance().stop();

    }

    public void activateMetrics() {
        // Activate Plugin Metrics
        try {
            if (metrics == null) {
                metrics = new Metrics(this);

                eventGraph = metrics.createGraph("Rules by Event");
                updateMetrics();

                Metrics.Graph matchGraph = metrics.createGraph("Matches");
                matchTracker = new Tracker("Matches");

                matchGraph.addPlotter(matchTracker);
            }
            metrics.start();


        } catch (IOException e) {
            LogManager.logger.fine(e.getMessage());
        }

    }

    public void updateMetrics() {

        ArrayList<String> activeListenerNames = new ArrayList<String>();
        for (FilterListener f : ListenerManager.getInstance().getActiveListeners()) {
            activeListenerNames.add(f.getShortName());
        }

        // Remove old plotters
        for (Metrics.Plotter p : eventGraph.getPlotters()) {
            if (!activeListenerNames.contains(p.getColumnName())) {
                eventGraph.removePlotter(p);
            }
        }

        // Add new plotters
        for (final FilterListener f : ListenerManager.getInstance().getActiveListeners()) {
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
            LogManager.getInstance().start();
        } else { // Needed during configuration reload to turn off logging if the option changes
            LogManager.getInstance().stop();
        }

        File ruleDir;
        String ruledirectory = getConfig().getString("ruledirectory");
        if (ruledirectory != null ) {
            ruleDir = new File(ruledirectory);
        } else {
            ruleDir = new File(getDataFolder(),"rules");
        }

        if (!ruleDir.exists()) {
            try {
                if (!ruleDir.mkdir()) {
                    LogManager.logger.severe("Unable to create rule directory: " + ruleDir.getAbsolutePath());
                    LogManager.logger.severe("Disabling PwnFilter");
                    getPluginLoader().disablePlugin(this);
                    return;
                }
            } catch (SecurityException ex) {
                LogManager.logger.severe("Unable to create rule directory: " + ruleDir.getAbsolutePath());
                LogManager.logger.severe("Exception: " + ex.getMessage());
                LogManager.logger.severe("Disabling PwnFilter");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }
        RuleManager.getInstance().setRuleDir(ruleDir);

        try {
            LogManager.ruleLogLevel = Level.parse(getConfig().getString("loglevel","info").toUpperCase());
        } catch (IllegalArgumentException e ) {
            LogManager.ruleLogLevel = Level.INFO;
        }

        decolor = getConfig().getBoolean("decolor");

        try {
            LogManager.debugMode = LogManager.DebugModes.valueOf(getConfig().getString("debug"));
        } catch (IllegalArgumentException e) {
            LogManager.debugMode = LogManager.DebugModes.off;
        }

        DefaultMessages.setConfig(getConfig());



    }

    private void setupEconomy() {

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
                LogManager.logger.info("Vault found. Enabling actions requiring Vault");
                return;
            }
        }
        LogManager.logger.info("Vault dependency not found.  Disabling actions requiring Vault");

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args ) {

        if (cmd.getName().equalsIgnoreCase("pfreload")) {
            sender.sendMessage(ChatColor.RED + "Reloading config.yml and rules.txt");

            LogManager.logger.info("Disabling all listeners");
            ListenerManager.getInstance().disableListeners();

            // Shut down the DataCache
            DataCache.getInstance().stop();

            reloadConfig();
            configurePlugin();

            LogManager.logger.config("Reloaded config.yml as requested by " + sender.getName());

            RuleManager.getInstance().reloadAllConfigs();
            LogManager.logger.config("All rules reloaded by " + sender.getName());

            // Start the DataCache again
            DataCache.getInstance().start();

            // Re-register our listeners
            ListenerManager.getInstance().enableListeners();
            LogManager.logger.info("All listeners re-enabled");

            return true;
        }

        else if (cmd.getName().equalsIgnoreCase("pfcls")) {
            sender.sendMessage(ChatColor.RED + "Clearing chat screen");
            LogManager.logger.info("chat screen cleared by " + sender.getName());
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
                LogManager.logger.info("global mute cancelled by " + sender.getName());
                pwnMute = false;
            }
            else {
                getServer().broadcastMessage(ChatColor.RED + "Global mute initiated by " + sender.getName());
                LogManager.logger.info("global mute initiated by " + sender.getName());
                pwnMute = true;
            }
            return true;
        }  else if (cmd.getName().equalsIgnoreCase("pfdumpcache")) {
            DataCache.getInstance().dumpCache(LogManager.logger);
            sender.sendMessage(ChatColor.RED + "Dumped PwnFilter cache to log.");
            LogManager.logger.info("Dumped PwnFilter cache to log by " + sender.getName());
        }
        return false;
    }

    //TODO: Handle this better
    public static void addKilledPlayer(Player p, String message) {
        killedPlayers.put(p, message);
    }


    public boolean copyRuleTemplate(File rulesFile, String configName) {
        try{
            InputStream templateFile;

            templateFile = getResource(configName);
            if (templateFile == null) {
                // Use the default rules.txt
                templateFile = getResource("rules.txt");
            }
            if (rulesFile.createNewFile()) {
                BufferedInputStream fin = new BufferedInputStream(templateFile);
                FileOutputStream fout = new FileOutputStream(rulesFile);
                byte[] data = new byte[1024];
                int c;
                while ((c = fin.read(data, 0, 1024)) != -1)
                    fout.write(data, 0, c);
                fin.close();
                fout.close();
                getLogger().info("Created rules file from template: " + configName);
                return true;
            } else {
                getLogger().warning("Failed to create rule file from template: " + configName);
                return false;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

}

