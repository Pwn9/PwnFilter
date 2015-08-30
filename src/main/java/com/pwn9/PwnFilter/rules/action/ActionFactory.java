/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action;

/**
 * This factory returns an action object selected by the rules file.
 * eg: "then kick" would return the Actionkick object.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public final class ActionFactory {

    /**
     * <p>getActionFromString.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link com.pwn9.PwnFilter.rules.action.Action} object.
     */
    public static Action getActionFromString(String s)
    {
        String[] parts = s.split("\\s",2);
        String actionName = parts[0];
        String actionData;
        actionData = ((parts.length > 1) ? parts[1]:"");

        return getAction(actionName, actionData);
    }

    /**
     * <p>getAction.</p>
     *
     * @param actionName a {@link java.lang.String} object.
     * @param actionData a {@link java.lang.String} object.
     * @return a {@link com.pwn9.PwnFilter.rules.action.Action} object.
     */
    public static Action getAction(final String actionName, final String actionData)
    {
        // Return a subclass instance based on actionName.
        try {
            Action newAction;
            String className = "com.pwn9.PwnFilter.rules.action.Action" + actionName;
            newAction = (Action)(Class.forName(className).newInstance());
            newAction.init(actionData);
            return newAction;
        } catch ( ClassNotFoundException ex ) {
            return null;
        } catch ( InstantiationException ex ) {
            return null;
        } catch ( IllegalAccessException ex) {
            return null;
        }
    }
}

