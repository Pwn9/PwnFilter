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

import com.pwn9.filter.engine.FilterContext;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.util.PwnFormatter;

import static com.pwn9.filter.util.tag.TagRegistry.replaceTags;

/**
 * Kick the user with a customized message.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
class Kick implements Action {
    // Default message to apply to this burn action
    private static String defaultMessage = "";
    // Message to apply to this kick action
    private final String messageString;

    private Kick(String messageString) {
        this.messageString = messageString;
    }

    /**
     * {@inheritDoc}
     */
    public static Action getAction(String s) {
        return new Kick((s != null && !s.isEmpty() ? PwnFormatter.legacyTextConverter(s) : defaultMessage));
    }

    static void setDefaultMessage(String s) {
        defaultMessage = s;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask, FilterService filterServiceImpl) {

        if (filterTask.getAuthor() instanceof KickTarget) {

            String outMessage = replaceTags(messageString, filterTask);
            ((KickTarget) filterTask.getAuthor()).kick(outMessage);
            filterTask.addLogMessage("Kicked " + filterTask.getAuthor().getName() + ": " + messageString);
        }
    }
}
