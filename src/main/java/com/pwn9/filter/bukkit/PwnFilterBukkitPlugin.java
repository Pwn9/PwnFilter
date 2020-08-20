/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.bukkit;

import com.google.common.collect.MapMaker;
import com.pwn9.filter.bukkit.commands.PwnFilterCommands;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.bukkit.listener.*;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.minecraft.api.MinecraftConsole;
import com.pwn9.filter.minecraft.command.PwnClearScreen;
import com.pwn9.filter.minecraft.command.PwnFilterMute;
import com.pwn9.filter.minecraft.command.PwnFilterReload;
import com.pwn9.filter.util.tag.RegisterTags;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;


/**
 * A Regular Expression (REGEX) Chat Filter For Bukkit with many great features
 *
 * @author sage905
 * @version $Id: $Id
 */

public class PwnFilterBukkitPlugin extends JavaPlugin implements PwnFilterPlugin, TemplateProvider {

    public static final ConcurrentMap<UUID, String> lastMessage = new MapMaker().concurrencyLevel(2).weakKeys().makeMap();
    static Economy economy = null;
    private static PwnFilterBukkitPlugin _instance;
    private final BukkitAPI minecraftAPI;
    private final MinecraftConsole console;
    private final FilterService filterService;
    private final Map<String, Integer > eventRules = new HashMap<>();


    /**
     * <p>Constructor for PwnFilter.</p>
     */
    public PwnFilterBukkitPlugin() {

        if (_instance == null) {
            _instance = this;
        } else {
            throw new IllegalStateException("Only one instance of PwnFilter can be run per server");
        }
        minecraftAPI = new BukkitAPI(this);
        console = new MinecraftConsole(minecraftAPI);
        filterService = new FilterService(getLogger());
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        filterService.getConfig().setTemplateProvider(this);
        RegisterTags.all();
    }

    /**
     * <p>getInstance.</p>
     *
     * @return a {@link PwnFilterBukkitPlugin} object.
     */
    public static PwnFilterBukkitPlugin getInstance() {
        return _instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoad() {

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
        if (!configurePlugin()) return;

        filterService.registerAuthorService(minecraftAPI);
        filterService.registerNotifyTarget(minecraftAPI);

        //Load up our listeners
        //        BaseListener.setAPI(minecraftAPI);

        filterService.registerClient(new PwnFilterCommandListener(this));
        filterService.registerClient(new PwnFilterInvListener(this));
        filterService.registerClient(new PwnFilterPlayerListener(this));
        filterService.registerClient(new PwnFilterServerCommandListener(this));
        filterService.registerClient(new PwnFilterSignListener(this));
        filterService.registerClient(new PwnFilterBookListener(this));


        // The Entity Death handler, for custom death messages.
        getServer().getPluginManager().registerEvents(new PwnFilterEntityListener(), this);
        // The DataCache handler, for async-safe player info (name/world/permissions)
        getServer().getPluginManager().registerEvents(new PlayerCacheListener(), this);

        // Enable the listeners
        filterService.enableClients();

        // Set up Command Handlers
        PwnFilterCommands commands = new PwnFilterCommands(this);
        commands.registerCommands("pfreload",new PwnFilterReload(filterService,this));
        commands.registerCommands("pfcls",new PwnClearScreen(this));
        commands.registerCommands("pfmute",new PwnFilterMute(this));
        for (final FilterClient f : filterService.getActiveClients()) {
            final String eventName = f.getShortName();
            int value = f.getRuleChain().ruleCount();
            eventRules.put(eventName,value);
        }
        Metrics metrics = new Metrics(this, 8480);
        Metrics.AdvancedPie pie = new Metrics.AdvancedPie("rules_by_event", () -> eventRules);
        metrics.addCustomChart(pie);
    }

    /**
     * <p>onDisable.</p>
     */
    public void onDisable() {
        HandlerList.unregisterAll(this); // Unregister all Bukkit Event handlers.
        filterService.shutdown();
        filterService.deregisterAuthorService(minecraftAPI);
    }

    public boolean configurePlugin() {
        minecraftAPI.reset();
        try {
            // Stupid hack because YamlConfiguration.loadConfiguration() eats our exception
            YamlConfiguration config = new YamlConfiguration();
            config.load(new File(getDataFolder(), "config.yml"));

            reloadConfig();
            BukkitConfig.loadConfiguration(getConfig(), getDataFolder(), filterService);
            return true;
        } catch (InvalidConfigurationException | IOException ex) {
            filterService.getLogger().severe("Fatal configuration failure: " + ex.getMessage());
            filterService.getLogger().severe("PwnFilter disabled.");
            getPluginLoader().disablePlugin(this);
        }
        return false;
    }

    @Override
    public boolean checkRecentMessage(UUID uuid, String message) {
        return (PwnFilterBukkitPlugin.lastMessage.containsKey(uuid) && PwnFilterBukkitPlugin.lastMessage.get(uuid).equals(message));
    }

    @Override
    public void addRecentMessage(UUID uuid, String string) {
        lastMessage.put(uuid,string);
    }

    private void setupEconomy() {

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
                filterService.getLogger().info("Vault found. Enabling actions requiring Vault");
                return;
            }
        }
        filterService.getLogger().info("Vault dependency not found.  Disabling actions requiring Vault");
    }

    @Override
    public FilterService getFilterService() {
        return filterService;
    }

    @Override
    public MinecraftConsole getConsole() {
        return console;
    }

    @Override
    public BukkitAPI getApi() {
        return minecraftAPI;
    }

}


