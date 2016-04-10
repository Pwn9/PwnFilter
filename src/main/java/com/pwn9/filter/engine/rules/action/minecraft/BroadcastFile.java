/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2014 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.minecraft;

import com.google.common.collect.ImmutableList;
import com.pwn9.filter.bukkit.BukkitPlayer;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.tag.TagRegistry;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Broadcasts the contents of the named file to all users.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class BroadcastFile implements Action {
    private final ImmutableList<String> messageStrings;

    private BroadcastFile(ArrayList<String> s) {
        messageStrings = ImmutableList.copyOf(s);
    }

    /** {@inheritDoc} */
    static Action getAction(String s, File sourceDir) throws InvalidActionException
    {
        ArrayList<String> messages = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(sourceDir,s)));
            String message;
            while ( (message = br.readLine()) != null ) {
                messages.add(ChatColor.translateAlternateColorCodes('&',message));
            }
        } catch (FileNotFoundException ex) {
            throw new InvalidActionException("File not found while trying to add Action: " + ex.getMessage());
        } catch (IOException ex) {
            throw new InvalidActionException("Error reading: " + s);
        }
        return new BroadcastFile(messages);
    }

    /** {@inheritDoc} */
    public void execute(final FilterContext filterTask, FilterService filterService) {
        final ArrayList<String> preparedMessages = messageStrings.
                stream().
                map(message -> TagRegistry.replaceTags(message, filterTask)).
                collect(Collectors.toCollection(ArrayList::new));

        filterTask.addLogMessage("Broadcasted: " + preparedMessages.get(0) +
                (preparedMessages.size() > 1 ? "..." : ""));

        MessageAuthor author = filterTask.getAuthor();
        if (author instanceof BukkitPlayer) {
            filterTask.addLogMessage("Broadcasted: " + preparedMessages.get(0) + (preparedMessages.size() > 1 ? "..." : ""));
            ((BukkitPlayer) author).getMinecraftAPI().sendBroadcast(preparedMessages);
        }
    }
}

