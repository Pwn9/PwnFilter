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

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.bukkit.BukkitPlayer;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.util.DefaultMessages;
import com.pwn9.PwnFilter.util.tags.TagRegistry;

/**
 * Warn the user with the string provided.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Warn implements Action {
    // Message to apply to this warn action
    String messageString;

    /** {@inheritDoc} */
    public void init(String s)
    {
        messageString = DefaultMessages.prepareMessage(s, "warnmsg");
    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {
        if ( state.getAuthor() instanceof BukkitPlayer) {

            final String message = TagRegistry.replaceTags(messageString, state);
            state.getAuthor().sendMessage(messageString);
            state.addLogMessage("Warned " + state.getAuthor().getName() + ": " + message);
            return true;
        }
        return false;
    }
}

