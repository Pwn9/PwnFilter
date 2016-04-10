/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import com.pwn9.filter.util.SimpleString;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;

/**
 * Mange the Points system of PwnFilter.
 *
 * Each entity that is capable of having points assigned must have a UUID.
 * This manager will track the points assigned to a particular UUID.
 *
 * TODO: Refactor to remove the FilterClient implementation. (IoC, instead?)
 * TODO: Remove Dependency on BukkitTask to leak points.
 *
 * User: Sage905
 * Date: 13-10-31
 * Time: 3:49 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class PointManager implements FilterClient {

    private final Map<UUID,Double> pointsMap = new ConcurrentHashMap<>(8, 0.75f, 2);
    private final TreeMap<Double, Threshold> thresholds = new TreeMap<>();

    private int leakInterval = 0;

    private Double leakPoints = 0.0;
    private ScheduledFuture<?> scheduledFuture;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private final FilterService filterService;

    PointManager(FilterService filterService) {
        this.filterService = filterService;
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

    public void clearThresholds() {
        thresholds.clear();
        // Setup the 0 threshold
        addThreshold("Default", (double) 0, new ArrayList<>(), new ArrayList<>());
    }


    public void start() {
        if (scheduledFuture == null) {
            final PointManager pointManager = this;

            final Runnable leakTask = () -> {
                //Every interval, check point balances, and if they are > 0, subtract leakPoints
                // from the players balance.  If they reach 0, remove them from the list.
                for (UUID id : pointManager.getPointsMap()) {
                    pointManager.subPoints(id, leakPoints);
                }
            };
            scheduledFuture = scheduler.scheduleAtFixedRate(leakTask, 1, leakInterval, TimeUnit.SECONDS);
        }
    }


    private void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }


    /**
     * <p>getPointsMap.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<java.util.UUID> getPointsMap() {
        return pointsMap.keySet();
    }

    /**
     * <p>Getter for the field <code>pointsMap</code>.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getPoints(java.util.UUID uuid) {
        return (pointsMap.containsKey(uuid))? pointsMap.get(uuid):0.0;
    }

    /**
     * <p>Getter for the field <code>pointsMap</code>.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getPoints(MessageAuthor author) {
        return (pointsMap.containsKey(author.getId()))? pointsMap.get(author.getId()):0.0;
    }

    /**
     * <p>Setter for the field <code>pointsMap</code>.</p>
     *
     * @param points a {@link java.lang.Double} object.
     */
    public void setPoints(java.util.UUID id, Double points) {
        Double old = pointsMap.get(id);
        pointsMap.put(id, points);
        executeActions(old, points,id);
    }

    /**
     * <p>addPoints.</p>
     *
     * @param points a {@link java.lang.Double} object.
     */
    public void addPoints(java.util.UUID id, Double points) {
        Double current = pointsMap.get(id);
        if (current == null) current = 0.0;
        Double updated = current + points;

        pointsMap.put(id, updated);

        executeActions(current, updated, id);

    }

    /**
     * <p>isEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isEnabled() {
        return scheduledFuture != null;
    }

    private void executeActions(final Double fromValue, final Double toValue, final java.util.UUID id) {
        final Double oldKey = thresholds.floorKey(fromValue);
        final Double newKey = thresholds.floorKey(toValue);

        if (oldKey.equals(newKey)) return;

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

    /**
     * <p>subPoints.</p>
     *
     * @param points a {@link java.lang.Double} object.
     */
    public void subPoints(java.util.UUID id, Double points) {
        Double updated;
        Double current = pointsMap.get(id);
        if (current == null) current = 0.0;
        updated = current - points;
        if ( updated <=0 ) {
            pointsMap.remove(id);
            updated = 0.0;
        }
        pointsMap.put(id, updated);

        executeActions(current, updated, id);

    }

    public void addThreshold(String name, Double points, List<Action> ascending, List<Action> descending) {
        thresholds.put(points, new Threshold(name, points, ascending, descending));
    }

    class Threshold implements Comparable<Threshold> {
        final String name;
        final Double points;
        final List<Action> actionsAscending;
        final List<Action> actionsDescending;

        protected Threshold(String name, Double points, List<Action> ascending, List<Action> descending) {
            this.name = name;
            this.points = points;
            this.actionsAscending = ascending;
            this.actionsDescending = descending;
        }

        @Override
        public int compareTo(@NotNull Threshold o) {
            return Double.compare(this.points, o.points);
        }

        public void executeAscending(UUID id, FilterClient client) {
            FilterContext state = new FilterContext(new SimpleString(""), getFilterService().getAuthor(id), client );
            for (Action a : actionsAscending ) {
                a.execute(state, filterService);
            }

        }

        public void executeDescending(UUID id, FilterClient client) {
            FilterContext state = new FilterContext(new SimpleString(""), getFilterService().getAuthor(id), client );
            for (Action a : actionsDescending ) {
                a.execute(state, filterService);
            }
        }

    }

    @Override
    public String getShortName() {
        return "POINTS";
    }

    @Override
    public FilterService getFilterService() {
        return filterService;
    }

    /** {@inheritDoc} */
    @Override
    public RuleChain getRuleChain() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isActive() {
        return scheduledFuture != null ;
    }

    /** {@inheritDoc} */
    @Override
    public void activate() {
        start();
    }

    /** {@inheritDoc} */
    @Override
    public void shutdown() {
        stop();
    }
}
