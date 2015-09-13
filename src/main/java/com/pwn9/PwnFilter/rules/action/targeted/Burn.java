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
import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.minecraft.api.MinecraftPlayer;
import com.pwn9.PwnFilter.minecraft.util.DefaultMessages;
import com.pwn9.PwnFilter.rules.action.Action;

/**
 * Burns a player to death.
 * NOTE: This method needs to use runTask to operate on the player, as the bukkit API
 * calls are NOT thread-safe.
 * TODO: Consider hooking this into the custom death message handler.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Burn implements Action {
    // Message to apply to this burn action
    String messageString;

    /** {@inheritDoc} */
    public void init(String s)
    {
        messageString = DefaultMessages.prepareMessage(s, "burnmsg");
    }

    /** {@inheritDoc} */
    public void execute(final FilterTask filterTask) {
        MessageAuthor target = filterTask.getAuthor();

        if (target instanceof MinecraftPlayer) {
            if (((MinecraftPlayer)target).burn(5000, messageString)) {
                filterTask.addLogMessage("Burned " + target.getName() + ": " + messageString);
            }
        } else {
            filterTask.addLogMessage("Target not flamable: " + target.getName());
        }

    }
}
