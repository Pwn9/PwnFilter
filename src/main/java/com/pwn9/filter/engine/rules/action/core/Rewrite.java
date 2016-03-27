/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.core;

import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.Action;
import org.bukkit.ChatColor;

/**
 * Rewrite the string by replacing the matched text with the provided string.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class Rewrite implements Action {

    // messageString is what we will use to replace any matched text.
    private final String messageString;

    private Rewrite(String message) {
        messageString = message;
    }

    static Action getAction(String s)
    {
        return new Rewrite(ChatColor.translateAlternateColorCodes('&',s));
    }

    @Override
    public void execute(final FilterContext filterTask) {
        filterTask.setModifiedMessage(filterTask.getModifiedMessage().
                replaceText(filterTask.getPattern(), messageString));

    }
}
