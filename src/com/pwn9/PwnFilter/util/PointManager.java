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

import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.rules.action.ActionFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * Mange the Points system of PwnFilter
 * User: ptoal
 * Date: 13-10-31
 * Time: 3:49 PM
 */
public class PointManager {

    private static PointManager _instance;
    private final PwnFilter plugin;

    private HashMap<Player,Double> playerPoints = new HashMap<Player, Double>();
    private TreeSet<Threshold> thresholds = new TreeSet<Threshold>();


    private PointManager(PwnFilter plugin) {
        this.plugin = plugin;
    }

    public static PointManager setup(PwnFilter p) {
        ConfigurationSection pointsSection = p.getConfig().getConfigurationSection("points");
        if (!pointsSection.getBoolean("enabled"))
            return null;

        if (_instance == null ) {
            _instance = new PointManager(p);
        }

        _instance.parseThresholds(pointsSection.getConfigurationSection("thresholds"));

        return _instance;
    }

    private void parseThresholds(ConfigurationSection cs) {

        for (String threshold : cs.getKeys(false)) {
            Threshold newThreshold = new Threshold();
            newThreshold.name = cs.getString(threshold+".name");
            newThreshold.points = cs.getDouble(threshold+".points");
            newThreshold.actions = new ArrayList<Action>();

            for (String action : cs.getStringList(threshold + ".actions")) {
                Action actionObject = ActionFactory.getActionFromString(action);
                if (actionObject != null) {
                    newThreshold.actions.add(actionObject);
                } else {
                    LogManager.logger.warning("Unable to parse action in threshold: " + threshold);
                }
            }
            thresholds.add(newThreshold);
        }

    }


    public static PointManager getInstance() {
        if (_instance == null ) {
            throw new IllegalStateException("Point Manager Not initialized!");
        }
        return _instance;
    }

    public Double getPlayerPoints(Player p) {
        return playerPoints.get(p);
    }

    public void setPlayerPoints(Player p, Double points) {
        playerPoints.put(p,points);
    }

    public void addPlayerPoints(Player p, Double points) {
        Double current = playerPoints.get(p);
        if (current == null) current = 0.0;
        current += points;
        playerPoints.put(p,current);
    }

    public void subPlayerPoints(Player p, Double points) {
        Double current = playerPoints.get(p);
        if (current == null) current = 0.0;
        current -= points;
        playerPoints.put(p,(current < 0.0?0:current));
    }


    class Threshold implements Comparable<Threshold> {
        String name;
        Double points;
        List<Action> actions = new ArrayList<Action>();

        public int compareTo(Threshold o) {
            return Double.compare(this.points, o.points);
        }
    }

}
