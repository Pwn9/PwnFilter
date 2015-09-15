/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit;

import com.pwn9.filter.engine.api.StatsTracker;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.rules.RuleChain;
import com.pwn9.filter.util.LogManager;
import org.bukkit.plugin.Plugin;
import org.mcstats.Metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An MCStats implementation of statistics tracking.
 * <p/>
 * Created by ptoal on 15-09-13.
 */
public class MCStatsTracker implements StatsTracker {
    private Metrics metrics;
    private Metrics.Graph eventGraph;
    private Tracker matchTracker;
    private final Plugin plugin;

    public MCStatsTracker(Plugin p) {
        plugin = p;
    }


    /**
     * <p>startTracking.</p>
     */
    @Override
    public void startTracking() {
        // Activate Plugin Metrics
        try {
            if (metrics == null) {
                metrics = new Metrics(plugin);

                eventGraph = metrics.createGraph("Rules by Event");

                Metrics.Graph matchGraph = metrics.createGraph("Matches");
                matchTracker = new Tracker("Matches");

                matchGraph.addPlotter(matchTracker);
            }
            metrics.start();


        } catch (IOException e) {
            LogManager.logger.fine(e.getMessage());
        }
    }

    @Override
    public void incrementMatch() {
        matchTracker.increment();
    }

    /**
     * <p>updateClients.</p>
     *
     * @param filterClientSet A list of filterclients to update.
     */
    @Override
    public void updateClients(List<FilterClient> filterClientSet) {

        ArrayList<String> activeListenerNames = new ArrayList<String>();
        for (FilterClient f : filterClientSet) {
            activeListenerNames.add(f.getShortName());
        }

        // Remove old plotters
        for (Metrics.Plotter p : eventGraph.getPlotters()) {
            if (!activeListenerNames.contains(p.getColumnName())) {
                eventGraph.removePlotter(p);
            }
        }

        // Add new plotters
        for (final FilterClient f : filterClientSet) {
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

}
