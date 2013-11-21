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

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Responds to the user with the string provided.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionrespond implements Action {
    ArrayList<String> messageStrings = new ArrayList<String>();

    public void init(String s)
    {
        for ( String message : s.split("\n") ) {
            messageStrings.add(ChatColor.translateAlternateColorCodes('&',message));
        }
    }

    public boolean execute(final FilterState state ) {
        final ArrayList<String> preparedMessages = new ArrayList<String>();

        for (String message : messageStrings) {
            preparedMessages.add(Patterns.replaceVars(message,state));
        }

        state.addLogMessage("Responded to " + state.playerName + " with: "+preparedMessages.get(0) + "...");

        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                for (String m : preparedMessages) {
                    state.getPlayer().sendMessage(m);
                }
            }
        });

        return true;
    }
}

