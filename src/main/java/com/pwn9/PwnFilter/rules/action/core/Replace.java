/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action.core;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.rules.action.Action;
import org.bukkit.ChatColor;

/**
 * Decolor the whole string and replace the matched text with the replacement string.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Replace implements Action {
    // messageString is what we will use to replace any matched text.
    String messageString = "";


    /** {@inheritDoc} */
    public void init(String s)
    {
        messageString = ChatColor.translateAlternateColorCodes('&',s).replaceAll("\"","");
    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {
        state.setModifiedMessage(state.getModifiedMessage().decolor().replaceText(state.pattern, messageString));

        if (state.rule.modifyRaw())
            state.setUnfilteredMessage(state.getUnfilteredMessage().replaceText(state.pattern,messageString));

        return true;
    }
}
