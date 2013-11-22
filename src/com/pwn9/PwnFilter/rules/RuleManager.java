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
import com.pwn9.PwnFilter.util.LogManager;

import java.io.*;
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

        // Try to migrate an old rules file, if it exists.
        migrateRules();

        // Set the shortcut manager to use the same directory.
        return ShortCutManager.getInstance().setShortcutDir(ruleDir);
    }

    public boolean migrateRules() {
        // Now, check to see if there's an old rules.txt in the PwnFilter directory, and if so, move it.
        File oldRuleFile = new File(plugin.getDataFolder(),"rules.txt");
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
                LogManager.logger.severe("Unable to move old rules.txt file to new dir: " + ruleDir.getAbsolutePath());
                LogManager.logger.severe("Please look in your plugin directory: " + plugin.getDataFolder().getAbsolutePath() + " and manually migrate your rules.");
                LogManager.logger.severe("Disabling PwnFilter");
                plugin.getPluginLoader().disablePlugin(plugin);
                return false;
            }
        }
        return true;

    }

    /**
     * Get a File object pointing to the named configuration in the configured
     * Rule Directory.
     *
     *
     * @param fileName Name of configuration File to load
     * @param createFile Create the file if it doesn't exist.
     * @return File object for requested config, or null if not found.
     */
    public File getFile(String fileName, boolean createFile) {
        try {
            if (ruleDir.exists()) {
                File ruleFile = new File(ruleDir,fileName);
                if (ruleFile.exists()) {
                    return ruleFile;
                } else {
                    if (createFile && copyRuleTemplate(ruleFile, fileName)) {
                        return ruleFile;
                    } else {
                        return null;
                    }
                }
            }
        } catch (IOException ex) {
            // Log the error below.
        }
        LogManager.logger.warning("Unable to find or create rule file:" + fileName);
        return null;
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

    public boolean copyRuleTemplate(File rulesFile, String configName) throws IOException {
        InputStream templateFile;

        templateFile = plugin.getResource(configName);
        if (templateFile == null) {
            // Use the default rules.txt
            templateFile = plugin.getResource("rules.txt");
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
            LogManager.logger.info("Created rules file from template: " + configName);
            return true;
        } else {
            LogManager.logger.warning("Failed to create rule file from template: " + configName);
            return false;
        }
    }


}
