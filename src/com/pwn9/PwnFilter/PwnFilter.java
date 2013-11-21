/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.api.ClientManager;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.listener.*;
import com.pwn9.PwnFilter.rules.RuleChain;
import com.pwn9.PwnFilter.rules.RuleManager;
import com.pwn9.PwnFilter.rules.ShortCutManager;
import com.pwn9.PwnFilter.util.DefaultMessages;
import com.pwn9.PwnFilter.util.LogManager;
import com.pwn9.PwnFilter.util.PointManager;
import com.pwn9.PwnFilter.util.Tracker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

    private static PwnFilter _instance;

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

    public PwnFilter() {
        _instance = this;
    }

    @Override
    public void onLoad() {

        LogManager.getInstance(getLogger(),getDataFolder());

        // Initialize the manager for FilterListeners
        ClientManager.getInstance(this);

        // Initialize the ruleManager
        RuleManager.getInstance(this);

    }

    public void onEnable() {

        // Initialize Configuration
        saveDefaultConfig();

        // Now get our configuration
        configurePlugin();

        // Set up a Vault economy for actions like "fine" (optional)
        setupEconomy();

        // Initialize the DataCache
        DataCache.getInstance();

        DataCache.getInstance().addPermissions(getDescription().getPermissions());

        // Initialize Points Manager if its enabled
        PointManager.setup();

        // Activate Plugin Metrics
        activateMetrics();

        //Load up our listeners
        ClientManager clientManager = ClientManager.getInstance();
        clientManager.registerClient(new PwnFilterCommandListener(this), this);
        clientManager.registerClient(new PwnFilterInvListener(this), this);
        clientManager.registerClient(new PwnFilterPlayerListener(this), this);
        clientManager.registerClient(new PwnFilterServerCommandListener(this), this);
        clientManager.registerClient(new PwnFilterSignListener(this), this);


        // The Entity Death handler, for custom death messages.
        getServer().getPluginManager().registerEvents(new PwnFilterEntityListener(),this);
        // The DataCache handler, for async-safe player info (name/world/permissions)
        getServer().getPluginManager().registerEvents(new PlayerCacheListener(), this);

        // Start the DataCache
        DataCache.getInstance().start();

        // Enable the listeners
        clientManager.enableClients();

    }

    public void onDisable() {

        ClientManager.getInstance().unregisterClients();

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
        for (FilterClient f : ClientManager.getInstance().getActiveClients()) {
            activeListenerNames.add(f.getShortName());
        }

        // Remove old plotters
        for (Metrics.Plotter p : eventGraph.getPlotters()) {
            if (!activeListenerNames.contains(p.getColumnName())) {
                eventGraph.removePlotter(p);
            }
        }

        // Add new plotters
        for (final FilterClient f : ClientManager.getInstance().getActiveClients()) {
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

    public static PwnFilter getInstance() {
        return _instance;
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

        ShortCutManager.getInstance().setShortcutDir(ruleDir);

        // Now, check to see if there's an old rules.txt in the PwnFilter directory, and if so, move it.
        File oldRuleFile = new File(getDataFolder(),"rules.txt");
        if (oldRuleFile.exists()) {
            try {
                LogManager.logger.info("Migrating your old rules.txt into the new rules directory: " + ruleDir.getAbsolutePath());
                if (!oldRuleFile.renameTo(new File(ruleDir,"rules.txt"))) {
                    LogManager.logger.severe("Unable to move old rules.txt file to new dir: " + ruleDir.getAbsolutePath());
                    LogManager.logger.severe("Please look in your plugin directory: " + getDataFolder().getAbsolutePath() + " and manually migrate your rules.");
                    getPluginLoader().disablePlugin(this);
                    return;
                }
            } catch (Exception ex) {
                LogManager.logger.severe("Unable to move old rules.txt file to new dir: " + ruleDir.getAbsolutePath());
                LogManager.logger.severe("Please look in your plugin directory: " + getDataFolder().getAbsolutePath() + " and manually migrate your rules.");
                LogManager.logger.severe("Disabling PwnFilter");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }

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
            sender.sendMessage(ChatColor.RED + "Reloading config.yml and rules/*.txt files.");

            LogManager.logger.info("Disabling all listeners");
            ClientManager.getInstance().disableClients();

            // Shut down the DataCache
            DataCache.getInstance().stop();

            reloadConfig();
            configurePlugin();


            LogManager.logger.config("Reloaded config.yml as requested by " + sender.getName());

            PointManager.setup();
            RuleManager.getInstance().reloadAllConfigs();
            LogManager.logger.config("All rules reloaded by " + sender.getName());

            // Start the DataCache again
            DataCache.getInstance().start();

            // Re-register our listeners
            ClientManager.getInstance().enableClients();
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


    public boolean copyRuleTemplate(File rulesFile, String configName) throws IOException {
        InputStream templateFile;

        templateFile = getResource(configName);
        if (templateFile == null) {
            // Use the default rules.txt
            templateFile = getResource("rules.txt");
            if (templateFile == null) return false;
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
    }

}

