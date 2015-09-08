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

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.util.tags.TagRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
    public boolean execute(final FilterState state ) {

        // Create the message to send
        final String sendString = TagRegistry.replaceTags(messageString, state);

        if (Bukkit.isPrimaryThread()) {
            // We are in the main thread, just execute API calls directly.
            notifyWithPerm(permissionString, sendString);
        } else {
            Bukkit.getScheduler().runTask(state.plugin,
                new Runnable(){
                    @Override
                    public void run() {
                        notifyWithPerm(permissionString, sendString);
                    }
                });
        }

        return true;
    }

    // Must be called thread-safely.
    private void notifyWithPerm(final String permissionString, final String sendString) {
        // Get all logged in players who have the required permission and send them the message

        if (permissionString.equalsIgnoreCase("console")) {
            Bukkit.getConsoleSender().sendMessage(sendString);
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(permissionString)) {
                    p.sendMessage(sendString);
                }
            }
        }
    }
}

