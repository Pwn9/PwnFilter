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

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.util.PwnFormatter;

/**
 * Burns a player to death.
 * NOTE: This method needs to use runTask to operate on the player, as the bukkit API
 * calls are NOT thread-safe.
 * TODO: Consider hooking this into the custom death message handler.
 *
 * @author Sage905
 * @version $Id: $Id
 */

class Burn implements Action {
    // Default message to apply to this burn action
    private static String defaultMessage = "";
    // Message to apply to this burn action
    private final String messageString;

    private Burn(String messageString) {
        this.messageString = messageString;
    }

    public static Action getAction(String s) {
        return new Burn((s != null && !s.isEmpty() ? PwnFormatter.legacyTextConverter(s) : defaultMessage));
    }

    static void setDefaultMessage(String s) {
        defaultMessage = s;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask, FilterService filterService) {
        MessageAuthor target = filterTask.getAuthor();

        if (target instanceof BurnTarget) {
            if (((BurnTarget) target).burn(5000, messageString)) {
                filterTask.addLogMessage("Burned " + target.getName() + ": " + messageString);
            }
        } else {
            filterTask.addLogMessage("Target not flamable: " + target.getName());
        }

    }
}
