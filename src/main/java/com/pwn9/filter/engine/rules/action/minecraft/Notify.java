/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.minecraft;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.tag.TagRegistry;
import org.bukkit.ChatColor;

/**
 * Notify all users with the permission specified in notifyperm:
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Notify implements Action {
    private final String permissionString;
    private final String messageString;


    private Notify(String perm, String message ) {
        this.permissionString = perm;
        this.messageString = message;
    }

    public static Action getAction(String s) throws InvalidActionException
    {
        String[] parts;

        parts = s.split("\\s",2);

        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty())
            throw new InvalidActionException("'notify' action requires a permission or 'console', and a message.");

        return new Notify(parts[0],ChatColor.translateAlternateColorCodes('&',parts[1]));

    }

    /** {@inheritDoc} */
    public void execute(final FilterContext filterTask) {

        // Create the message to send
        final String sendString = TagRegistry.replaceTags(messageString, filterTask);

        //TODO: Create Notification
        //filterTask.get().notifyWithPerm(permissionString, sendString);

    }

}

