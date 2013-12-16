/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.util.FileUtil;
import com.pwn9.PwnFilter.util.LogManager;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manage RuleSets, rulefiles, etc.  All ruleChains that are to be managed by
 * PwnFilter are to be created / reloaded, etc. through this manager interface
 * User: ptoal
 * Date: 13-09-25
 * Time: 2:18 PM
 */

@SuppressWarnings("UnusedDeclaration")
public class RuleManager {
    private static RuleManager _instance = null;
    private ConcurrentHashMap<String, RuleChain> ruleChains = new ConcurrentHashMap<String, RuleChain>();
    private File ruleDir;
    private PwnFilter plugin;


    private RuleManager(PwnFilter p) {
        plugin = p;
    }

    public static RuleManager getInstance() {
        if (_instance == null) {
            _instance = new RuleManager(PwnFilter.getInstance());
        }
        return _instance;
    }

    public File getRuleDir() {
        return ruleDir;
    }

    public boolean setRuleDir(String dirName) {

        // The folder on the filesystem to find rules/shortcut files.
        // If not specified in the config, default to: "PwnFilter/rules"

        if (dirName != null && !dirName.isEmpty()) {
            ruleDir = new File(dirName);
        } else {
            ruleDir = new File(plugin.getDataFolder(),"rules");
        }

        if (!ruleDir.exists()) {
            try {
                if (!ruleDir.mkdir()) {
                    LogManager.logger.severe("Unable to create rule directory: " + ruleDir.getAbsolutePath());
                    LogManager.logger.severe("Disabling PwnFilter");
                    plugin.getPluginLoader().disablePlugin(plugin);
                    return false;
                }
            } catch (SecurityException ex) {
                LogManager.logger.severe("Unable to create rule directory: " + ruleDir.getAbsolutePath());
                LogManager.logger.severe("Exception: " + ex.getMessage());
                LogManager.logger.severe("Disabling PwnFilter");
                plugin.getPluginLoader().disablePlugin(plugin);
                return false;
            }
        }

        // Set the shortcut manager to use the same directory.
        return ShortCutManager.getInstance().setShortcutDir(ruleDir);
    }

    public boolean migrateRules(File dataFolder) {
        // Now, check to see if there's an old rules.txt in the PwnFilter directory, and if so, move it.
        File oldRuleFile = new File(dataFolder,"rules.txt");
        if (oldRuleFile.exists()) {
            try {
                LogManager.logger.info("Migrating your old rules.txt into the new rules directory: " + ruleDir.getAbsolutePath());
                if (!oldRuleFile.renameTo(new File(ruleDir,"rules.txt"))) {
                    LogManager.logger.severe("Unable to move old rules.txt file to new dir: " + ruleDir.getAbsolutePath());
                    LogManager.logger.severe("Please look in your plugin directory: " + plugin.getDataFolder().getAbsolutePath() + " and manually migrate your rules.");
                    plugin.getPluginLoader().disablePlugin(plugin);
                    return false;
                }
            } catch (Exception ex) {
                LogManager.logger.severe("Unable to move old rules.txt file to new dir.");
                LogManager.logger.severe("Please look in your plugin directory: " + plugin.getDataFolder().getAbsolutePath() + " and manually migrate your rules.");
                LogManager.logger.severe("Disabling PwnFilter");
                plugin.getPluginLoader().disablePlugin(plugin);
                return false;
            }
        }
        return true;

    }

    /*
     * Get a rulechain, or create a new one from the named config
     */
    public RuleChain getRuleChain(String configName) {
        if (ruleChains.containsKey(configName)) {
            return ruleChains.get(configName);
        } else {
            RuleChain newRuleChain = new RuleChain(configName);
            ruleChains.put(configName,newRuleChain);
            return newRuleChain;
        }
    }

    /*
     * Force all ruleChains to be refreshed.
     */
    public void reloadAllConfigs() {

        // We do this as a separate loop, to invalidate all ruleChains before
        // starting to reload the configs.  This ensures we check for cyclic
        // references properly.

        // Invalidate all ruleChains
        for (RuleChain rc : ruleChains.values()) {
            rc.resetChain();
        }

        // Reload all the shortcuts
        ShortCutManager.getInstance().reloadFiles();

        // Now, reparse the configs
        for (String ruleSetName : new CopyOnWriteArrayList<String>(ruleChains.keySet())) {
            RuleChain chain = ruleChains.get(ruleSetName);
            if (chain.loadConfigFile()) {
                LogManager.getInstance().debugMedium("Re-loaded RuleChain from config: " + chain.getConfigName());
            } else {
                LogManager.getInstance().debugMedium("Unable to load RuleChain from config: " + chain.getConfigName());
            }
        }

        // Now remove the ones that aren't fully loaded.
        for (Map.Entry <String, RuleChain>e : ruleChains.entrySet()) {
            if (e.getValue().isValid()) {
                ruleChains.remove(e.getKey());
            }
        }
    }

    public File getFile(String fileName, boolean createFile) {
        return FileUtil.getFile(ruleDir, fileName, createFile);
    }
}
