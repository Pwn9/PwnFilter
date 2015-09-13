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

import com.pwn9.PwnFilter.FilterTask;
import com.pwn9.PwnFilter.minecraft.api.MinecraftPlayer;
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
    public void execute(final FilterTask filterTask) {
        filterTask.setCancelled();
        final String cmd;
        if (filterTask.getAuthor() instanceof MinecraftPlayer) {
            MinecraftPlayer player = (MinecraftPlayer)filterTask.getAuthor();

            if (!command.isEmpty()) {
                cmd = TagRegistry.replaceTags(command, filterTask);
            } else {
                cmd = filterTask.getModifiedMessage().getRaw();
            }
            filterTask.addLogMessage("Helped " + filterTask.getAuthor().getName() + " execute command: " + cmd);
            player.executeCommand(cmd);

        } else {
            filterTask.addLogMessage("Could not execute command as non-player.");
            filterTask.setCancelled();
        }
    }
}
