/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action;

import com.pwn9.filter.engine.api.Action;

import java.util.HashMap;

/**
 * This factory returns an action object selected by the rules file.
 * eg: "then kick" would return the Actionkick object.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public final class ActionFactory {

    public static final HashMap<String, Class<? extends Action>> actionClassMap =
            new HashMap<String, Class<? extends Action>>();

    /**
     * <p>getActionFromString.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link Action} object.
     */
    public static Action getActionFromString(String s) {
        String[] parts = s.split("\\s", 2);
        String actionName = parts[0];
        String actionData;
        actionData = ((parts.length > 1) ? parts[1] : "");

        return getAction(actionName, actionData);
    }

    /**
     * <p>getAction.</p>
     *
     * @param actionName a {@link java.lang.String} object.
     * @param actionData a {@link java.lang.String} object.
     * @return a {@link Action} object.
     */
    public static Action getAction(final String actionName, final String actionData) {
        // Return a subclass instance based on actionName.

        Class<? extends Action> actionClass = actionClassMap.get(actionName);

        if (actionClass != null) {
            Action newAction;
            try {
                newAction = actionClass.newInstance();
            } catch (Exception e) {
                return null;
            }
            newAction.init(actionData);
            return newAction;
        }

        return null;

    }

    synchronized public static void add(String keyword, Class<? extends Action> actionClass) {

        Class<? extends Action> current = actionClassMap.get(keyword);

        if (current == null ) {
            actionClassMap.put(keyword, actionClass);
        } else if (current != actionClass ) {
            throw new RuntimeException("Action Keyword already Registered: " +
                keyword + "new class: " + actionClass.getName() +
                ". current class: " + current.getName());
        }

    }

}

