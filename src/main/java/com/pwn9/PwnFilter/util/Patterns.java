/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.util;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Some helpful utility methods.
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class Patterns {

    /**
     * <p>compilePattern.</p>
     *
     * @param re a {@link java.lang.String} object.
     * @return a java$util$regex$Pattern object.
     */
    public static java.util.regex.Pattern compilePattern(String re) {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
            LogManager.getInstance().debugMedium("Successfully compiled regex: " + re);
            return pattern;
        }
        catch (PatternSyntaxException e) {
            LogManager.logger.warning("Failed to compile regex: " + re);
            LogManager.logger.warning(e.getMessage());
        }
        catch (Exception e) {
            LogManager.logger.severe("Unexpected error while compiling expression '" + re + "'");
            e.printStackTrace();
        }
        return pattern;
    }

}
