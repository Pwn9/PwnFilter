/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.util;

import com.pwn9.PwnFilter.FilterTask;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.rules.RuleChain;
import com.pwn9.PwnFilter.rules.action.Action;
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
 * User: ptoal
 * Date: 13-10-31
 * Time: 3:49 PM
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class PointManager implements FilterClient {

    private static PointManager _instance;

    private final Map<UUID,Double> pointsMap = new ConcurrentHashMap<UUID, Double>(8, 0.75f, 2);
    private final TreeMap<Double, Threshold> thresholds = new TreeMap<Double,Threshold>();

    private int leakInterval;
    private Double leakPoints;
    private ScheduledFuture<?> leakHandle;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private PointManager() {}

    /**
     * <p>setup.</p>
     *
     * @return a {@link com.pwn9.PwnFilter.util.PointManager} object.
     */
    public static PointManager setup(Double leakPoints, Integer leakInterval) {

        if (_instance == null) {
            _instance = new PointManager();
        }

        _instance.leakPoints = leakPoints;
        _instance.leakInterval = leakInterval;

        _instance.clearThresholds();

        _instance.startLeaking();

        return _instance;
    }

    /**
     * <p>reset.</p>
     */
    public void reset() {
        stopLeaking();

        // Reset all player points.
        for (UUID id : pointsMap.keySet()) {
            setPoints(id, 0.0);
        }

        startLeaking();
    }

    public void clearThresholds() {
        thresholds.clear();
        // Setup the 0 threshold
        addThreshold("Default", (double) 0, new ArrayList<Action>(), new ArrayList<Action>());
    }


    private void startLeaking() {
        final PointManager pointManager = this;

            final Runnable leakTask = new Runnable() {
                @Override
                public void run() {
                    //Every interval, check point balances, and if they are > 0, subtract leakPoints
                    // from the players balance.  If they reach 0, remove them from the list.
                    for (UUID id : pointManager.getPointsMap()) {
                        pointManager.subPoints(id, leakPoints);
                    }
                }
            };
        if (leakHandle == null) {
            leakHandle = scheduler.scheduleAtFixedRate(leakTask, 1, leakInterval, TimeUnit.SECONDS);
        }
    }


    private void stopLeaking() {
        if (leakHandle != null) {
            leakHandle.cancel(true);
            leakHandle = null;
        }
    }

    /**
     * <p>getInstance.</p>
     *
     * @return a {@link com.pwn9.PwnFilter.util.PointManager} object.
     * @throws java.lang.IllegalStateException if any.
     */
    public static PointManager getInstance() throws IllegalStateException {
        if (_instance == null ) {
            throw new IllegalStateException("Point Manager Not initialized.");
        }
        return _instance;
    }

    /**
     * <p>getPointsMap.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<UUID> getPointsMap() {
        return pointsMap.keySet();
    }

    /**
     * <p>Getter for the field <code>pointsMap</code>.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getPoints(UUID uuid) {
        return (pointsMap.containsKey(uuid))? pointsMap.get(uuid):0.0;
    }

    /**
     * <p>Getter for the field <code>pointsMap</code>.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getPoints(MessageAuthor author) {
        return (pointsMap.containsKey(author.getID()))? pointsMap.get(author.getID()):0.0;
    }

    /**
     * <p>Setter for the field <code>pointsMap</code>.</p>
     *
     * @param points a {@link java.lang.Double} object.
     */
    public void setPoints(UUID id, Double points) {
        Double old = pointsMap.get(id);
        pointsMap.put(id, points);
        executeActions(old, points,id);
    }

    /**
     * <p>addPoints.</p>
     *
     * @param points a {@link java.lang.Double} object.
     */
    public void addPoints(UUID id, Double points) {
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
    public static boolean isEnabled() {
        return _instance != null;
    }

    private void executeActions(final Double fromValue, final Double toValue, final UUID id) {
        final Double oldKey = thresholds.floorKey(fromValue);
        final Double newKey = thresholds.floorKey(toValue);

        if (oldKey.equals(newKey)) return;

        if (fromValue < toValue) {

            // Check to see if we've crossed any thresholds on our way up/down, and if so
            // execute the actions for that crossing.

            for (Map.Entry<Double, Threshold> entry : thresholds.subMap(oldKey, false, newKey, true).entrySet())
                entry.getValue().executeAscending(id);

        } else {
            for (Map.Entry<Double, Threshold> entry : thresholds.subMap(newKey, false, oldKey, true).descendingMap().entrySet())
                entry.getValue().executeDescending(id);
        }

    }

    /**
     * <p>subPoints.</p>
     *
     * @param points a {@link java.lang.Double} object.
     */
    public void subPoints(UUID id, Double points) {
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
        String name;
        Double points;
        List<Action> actionsAscending;
        List<Action> actionsDescending;

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

        public void executeAscending(UUID id) {
            FilterTask state = new FilterTask(new SimpleString(""), id, _instance );
            for (Action a : actionsAscending ) {
                a.execute(state);
            }
        }

        public void executeDescending(UUID id) {
            FilterTask state = new FilterTask(new SimpleString(""), id, _instance );
            for (Action a : actionsDescending ) {
                a.execute(state);
            }
        }

    }

    /**
     * {@inheritDoc}
     *
     * Setup as a Client, so we can create a FilterTask object, and execute actions.
     * Really, the only thing we implement is the getShortName() call.  This is hackish.
     * Should really re-think this implementation.
     */
    @Override
    public String getShortName() {
        return "POINTS";
    }

    /** {@inheritDoc} */
    @Override
    public RuleChain getRuleChain() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isActive() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void activate() {
    }

    /** {@inheritDoc} */
    @Override
    public void shutdown() {
        stopLeaking();
        _instance = null;
    }
}
