/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.util.LogManager;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Responds to the user with the string provided.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionrespondfile implements Action {
    ArrayList<String> messageStrings = new ArrayList<String>();

    /** {@inheritDoc} */
    public void init(String s)
    {
        try {
            BufferedReader br = PwnFilter.getInstance().getBufferedReader(s);
            String message;
            while ( (message = br.readLine()) != null ) {
                messageStrings.add(ChatColor.translateAlternateColorCodes('&',message));
            }
        } catch (FileNotFoundException ex) {
            LogManager.logger.warning("File not found while trying to add Action: " + ex.getMessage());
            messageStrings.add("[PwnFilter] Configuration error: file not found.");
        } catch (IOException ex) {
            LogManager.logger.warning("Error reading file: " + s);
            messageStrings.add("[PwnFilter] Error: respondfile IO.  Please notify admins.");
        }
    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {
        final ArrayList<String> preparedMessages = new ArrayList<String>();

        for (String message : messageStrings) {
            preparedMessages.add(Patterns.replaceVars(message,state));
        }

        state.addLogMessage("Responded to " + state.playerName + " with: "+preparedMessages.get(0) + "...");

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                for (String m : preparedMessages) {
                    state.getPlayer().sendMessage(m);
                }
            }
        };
        task.runTask(state.plugin);

        return true;
    }
}

