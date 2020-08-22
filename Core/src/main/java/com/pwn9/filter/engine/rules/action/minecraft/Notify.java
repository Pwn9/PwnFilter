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

package com.pwn9.filter.engine.rules.action.minecraft;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.PwnFormatter;
import com.pwn9.filter.util.tag.TagRegistry;

/**
 * Notify all users with the permission specified in notifyperm:
 *
 * @author Sage905
 * @version $Id: $Id
 */

class Notify implements Action {
    private final String permissionString;
    private final String messageString;


    private Notify(String perm, String message) {
        this.permissionString = perm;
        this.messageString = message;
    }

    public static Action getAction(String s) throws InvalidActionException {
        String[] parts;

        parts = s.split("\\s", 2);

        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty())
            throw new InvalidActionException("'notify' action requires a permission or 'console', and a message.");

        return new Notify(parts[0], PwnFormatter.legacyTextConverter(parts[1]));

    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterContext, FilterService filterService) {

        // Create the message to send
        final String sendString = TagRegistry.replaceTags(messageString, filterContext);

        // This will set the message for the perm, overwriting older messages.
        filterContext.setNotifyMessage(permissionString, sendString);

    }

}

