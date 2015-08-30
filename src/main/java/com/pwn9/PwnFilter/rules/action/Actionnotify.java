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

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Notify all users with the permission specified in notifyperm:
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionnotify implements Action {
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

        DataCache.getInstance().addPermission(permissionString);

    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {

        // Create the message to send
        final String sendString = Patterns.replaceVars(messageString,state);

        if (permissionString.equalsIgnoreCase("console")) {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getConsoleSender().sendMessage(sendString);
                }
            };
            task.runTask(state.plugin);
        }  else {
            // Get all logged in players who have the required permission and send them the message
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player p : DataCache.getInstance().getOnlinePlayers()) {
                        if (DataCache.getInstance().hasPermission(p, permissionString)) {
                            p.sendMessage(sendString);
                        }
                    }
                }
            };
            task.runTask(state.plugin);
        }

        return true;
    }
}

