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

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.bukkit.BukkitPlayer;
import com.pwn9.filter.util.tag.TagRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Execute a chain of commands by the player.
 *  * NOTE: This method needs to use runTask to operate on the player, as the bukkit API
 * calls are NOT thread-safe.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class CommandChain implements Action {
    private final List<String> commands;

    private CommandChain(List<String> commands) {
        this.commands = commands;
    }

    /** {@inheritDoc} */
    public static Action getAction(String s) throws InvalidActionException
    {
        if (s.isEmpty()) throw new InvalidActionException("No commands were provided to 'cmdchain'");
        return new CommandChain(Arrays.asList(s.split("\\|")));
    }

    /** {@inheritDoc} */
    public void execute(final FilterContext filterTask, FilterService filterService) {
        filterTask.setCancelled();
        final ArrayList<String> parsedCommands = new ArrayList<>();

        for (String cmd : commands)
            parsedCommands.add(TagRegistry.replaceTags(cmd, filterTask));

        MessageAuthor author = filterTask.getAuthor();
        if (author instanceof BukkitPlayer) {
            BukkitPlayer player = (BukkitPlayer)author;
            for (String cmd : parsedCommands) {
                player.executeCommand(cmd);
                filterTask.addLogMessage("Helped " + author.getName() + " execute command: " + cmd);
            }
        } else {
            filterTask.addLogMessage("Could not execute cmdchain on non-player.");
            filterTask.setCancelled();
        }
    }
}
