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
import com.pwn9.PwnFilter.util.ColoredString;

/**
 * Decolor the whole string and replace the matched text with the replacement string.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionreplace implements Action {
    // messageString is what we will use to replace any matched text.
    String messageString;

    public void init(String s)
    {
        messageString = s.replaceAll("&([0-9a-fk-or])", "\u00A7$1").replaceAll("\"","");
    }

    public boolean execute(final FilterState state ) {
        ColoredString cs = state.message;
        cs.decolor();
        cs.replaceText(state.pattern, messageString);
        return true;
    }
}
