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
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionnotify implements Action {
    String permissionString;
    String messageString;

    public void init(String s)
    {
        String[] parts = s.split("\\s",2);
        if (parts.length < 2) {
            return;
        } else {
            permissionString = parts[0];
            messageString = ChatColor.translateAlternateColorCodes('&',parts[1]);
        }
        DataCache.getInstance().addPermission(permissionString);
    }

    public boolean execute(final FilterState state ) {

        // Create the message to send
        final String sendString = Patterns.replaceVars(messageString,state);

        // Get all logged in players who have the required permission and send them the message
        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : DataCache.getInstance().getOnlinePlayers()) {
                    if (DataCache.getInstance().hasPermission(p, permissionString)) {
                        p.sendMessage(sendString);
                    }
                }
            }
        });

        return true;
    }
}

