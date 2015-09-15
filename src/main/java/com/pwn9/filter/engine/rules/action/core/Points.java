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

import com.pwn9.filter.engine.api.FilterTask;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.util.LogManager;
import com.pwn9.filter.util.PointManager;
import org.bukkit.ChatColor;

/**
 * Add the configured number of points to the players account.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Points implements Action {

    String messageString;
    double pointsAmount; // How much to fine the player.

    /** {@inheritDoc} */
    public void init(String s)
    {
        String[] parts;

        parts = s.split("\\s",2);
        try {
            pointsAmount = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e ) {
            throw new IllegalArgumentException("'points' action did not have a valid amount.");
        }
        messageString = (parts.length > 1) ? ChatColor.translateAlternateColorCodes('&',parts[1]) : "";
    }

    /** {@inheritDoc} */
    public void execute(final FilterTask filterTask) {
        MessageAuthor p = filterTask.getAuthor();

        if (p == null) return;

        PointManager pm;
        try {
            pm = PointManager.getInstance();
        } catch (IllegalStateException ex) {
            LogManager.getInstance().debugLow(String.format("Rule: %s has 'then points', but PointManager is disabled in config.yml", filterTask.getRule().getId()));
            return;
        }

        // TODO: Add more comprehensive messaging, as well as details about thresholds.

        pm.addPoints(p.getID(), pointsAmount);

        filterTask.addLogMessage(String.format("Points Accumulated %s : %f. Total: %f", p.getName(), pointsAmount, pm.getPoints(p)));

        if (!messageString.isEmpty()) {
          p.sendMessage(messageString);
        }

    }
}
