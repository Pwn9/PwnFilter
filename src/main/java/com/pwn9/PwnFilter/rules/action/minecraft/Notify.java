/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action.minecraft;

import com.pwn9.PwnFilter.FilterTask;
import com.pwn9.PwnFilter.minecraft.api.MinecraftConsole;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.util.tags.TagRegistry;
import org.bukkit.ChatColor;

/**
 * Notify all users with the permission specified in notifyperm:
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Notify implements Action {
    String permissionString;
    String messageString;

    /** {@inheritDoc} */
    public void init(String s)
    {
        String[] parts;

        parts = s.split("\\s",2);

        permissionString = parts[0];

        if (permissionString.isEmpty()) throw new IllegalArgumentException("'notify' action requires a permission or 'console'");

        if (parts.length > 1) {
            messageString = ChatColor.translateAlternateColorCodes('&',parts[1]);
        } else {
            throw new IllegalArgumentException("'notify' action requires a message string");
        }

    }

    /** {@inheritDoc} */
    public void execute(final FilterTask filterTask) {

        // Create the message to send
        final String sendString = TagRegistry.replaceTags(messageString, filterTask);

        MinecraftConsole.getInstance().notifyWithPerm(permissionString, sendString);

    }

}

