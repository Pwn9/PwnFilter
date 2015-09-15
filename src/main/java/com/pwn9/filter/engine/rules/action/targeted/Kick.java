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

import com.pwn9.filter.engine.api.FilterTask;
import com.pwn9.filter.minecraft.api.MinecraftPlayer;
import com.pwn9.filter.minecraft.util.DefaultMessages;
import com.pwn9.filter.engine.api.Action;

import static com.pwn9.filter.util.tags.TagRegistry.replaceTags;

/**
 * Kick the user with a customized message.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Kick implements Action {
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

            String outMessage = replaceTags(messageString, filterTask);
            ((MinecraftPlayer) filterTask.getAuthor()).kick(outMessage);
            filterTask.addLogMessage("Kicked " + filterTask.getAuthor().getName() + ": " + messageString);
        }
    }
}
