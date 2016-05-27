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

import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.StatsTracker;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import org.bukkit.plugin.Plugin;
import org.mcstats.Metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An MCStats implementation of statistics tracking.
 * <p/>
 * Created by Sage905 on 15-09-13.
 */
class MCStatsTracker implements StatsTracker {
    private Metrics metrics;
    private Metrics.Graph eventGraph;
    private Tracker matchTracker;
    private final Plugin plugin;

    MCStatsTracker(Plugin p) {
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
            plugin.getLogger().fine(e.getMessage());
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
    public void updateClients(Set<FilterClient> filterClientSet) {

        ArrayList<String> activeListenerNames =
                filterClientSet.stream().map(FilterClient::getShortName).
                        collect(Collectors.toCollection(ArrayList::new));

        // Remove old plotters
        eventGraph.getPlotters().stream().
                filter(p -> !activeListenerNames.contains(p.getColumnName())).
                forEach(p -> eventGraph.removePlotter(p));

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
