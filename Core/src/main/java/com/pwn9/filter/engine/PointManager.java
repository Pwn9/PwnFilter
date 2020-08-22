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

package com.pwn9.filter.engine;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import com.pwn9.filter.util.SimpleString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Mange the Points system of PwnFilter.
 * <p>
 * Each entity that is capable of having points assigned must have a UUID.
 * This manager will track the points assigned to a particular UUID.
 * <p>
 * User: Sage905
 * Date: 13-10-31
 * Time: 3:49 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class PointManager implements FilterClient {

    private final Map<UUID, Double> pointsMap = new ConcurrentHashMap<>(8, 0.75f, 2);
    private final TreeMap<Double, Threshold> thresholds = new TreeMap<>();
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private final FilterService filterService;
    private int leakInterval = 10;
    private Double leakPoints = 1.0;
    private ScheduledFuture<?> scheduledFuture;

    PointManager(FilterService filterService) {
        this.filterService = filterService;
        this.clearThresholds();
    }

    public void reset() {
        stop();
        // Reset all player points.
        pointsMap.clear();
        start();
    }

    public int getLeakInterval() {
        return leakInterval;
    }

    public void setLeakInterval(int leakInterval) {
        this.leakInterval = leakInterval;
    }

    public Double getLeakPoints() {
        return leakPoints;
    }

    public void setLeakPoints(Double leakPoints) {
        this.leakPoints = leakPoints;
    }

    private void clearThresholds() {
        thresholds.clear();
        // Setup the 0 threshold
        addThreshold("Default", (double) 0, new ArrayList<>(), new ArrayList<>());
    }

    void leakTask(PointManager pointManager) {
        //Every interval, check point balances, and if they are > 0, subtract leakPoints
        // from the players balance.  If they reach 0, remove them from the list.
        pointManager.getPointsMap()
                .forEach((id) -> pointManager.subPoints(id, leakPoints));
    }

    public void start() {
        if (scheduledFuture == null) {
            scheduledFuture = scheduler.scheduleAtFixedRate(
                    () -> leakTask(this), 1, leakInterval, TimeUnit.SECONDS);
        }
    }


    private void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }


    Set<UUID> getPointsMap() {
        return pointsMap.keySet();
    }

    Double getPoints(UUID uuid) {
        return pointsMap.getOrDefault(uuid, 0.0);
    }

    public Double getPoints(MessageAuthor author) {
        return pointsMap.getOrDefault(author.getId(), 0.0);
    }

    void setPoints(UUID id, Double points) {
        Double old = pointsMap.getOrDefault(id, 0d);
        pointsMap.put(id, points);
    }

    public void addPoints(UUID id, Double points) {
        Double current = pointsMap.getOrDefault(id, 0d);
        Double updated = current + points;

        pointsMap.put(id, updated);

        executeActions(current, updated, id);

    }

    public boolean isEnabled() {
        return scheduledFuture != null;
    }

    private void executeActions(final Double fromValue, final Double toValue, final UUID id) {
        final Double oldKey = thresholds.floorKey(fromValue);
        final Double newKey = thresholds.floorKey(toValue);

        if (oldKey == null || newKey == null || oldKey.equals(newKey)) return;

        if (fromValue < toValue) {

            // Check to see if we've crossed any thresholds on our way up/down, and if so
            // execute the actions for that crossing.

            for (Map.Entry<Double, Threshold> entry : thresholds.subMap(oldKey, false, newKey, true).entrySet())
                entry.getValue().executeAscending(id, this);

        } else {
            for (Map.Entry<Double, Threshold> entry : thresholds.subMap(newKey, false, oldKey, true).descendingMap().entrySet())
                entry.getValue().executeDescending(id, this);
        }

    }

    void subPoints(UUID id, Double points) {
        double updated;
        Double current = pointsMap.getOrDefault(id, 0d);
        updated = current - points;
        if (updated <= 0) {
            pointsMap.remove(id);
            updated = 0.0;
        } else {
            pointsMap.put(id, updated);
        }

        executeActions(current, updated, id);

    }

    public void addThreshold(String name, Double points, List<Action> ascending, List<Action> descending) {
        thresholds.put(points, new Threshold(name, points, ascending, descending));
    }

    @Override
    public String getShortName() {
        return "POINTS";
    }

    @Override
    public FilterService getFilterService() {
        return filterService;
    }

    @Override
    public RuleChain getRuleChain() {
        return null;
    }

    @Override
    public boolean isActive() {
        return scheduledFuture != null;
    }

    @Override
    public void activate() {
        start();
    }

    @Override
    public void shutdown() {
        stop();
    }

    class Threshold implements Comparable<Threshold> {
        final String name;
        final Double points;
        final List<Action> actionsAscending;
        final List<Action> actionsDescending;

        Threshold(String name, Double points, List<Action> ascending, List<Action> descending) {
            this.name = name;
            this.points = points;
            this.actionsAscending = ascending;
            this.actionsDescending = descending;
        }

        @Override
        public int compareTo(@NotNull Threshold o) {
            return Double.compare(this.points, o.points);
        }

        void executeAscending(UUID id, FilterClient client) {
            FilterContext state = new FilterContext(new SimpleString(""), getFilterService().getAuthor(id), client);
            for (Action a : actionsAscending) {
                client.getFilterService().getLogger().finest("Executing Action: " + a + " on " + state.getAuthor().getName());
                a.execute(state, filterService);
            }
        }

        void executeDescending(UUID id, FilterClient client) {
            FilterContext state = new FilterContext(new SimpleString(""), getFilterService().getAuthor(id), client);
            for (Action a : actionsDescending) {
                client.getFilterService().getLogger().finest("Executing Action: " + a + " on " + state.getAuthor().getName());
                a.execute(state, filterService);
            }
        }

    }
}
