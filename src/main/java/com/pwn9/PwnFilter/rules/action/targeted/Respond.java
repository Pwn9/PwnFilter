/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action.targeted;

import com.pwn9.PwnFilter.FilterTask;
import com.pwn9.PwnFilter.bukkit.PwnFilterPlugin;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.util.tags.TagRegistry;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Responds to the user with the string provided.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Respond implements Action {
    ArrayList<String> messageStrings = new ArrayList<String>();

    /**
     * {@inheritDoc}
     */
    public void init(String s) {
        for (String message : s.split("\n")) {
            messageStrings.add(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute(final FilterTask filterTask) {
        if (filterTask.getAuthor() == null) return false;

        final ArrayList<String> preparedMessages = new ArrayList<String>();

        for (String message : messageStrings) {
            preparedMessages.add(TagRegistry.replaceTags(message, filterTask));
        }

        filterTask.addLogMessage("Responded to " + filterTask.getAuthor().getName()
                + " with: " + preparedMessages.get(0) + "...");

        PwnFilterPlugin.getBukkitAPI().safeBukkitDispatch(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (String m : preparedMessages) {
                            filterTask.getAuthor().sendMessage(m);
                        }
                    }
                });
        return true;
    }
}

