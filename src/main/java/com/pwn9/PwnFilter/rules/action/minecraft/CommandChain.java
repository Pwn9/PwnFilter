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
import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.bukkit.BukkitPlayer;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.util.tags.TagRegistry;

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
public class CommandChain implements Action {
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
            parsedCommands.add(TagRegistry.replaceTags(cmd, state));

        MessageAuthor author = state.getAuthor();
        if (author instanceof BukkitPlayer ) {
            BukkitPlayer player = (BukkitPlayer)author;
            for (String cmd : parsedCommands) {
                player.executeCommand(cmd);
                state.addLogMessage("Helped " + author.getName() + " execute command: " + cmd);
            }
            return true;
        } else {
            state.addLogMessage("Could not execute cmdchain on non-player.");
            state.setCancelled();
            return false;
        }
    }
}
