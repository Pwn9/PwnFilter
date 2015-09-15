/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit;

import com.google.common.collect.MapMaker;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.bukkit.listener.*;
import com.pwn9.filter.engine.FilterEngine;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import com.pwn9.filter.minecraft.api.MinecraftServer;
import com.pwn9.filter.minecraft.command.pfcls;
import com.pwn9.filter.minecraft.command.pfdumpcache;
import com.pwn9.filter.minecraft.command.pfmute;
import com.pwn9.filter.minecraft.command.pfreload;
import com.pwn9.filter.util.LogManager;
import com.pwn9.filter.util.tags.RegisterTags;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
 *
 * @author sage905
 * @version $Id: $Id
 */

public class PwnFilterPlugin extends JavaPlugin {

    private static PwnFilterPlugin _instance;
    private static MinecraftAPI minecraftAPI;
    private MCStatsTracker statsTracker;
    public static Economy economy = null;
    private FilterEngine filterEngine;

    public static final ConcurrentMap<UUID, String> lastMessage = new MapMaker().concurrencyLevel(2).weakKeys().makeMap();

    /**
     * <p>Constructor for PwnFilter.</p>
     */
    public PwnFilterPlugin() {
        if (_instance == null) {
            _instance = this;
        } else {
            throw new IllegalStateException("Only one instance of PwnFilter can be run per server");
        }
        minecraftAPI = new BukkitAPI(this);
        statsTracker = new MCStatsTracker(this);
        filterEngine = new FilterEngine(statsTracker);
        MinecraftServer.setAPI(minecraftAPI);
        RegisterTags.all();

    }


    /**
     * <p>getInstance.</p>
     *
     * @return a {@link PwnFilterPlugin} object.
     */
    public static PwnFilterPlugin getInstance() {
        return _instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoad() {

        // Set up the Log manager.
        LogManager.getInstance(getLogger(), getDataFolder());

        // We're all Bukkit here, so let's set the API appropriately

    }

    /**
     * <p>onEnable.</p>
     */
    public void onEnable() {

        // Initialize Configuration
        saveDefaultConfig();

        // Set up a Vault economy for actions like "fine" (optional)
        setupEconomy();

        // Now get our configuration
        configurePlugin();

        // Activate Statistics Tracking
        statsTracker.startTracking();

        //Load up our listeners
        BaseListener.setAPI(minecraftAPI);

        filterEngine.registerClient(new PwnFilterCommandListener());
        filterEngine.registerClient(new PwnFilterInvListener());
        filterEngine.registerClient(new PwnFilterPlayerListener());
        filterEngine.registerClient(new PwnFilterServerCommandListener());
        filterEngine.registerClient(new PwnFilterSignListener());
        filterEngine.registerClient(new PwnFilterBookListener());


        // The Entity Death handler, for custom death messages.
        getServer().getPluginManager().registerEvents(new PwnFilterEntityListener(), this);
        // The DataCache handler, for async-safe player info (name/world/permissions)
        getServer().getPluginManager().registerEvents(new PlayerCacheListener(), this);

        // Enable the listeners
        filterEngine.enableClients();

        // Set up Command Handlers
        getCommand("pfreload").setExecutor(new pfreload(filterEngine));
        getCommand("pfcls").setExecutor(new pfcls());
        getCommand("pfmute").setExecutor(new pfmute());
        getCommand("pfdumpcache").setExecutor(new pfdumpcache());

    }

    /**
     * <p>onDisable.</p>
     */
    public void onDisable() {

        filterEngine.unregisterAllClients();
        HandlerList.unregisterAll(this); // Unregister all remaining handlers.
        LogManager.getInstance().stop();

    }


    /**
     * <p>configurePlugin.</p>
     */
    public void configurePlugin() {

        minecraftAPI.reset();
        // Whenever we reset the API, we need to make sure the plugin permissions
        // get re-loaded into the cache.
        minecraftAPI.addCachedPermissions(getDescription().getPermissions());

        try {
            BukkitConfig.loadConfiguration(getConfig(), getDataFolder());
        } catch (RuntimeException ex) {
            LogManager.logger.severe("Fatal configuration failure: " + ex.getMessage());
            LogManager.logger.severe("PwnFilter disabled.");
            getPluginLoader().disablePlugin(this);
        }

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

}

