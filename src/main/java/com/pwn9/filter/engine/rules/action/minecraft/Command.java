/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.minecraft;

import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.minecraft.api.MinecraftPlayer;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.util.tag.TagRegistry;

/**
 * Execute a command as a player.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Command implements Action {

    String command;

    public Command(String s) {
        this.command = s;
    }

    /** {@inheritDoc} */
    public static Action getAction(String s) throws InvalidActionException
    {
        if (s.isEmpty()) throw new InvalidActionException("No command was provided to 'command'");
        return new Command(s);
    }

    /** {@inheritDoc} */
    public void execute(final FilterContext filterTask) {
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
