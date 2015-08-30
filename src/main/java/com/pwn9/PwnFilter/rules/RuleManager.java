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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manage RuleSets, rulefiles, etc.  All ruleChains that are to be managed by
 * PwnFilter are to be created / reloaded, etc. through this manager interface
 * User: ptoal
 * Date: 13-09-25
 * Time: 2:18 PM
 *
 * @author ptoal
 * @version $Id: $Id
 */

@SuppressWarnings("UnusedDeclaration")
public class RuleManager {
    private static RuleManager _instance = null;
    private final Map<String, RuleChain> ruleChains = Collections.synchronizedMap(new HashMap<String, RuleChain>());
    private File ruleDir;
    private PwnFilter plugin;


    private RuleManager(PwnFilter p) {
        plugin = p;
    }

    /**
     * <p>getInstance.</p>
     *
     * @return a {@link com.pwn9.PwnFilter.rules.RuleManager} object.
     */
    public static RuleManager getInstance() {
        if (_instance == null) {
            throw new IllegalStateException("Rule Manager not initialized.");
        }
        return _instance;
    }

    /**
     * <p>init.</p>
     *
     * @param p a {@link com.pwn9.PwnFilter.PwnFilter} object.
     * @return a {@link com.pwn9.PwnFilter.rules.RuleManager} object.
     */
    public static RuleManager init(PwnFilter p) {
        if (_instance == null) {
            _instance = new RuleManager(p);
            return _instance;
        } else {
            return _instance;
        }
    }

    /**
     * <p>Getter for the field <code>ruleDir</code>.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public File getRuleDir() {
        return ruleDir;
    }

    /**
     * <p>Setter for the field <code>ruleDir</code>.</p>
     *
     * @param dirName a {@link java.lang.String} object.
     * @return a boolean.
     */
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

    /**
     * <p>migrateRules.</p>
     *
     * @param dataFolder a {@link java.io.File} object.
     * @return a boolean.
     */
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
    /**
     * <p>getRuleChain.</p>
     *
     * @param configName a {@link java.lang.String} object.
     * @return a {@link com.pwn9.PwnFilter.rules.RuleChain} object.
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
    /**
     * <p>reloadAllConfigs.</p>
     */
    public void reloadAllConfigs() {

        // We do this as a separate loop, to invalidate all ruleChains before
        // starting to reload the configs.  This ensures we check for cyclic
        // references properly.

        // Invalidate all ruleChains
        synchronized (ruleChains) {
            for (RuleChain rc : ruleChains.values()) {
                rc.resetChain();
            }

            // Reload all the shortcuts
            ShortCutManager.getInstance().reloadFiles();

            // Now, reparse the configs
            for (Map.Entry <String, RuleChain> entry : ruleChains.entrySet()) {
                if (entry.getValue().loadConfigFile()) {
                    LogManager.getInstance().debugMedium("Re-loaded RuleChain from config: " + entry.getValue().getConfigName());
                } else {
                    LogManager.getInstance().debugMedium("Unable to load RuleChain from config: " + entry.getValue().getConfigName());
                }
            }

            // Now remove the ones that aren't fully loaded.
            for (Iterator<Map.Entry <String, RuleChain>>it = ruleChains.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry <String, RuleChain> e = it.next();
                if (!e.getValue().isValid()) {
                    it.remove();
                }
            }
        }
    }

    /**
     * <p>getFile.</p>
     *
     * @param fileName a {@link java.lang.String} object.
     * @param createFile a boolean.
     * @return a {@link java.io.File} object.
     */
    public File getFile(String fileName, boolean createFile) {
        return FileUtil.getFile(ruleDir, fileName, createFile);
    }
}
