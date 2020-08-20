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

package com.pwn9.filter.engine.rules.action.targeted;

import com.pwn9.filter.bukkit.BukkitPlayer;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.util.PwnFormatter;
import com.pwn9.filter.util.tag.TagRegistry;

/**
 * Warn the user with the string provided.
 *
 * @author Sage905
 * @version $Id: $Id
 */

class Warn implements Action {
    // Default message to apply to this burn action
    private static String defaultMessage = "";
    // Message to apply to this warn action
    private final String messageString;


    private Warn(String messageString) {
        this.messageString = messageString;
    }

    public static Action getAction(String s) {
        return new Warn((s != null && !s.isEmpty() ? PwnFormatter.legacyTextConverter(s) : defaultMessage));
    }

    static void setDefaultMessage(String s) {
        defaultMessage = s;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask, FilterService filterService) {
        if (filterTask.getAuthor() instanceof BukkitPlayer) {

            final String message = TagRegistry.replaceTags(messageString, filterTask);
            filterTask.getAuthor().sendMessage(messageString);
            filterTask.addLogMessage("Warned " + filterTask.getAuthor().getName() + ": " + message);
        }
    }
}

