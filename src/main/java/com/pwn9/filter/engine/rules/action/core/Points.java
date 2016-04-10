/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.core;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import org.bukkit.ChatColor;

/**
 * Add the configured number of points to the players account.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Points implements Action {

    private final String messageString;
    private final double pointsAmount; // How much to fine the player.

    private Points(String messageString, Double pointsAmount){
        this.messageString = messageString;
        this.pointsAmount = pointsAmount;
    }

    static Action getAction(String s) throws InvalidActionException
    {
        String[] parts;
        String message;
        Double points;
        parts = s.split("\\s",2);
        try {
            points = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e ) {
            throw new InvalidActionException("'points' action did not have a valid amount.");
        }
        message = (parts.length > 1) ? ChatColor.translateAlternateColorCodes('&',parts[1]) : "";

        return new Points(message, points);
    }

    @Override
    public void execute(final FilterContext filterTask) {
//        UUID p = filterTask.getAuthor();
//
//        if (p == null) return;
//
//        PointManager pm;
//        try {
//            pm = PointManager.getInstance();
//        } catch (IllegalStateException ex) {
//            FileLogger.getInstance().debugLow(String.format("Rule: %s has 'then points', but PointManager is disabled in config.yml", filterTask.getRule().getId()));
//            return;
//        }
//
//        // TODO: Add more comprehensive messaging, as well as details about thresholds.
//
//        pm.addPoints(p.getID(), pointsAmount);
//
//        filterTask.addLogMessage(String.format("Points Accumulated %s : %f. Total: %f", p.getName(), pointsAmount, pm.getPoints(p)));
//
//        if (!messageString.isEmpty()) {
//          p.sendMessage(messageString);
//        }

    }
}
