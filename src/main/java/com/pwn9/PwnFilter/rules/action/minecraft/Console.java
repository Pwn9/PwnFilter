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

/**
 * Execute a console command
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Console implements Action {
    String command;

    /** {@inheritDoc} */
    public void init(String s)
    {
        if ((command = s).isEmpty()) throw new IllegalArgumentException("No command was provided to 'console'");

    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {
        final String cmd = TagRegistry.replaceTags(command, state);
        state.addLogMessage("Sending console command: " + cmd);
        PwnFilterPlugin.sendConsoleCommand(cmd);
        return true;
    }
}
