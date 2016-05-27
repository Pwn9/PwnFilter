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

package com.pwn9.filter.engine.rules.action;

import com.pwn9.filter.engine.FilterConfig;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.ActionToken;
import com.pwn9.filter.engine.rules.action.core.CoreAction;

import java.util.ArrayList;
import java.util.List;

/**
 * This factory returns an action object selected by the rules file.
 * eg: "then kick" would return the Actionkick object.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public final class ActionFactory {

    private final List<Class<? extends ActionToken>> actionTokens =
            new ArrayList<>();
    private final FilterConfig filterConfig;

    public ActionFactory(FilterService filterService) {
        // Ensure that all instances get the Core Actions
        actionTokens.add(CoreAction.class);
        filterConfig = filterService.getConfig();
    }

    public Action getActionFromString(String s) throws InvalidActionException {
        String[] parts = s.split("\\s", 2);
        String actionName = parts[0];
        String actionData;
        actionData = ((parts.length > 1) ? parts[1] : "");

        return getAction(actionName, actionData);
    }

    public Action getAction(final String actionName, final String actionData)
            throws InvalidActionException {

        // Scan all tokens for a match

        for (Class<? extends ActionToken> tokens : actionTokens) {
            for (ActionToken token : tokens.getEnumConstants()) {
                if (token.toString().equals(actionName.toUpperCase())) {
                    return token.getAction(actionData, filterConfig);
                }
            }
        }
        throw new InvalidActionException("Unable to implement action: " + actionName
                + " / " + actionData);
    }

    synchronized public void addActionTokens(Class<? extends ActionToken> tokenEnum) {
        actionTokens.add(tokenEnum);
    }

}

