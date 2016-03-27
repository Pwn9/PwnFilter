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

import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.minecraft.api.MinecraftPlayer;
import com.pwn9.filter.minecraft.util.DefaultMessages;
import com.pwn9.filter.engine.api.Action;

import static com.pwn9.filter.util.tag.TagRegistry.replaceTags;

/**
 * Kick the user with a customized message.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Kick implements Action {
    // Message to apply to this kick action
    private final String messageString;

    private Kick(String messageString) {
        this.messageString = messageString;
    }

    /** {@inheritDoc} */
    public static Action getAction(String s)
    {
        return new Kick(DefaultMessages.prepareMessage(s, "burnmsg"));
    }

    /** {@inheritDoc} */
    public void execute(final FilterContext filterTask) {

        if (filterTask.getAuthor() instanceof MinecraftPlayer) {

            String outMessage = replaceTags(messageString, filterTask);
            ((MinecraftPlayer) filterTask.getAuthor()).kick(outMessage);
            filterTask.addLogMessage("Kicked " + filterTask.getAuthor().getName() + ": " + messageString);
        }
    }
}
