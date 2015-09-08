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
import com.pwn9.PwnFilter.bukkit.BukkitPlayer;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.util.tags.TagRegistry;

/**
 * Execute a command as a player.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Command implements Action {
    String command;

    /** {@inheritDoc} */
    public void init(String s)
    {
        if ((command = s).isEmpty()) throw new IllegalArgumentException("No command was provided to 'command'");
    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {
        state.cancel = true;
        final String cmd;
        if (state.getAuthor() instanceof BukkitPlayer ) {
            BukkitPlayer player = (BukkitPlayer)state.getAuthor();

            if (!command.isEmpty()) {
                cmd = TagRegistry.replaceTags(command, state);
            } else {
                cmd = state.getModifiedMessage().getColoredString();
            }
            state.addLogMessage("Helped " + player.getName() + " execute command: " + cmd);
            player.executeCommand(cmd);

            return true;
        } else {
            state.addLogMessage("Could not execute command as non-player.");
            state.setCancelled();
            return false;
        }
    }
}
