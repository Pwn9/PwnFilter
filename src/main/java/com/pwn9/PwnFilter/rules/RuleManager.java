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

import com.pwn9.PwnFilter.util.LogManager;

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

// TODO: Abstract out the dependencies on PwnFilterPlugin

@SuppressWarnings("UnusedDeclaration")
public class RuleManager {
    private static RuleManager _instance = null;
    private final Map<String, RuleChain> ruleChains = Collections.synchronizedMap(new HashMap<String, RuleChain>());

    private RuleManager() {}

    /**
     * <p>getInstance.</p>
     *
     * @return a {@link com.pwn9.PwnFilter.rules.RuleManager} object.
     */
    public static RuleManager getInstance() {
        if (_instance == null) {
            _instance = new RuleManager();
            return _instance;
        } else {
            return _instance;
        }
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


}
