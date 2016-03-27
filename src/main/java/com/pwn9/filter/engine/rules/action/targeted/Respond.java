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
import com.pwn9.filter.util.tag.TagRegistry;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Responds to the user with the string provided.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Respond implements Action {
    private final List<String> messageStrings;

    private Respond(List<String> messageStrings) {
        this.messageStrings = messageStrings;
    }

    /**
     * {@inheritDoc}
     */
    public static Action getAction(String s) {
        ArrayList<String> messageStrings = new ArrayList<>();

        for (String message : s.split("\n")) {
            messageStrings.add(ChatColor.translateAlternateColorCodes('&', message));
        }
        return new Respond(ImmutableList.copyOf(messageStrings));
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask) {
        if (filterTask.getAuthor() == null) return;

        final ArrayList<String> preparedMessages = new ArrayList<String>();

        for (String message : messageStrings) {
            preparedMessages.add(TagRegistry.replaceTags(message, filterTask));
        }

        filterTask.getAuthor().sendMessages(preparedMessages);

        filterTask.addLogMessage("Responded to " + filterTask.getAuthor().getName()
                + " with: " + preparedMessages.get(0) + "...");

    }
}

