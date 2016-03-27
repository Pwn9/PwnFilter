/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.targeted;

import com.google.common.collect.ImmutableList;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.tag.TagRegistry;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responds to the user with the string provided.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class RespondFile implements Action {
    private final List<String> messageStrings;

    private RespondFile(List<String> messageStrings) {
        this.messageStrings = messageStrings;
    }

    public static Action getAction(String s, File sourceDir ) throws InvalidActionException {
        ArrayList<String> messageStrings = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(sourceDir, s)));
            String message;
            while ((message = br.readLine()) != null) {
                messageStrings.add(ChatColor.translateAlternateColorCodes('&', message));
            }
        } catch (FileNotFoundException ex) {
            throw new InvalidActionException("File not found while trying to add Action: " + ex.getMessage());
        } catch (IOException ex) {
            throw new InvalidActionException("Error reading file: " + s);
        }
        return new RespondFile(ImmutableList.copyOf(messageStrings));
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask) {
        final ArrayList<String> preparedMessages = new ArrayList<String>();

        for (String message : messageStrings) {
            preparedMessages.add(TagRegistry.replaceTags(message, filterTask));
        }

        filterTask.getAuthor().sendMessages(preparedMessages);

        filterTask.addLogMessage("Responded to " + filterTask.getAuthor().getName() + " with: " + preparedMessages.get(0) + "...");

    }
}

