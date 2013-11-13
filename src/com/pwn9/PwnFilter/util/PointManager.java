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

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.rules.RuleChain;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.rules.action.ActionFactory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mange the Points system of PwnFilter
 * User: ptoal
 * Date: 13-10-31
 * Time: 3:49 PM
 */
public class PointManager implements FilterClient {

    private static PointManager _instance;
    private final PwnFilter plugin;

    private ConcurrentHashMap<Player,Double> playerPoints = new ConcurrentHashMap<Player, Double>();
    private TreeMap<Double, Threshold> thresholds = new TreeMap<Double,Threshold>();

    private int leakInterval;
    private Double leakPoints;
    private BukkitTask leakTask;

    private PointManager(PwnFilter p) {
        this.plugin = p;
    }

    public static PointManager setup() {
        PwnFilter pwnFilter = PwnFilter.getInstance();
        ConfigurationSection pointsSection = pwnFilter.getConfig().getConfigurationSection("points");
        if (!pointsSection.getBoolean("enabled"))
            return null;

        if (_instance == null ) {
            _instance = new PointManager(pwnFilter);
        }

        Double leakPoints = pointsSection.getDouble("leak.points");
        int leakInterval = pointsSection.getInt("leak.rate");

        if (leakPoints > 0.0 && leakInterval > 0) {
            _instance.leakPoints = leakPoints;
            _instance.leakInterval = leakInterval;
            _instance.startLeaking();
        } else {
            _instance.stopLeaking();
        }

        _instance.parseThresholds(pointsSection.getConfigurationSection("thresholds"));

        return _instance;
    }
    private void startLeaking() {
        final PointManager pointManager = this;

        if (leakTask == null) {
            leakTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new BukkitRunnable() {
                @Override
                public void run() {
                    //Every interval, check point balances, and if they are > 0, subtract leakPoints
                    // from the players balance.  If they reach 0, remove them from the list.
                    Set<Player> players = pointManager.getPlayersWithPoints();
                    for (Player p : players) {
                        pointManager.subPlayerPoints(p,leakPoints);
                    }
                }
            }, 20, 20*leakInterval);
        }
    }


    private void stopLeaking() {
        if (leakTask != null) {
            leakTask.cancel();
            leakTask = null;
        }
    }
    private void parseThresholds(ConfigurationSection cs) {

        // Setup the 0 threshold
        Threshold defaultThreshold = new Threshold();
        defaultThreshold.name = "Default";
        defaultThreshold.points = (double)0;
        thresholds.put((double)0,defaultThreshold);

        for (String threshold : cs.getKeys(false)) {
            Threshold newThreshold = new Threshold();
            newThreshold.name = cs.getString(threshold + ".name");
            newThreshold.points = cs.getDouble(threshold+".points");

            for (String action : cs.getStringList(threshold + ".actions.ascending")) {
                Action actionObject = ActionFactory.getActionFromString(action);
                if (actionObject != null) {
                    newThreshold.actionsAscending.add(actionObject);
                } else {
                    LogManager.logger.warning("Unable to parse action in threshold: " + threshold);
                }
            }
            for (String action : cs.getStringList(threshold + ".actions.descending")) {
                Action actionObject = ActionFactory.getActionFromString(action);
                if (actionObject != null) {
                    newThreshold.actionsDescending.add(actionObject);
                } else {
                    LogManager.logger.warning("Unable to parse action in threshold: " + threshold);
                }
            }
            thresholds.put(newThreshold.points, newThreshold);
        }

    }


    public static PointManager getInstance() {
        if (_instance == null ) {
            return setup();
        }
        return _instance;
    }

    public Set<Player> getPlayersWithPoints() {
        return playerPoints.keySet();
    }

    public Double getPlayerPoints(Player p) {
        return playerPoints.get(p);
    }

    public void setPlayerPoints(Player p, Double points) {
        Double old = playerPoints.get(p);
        playerPoints.put(p,points);
        executeActions(old, points,p);
    }

    public void addPlayerPoints(Player p, Double points) {
        Double current = playerPoints.get(p);
        if (current == null) current = 0.0;
        Double updated = current + points;

        playerPoints.put(p,updated);

        executeActions(current, updated, p);

    }

    private void executeActions(Double fromValue, Double toValue, Player p) {
        Double oldKey = thresholds.floorKey(fromValue);
        Double newKey = thresholds.floorKey(toValue);

        if (oldKey.equals(newKey)) return;

        if (fromValue < toValue) {

            // Check to see if we've crossed any thresholds on our way up/down, and if so
            // execute the actions for that crossing.

            for (Map.Entry<Double,Threshold> entry : thresholds.subMap(oldKey, false, newKey, true).entrySet())
                entry.getValue().executeAscending(p);
        } else {
            for (Map.Entry<Double, Threshold> entry : thresholds.subMap(newKey, false, oldKey, true).descendingMap().entrySet())
                entry.getValue().executeDescending(p);
        }

    }

    public void subPlayerPoints(Player p, Double points) {
        Double updated;
        Double current = playerPoints.get(p);
        if (current == null) current = 0.0;
        updated = current - points;
        if ( updated <=0 ) {
            playerPoints.remove(p);
            updated = 0.0;
        }
        playerPoints.put(p,updated);

        executeActions(current, updated, p);

    }


    class Threshold implements Comparable<Threshold> {
        String name;
        Double points;
        List<Action> actionsAscending = new ArrayList<Action>();
        List<Action> actionsDescending = new ArrayList<Action>();

        public int compareTo(Threshold o) {
            return Double.compare(this.points, o.points);
        }

        public void executeAscending(Player player) {
            FilterState state = new FilterState(plugin, "", player, _instance );
            for (Action a : actionsAscending ) {
                a.execute(state);
            }
        }

        public void executeDescending(Player player) {
            FilterState state = new FilterState(plugin, "", player, _instance );
            for (Action a : actionsDescending ) {
                a.execute(state);
            }
        }

    }

    /**
     * Setup as a Client, so we can create a FilterState object, and execute actions.
     * Really, the only thing we implament is the getShortName() call.  This is hackish.
     * Should really re-think this implementation.
     */
    @Override
    public String getShortName() {
        return "POINTS";
    }

    @Override
    public RuleChain getRuleChain() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void activate(Configuration config) {
    }

    @Override
    public void shutdown() {
    }
}
