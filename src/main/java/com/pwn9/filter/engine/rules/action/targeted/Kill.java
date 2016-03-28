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

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.minecraft.api.MinecraftPlayer;

/**
 * Kill a player with a customized Death Message
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Kill implements Action {
    // Message to apply to this kick action
    private final String messageString;
    // Default message to apply to this burn action
    private static String defaultMessage = "";


    private Kill(String messageString) {
        this.messageString = messageString;
    }

    /** {@inheritDoc} */
    public static Action getAction(String s)
    {
        return new Kill((s != null && !s.isEmpty() ? s : defaultMessage));
    }
    public static void setDefaultMessage(String s) {
        defaultMessage = s;
    }

    /** {@inheritDoc} */
    public void execute(final FilterContext filterTask) {

        if (filterTask.getAuthor() instanceof MinecraftPlayer) {

            ((MinecraftPlayer) filterTask.getAuthor()).kill(messageString);
            filterTask.addLogMessage("Killed by Filter: " + filterTask.getAuthor().getName() + " " + messageString);

        }
    }
}
