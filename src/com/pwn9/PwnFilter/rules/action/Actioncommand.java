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
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Execute a command as a player.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actioncommand implements Action {
    String command;

    public void init(String s)
    {
        if ((command = s).isEmpty()) throw new IllegalArgumentException("No command was provided to 'command'");
    }

    public boolean execute(final FilterState state ) {
        state.cancel = true;
        final String cmd;
        if (state.getPlayer() != null ) {
            if (!command.isEmpty()) {
                cmd = Patterns.replaceVars(command, state);
                state.addLogMessage("Helped " + state.playerName + " execute command: " + cmd);
            } else {
                cmd = state.getModifiedMessage().getColoredString();
            }
            state.addLogMessage("Helped " + state.playerName + " execute command: " + cmd);
            Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
                @Override
                public void run() {
                    state.getPlayer().chat("/" + cmd);
                }
            });

            return true;
        } else {
            state.addLogMessage("Could not execute command as non-player.");
            state.setCancelled(true);
            return false;
        }
    }
}
