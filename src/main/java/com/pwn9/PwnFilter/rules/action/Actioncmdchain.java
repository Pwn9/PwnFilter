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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Execute a chain of commands by the player.
 *  * NOTE: This method needs to use runTask to operate on the player, as the bukkit API
 * calls are NOT thread-safe.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Actioncmdchain implements Action {
    String[] commands;

    /** {@inheritDoc} */
    public void init(String s)
    {
        commands = s.split("\\|");
        if (commands[0].isEmpty()) throw new IllegalArgumentException("No commands were provided to 'cmdchain'");
    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {
        state.cancel = true;
        final ArrayList<String> parsedCommands = new ArrayList<String>();

        for (String cmd : commands)
            parsedCommands.add(Patterns.replaceVars(cmd, state));

        if (state.getPlayer() != null ) {
            for (final String cmd : parsedCommands)
                state.addLogMessage("Helped " + state.playerName + " execute command: " + cmd);

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    for (String cmd : parsedCommands ) {
                        state.getPlayer().performCommand(cmd);
                    }
                }
            };
            task.runTask(state.plugin);
            return true;
        } else {
            state.addLogMessage("Could not execute cmdchain on non-player.");
            state.setCancelled(true);
            return false;
        }
    }
}
