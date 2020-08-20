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

package com.pwn9.filter.engine.rules.action.core;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.PointManager;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.Rule;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.PwnFormatter;

/**
 * Add the configured number of points to the players account.
 *
 * @author Sage905
 * @version $Id: $Id
 */

class Points implements Action {

    private final String messageString;
    private final double pointsAmount; // How much to fine the player.

    private Points(String messageString, Double pointsAmount) {
        this.messageString = messageString;
        this.pointsAmount = pointsAmount;
    }

    static Action getAction(String s) throws InvalidActionException {
        String[] parts;
        String message;
        double points;
        parts = s.split("\\s", 2);
        try {
            points = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e) {
            throw new InvalidActionException("'points' action did not have a valid amount.");
        }
        message = (parts.length > 1) ? PwnFormatter.legacyTextConverter(parts[1]) : "";

        return new Points(message, points);
    }

    @Override
    public void execute(final FilterContext filterTask, FilterService filterService) {
        MessageAuthor a = filterTask.getAuthor();

        if (a == null) return;

        PointManager pm = filterService.getPointManager();
        if (!pm.isEnabled()) {
            Rule thisRule = filterTask.getRule();
            filterService.getLogger().fine(String.format("Rule: %s has 'then points', but PointManager is disabled in config.yml",
                    (thisRule != null) ? thisRule.getId() : "None"));
            return;
        }

        // TODO: Add more comprehensive messaging, as well as details about thresholds.

        pm.addPoints(a.getId(), pointsAmount);

        filterTask.addLogMessage(String.format("Points Accumulated %s : %f. Total: %f", a.getName(), pointsAmount, pm.getPoints(a)));

        if (!messageString.isEmpty()) {
            a.sendMessage(messageString);
        }

    }
}
