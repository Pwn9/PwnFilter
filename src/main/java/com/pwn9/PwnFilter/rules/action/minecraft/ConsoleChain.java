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
import com.pwn9.PwnFilter.bukkit.PwnFilterPlugin;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.util.tags.TagRegistry;

import java.util.ArrayList;

/**
 * Execute a chain of console commands
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class ConsoleChain implements Action {
    String[] commands;

    /** {@inheritDoc} */
    public void init(String s)
    {
        commands = s.split("\\|");
        if (commands[0].isEmpty()) throw new IllegalArgumentException("No commands were provided to 'conchain'");
    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {
        final ArrayList<String> parsedCommands = new ArrayList<String>();

        for (String cmd : commands)
            parsedCommands.add(TagRegistry.replaceTags(cmd, state));

        for (final String cmd : parsedCommands) {
            state.addLogMessage("Sending console command: " + cmd);
            PwnFilterPlugin.sendConsoleCommand(cmd);
        }

        return true;

    }

}
