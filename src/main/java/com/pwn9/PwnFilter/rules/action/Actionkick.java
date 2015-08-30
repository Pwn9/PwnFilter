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
import com.pwn9.PwnFilter.util.DefaultMessages;
import org.bukkit.scheduler.BukkitRunnable;

import static com.pwn9.PwnFilter.util.Patterns.replaceVars;

/**
 * Kick the user with a customized message.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionkick implements Action {
    // Message to apply to this kick action
    String messageString;

    /** {@inheritDoc} */
    public void init(String s)
    {
        messageString = DefaultMessages.prepareMessage(s, "burnmsg");
    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {

        if (state.getPlayer() == null ) return false;
        String outMessage = replaceVars(messageString,state);

        state.addLogMessage("Kicked " + state.playerName + ": " + messageString);
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                state.getPlayer().kickPlayer(messageString);
            }
        };
        task.runTask(state.plugin);
        return true;
    }
}
