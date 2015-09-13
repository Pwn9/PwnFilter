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
import com.pwn9.PwnFilter.minecraft.api.MinecraftPlayer;
import com.pwn9.PwnFilter.minecraft.util.DefaultMessages;
import com.pwn9.PwnFilter.rules.action.Action;

/**
 * Kill a player with a customized Death Message
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Kill implements Action {
    // Message to apply to this kick action
    String messageString;

    /** {@inheritDoc} */
    public void init(String s)
    {
        messageString = DefaultMessages.prepareMessage(s, "burnmsg");
    }

    /** {@inheritDoc} */
    public void execute(final FilterTask filterTask) {

        if (filterTask.getAuthor() instanceof MinecraftPlayer) {

            ((MinecraftPlayer) filterTask.getAuthor()).kill(messageString);
            filterTask.addLogMessage("Killed by Filter: " + filterTask.getAuthor().getName() + " " + messageString);

        }
    }
}
