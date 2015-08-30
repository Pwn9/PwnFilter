/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.RuleManager;
import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Listen for Sign Change events and apply the filter to the text.
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class PwnFilterSignListener extends BaseListener {

    /**
     * <p>Constructor for PwnFilterSignListener.</p>
     *
     * @param p a {@link com.pwn9.PwnFilter.PwnFilter} object.
     */
    public PwnFilterSignListener(PwnFilter p) {
        super(p);
    }

    /** {@inheritDoc} */
    @Override
    public String getShortName() {
        return "SIGN";
    }

    /**
     * The sign filter has extra work to do that the chat doesn't:
     * 1. Take lines of sign and aggregate them into one string for processing
     * 2. Feed them into the filter.
     * 3. Re-split the lines so they can be placed on the sign.
     *
     * @param event The SignChangeEvent to be processed.
     */
    public void onSignChange(SignChangeEvent event) {
        if (event.isCancelled()) return;

        // Permissions Check, if player has bypass permissions, then skip everything.
        if (event.getPlayer().hasPermission("pwnfilter.bypass.signs")) {
            return;
        }
        // Take the message from the CommandPreprocessEvent and send it through the filter.
        StringBuilder builder = new StringBuilder();

        for (String l :event.getLines()) {
            builder.append(l).append("\t");
        }
        String signLines = builder.toString().trim();

        FilterState state = new FilterState(plugin, signLines, event.getPlayer(),this);

        ruleChain.execute(state);

        if (state.messageChanged()){
            // TODO: Can colors be placed on signs?  Wasn't working. Find out why.
            // Break the changed string into new Lines
            List<String> newLines = new ArrayList<String>();

            // Global decolor
            if ((PwnFilter.decolor) && !(DataCache.getInstance().hasPermission(event.getPlayer(), "pwnfilter.color"))) {
                Collections.addAll(newLines,state.getModifiedMessage().getPlainString().split("\t"));
            } else {
                Collections.addAll(newLines,state.getModifiedMessage().getColoredString().split("\t"));
            }

            String[] outputLines = new String[4];

            // Check if any of the lines are now too long to fit the sign.  Truncate if they are.

            for (int i = 0 ; i < 4 && i < newLines.size() ; i++) {
                if (newLines.get(i).length() > 15) {
                    outputLines[i] = newLines.get(i).substring(0, 15);
                } else {
                    outputLines[i] = newLines.get(i);
                }
            }

            for (int i = 0 ; i < 4 ; i++ ) {
                if (outputLines[i] != null) {
                    event.setLine(i,outputLines[i]);
                } else {
                    event.setLine(i,"");
                }
            }
        }

        if (state.cancel) {
            event.setCancelled(true);
            state.getPlayer().sendMessage("Your sign broke, there must be something wrong with it.");
            state.addLogMessage("SIGN " + state.playerName + " sign text: "
                    + state.getOriginalMessage().getColoredString());
        }

    }

    /**
     * {@inheritDoc}
     *
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p/>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    @Override
    public void activate(Configuration config) {
        if (isActive()) return;
        setRuleChain(RuleManager.getInstance().getRuleChain("sign.txt"));

        PluginManager pm = Bukkit.getPluginManager();
        EventPriority priority = EventPriority.valueOf(config.getString("signpriority", "LOWEST").toUpperCase());

        if (config.getBoolean("signfilter")) {
            // Now register the listener with the appropriate priority
            pm.registerEvent(SignChangeEvent.class, this, priority,
                    new EventExecutor() {
                        public void execute(Listener l, Event e) { onSignChange((SignChangeEvent)e); }
                    },
                    plugin);

            LogManager.logger.info("Activated SignListener with Priority Setting: " + priority.toString()
                    + " Rule Count: " + getRuleChain().ruleCount());

            setActive();
        }
    }

}
