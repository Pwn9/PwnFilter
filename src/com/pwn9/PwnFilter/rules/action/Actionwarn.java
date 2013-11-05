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
import com.pwn9.PwnFilter.util.DefaultMessages;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Warn the user with the string provided.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionwarn implements Action {
    // Message to apply to this warn action
    String messageString;

    public void init(String s)
    {
        messageString = DefaultMessages.prepareMessage(s, "warnmsg");
    }

    public boolean execute(final FilterState state ) {
        if ( state.getPlayer() == null ) return false;
        final String message = Patterns.replaceVars(messageString,state);
        state.addLogMessage("Warned " + state.playerName + ": " + message);
        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                state.getPlayer().sendMessage(message);
            }
        });

        return true;
    }
}

