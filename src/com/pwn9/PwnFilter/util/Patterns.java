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

import com.pwn9.PwnFilter.FilterState;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Some helpful utility methods.
 */
public class Patterns {
    /**
     * Class Utility Methods
     */
    static final DecimalFormat df = new DecimalFormat("0.00##");

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

    public static String replaceVars(String line, FilterState state) {
        Pattern p = Pattern.compile("(&player|&string|&rawstring|&event|&ruleid|&ruledescr)");
        Matcher m = p.matcher(line);


        if (m.matches()) {
            String group = m.group(1);
            String replace = "%" + group.substring(1) + "%";
            LogManager.logger.warning("The use of " + m.group(1) + " is deprecated.  Please update your configuration to use " + replace + ".");
            line = line.replaceAll("&world", wrapReplacement(state.playerWorldName)).
                    replaceAll("&player", wrapReplacement(state.playerName)).
                    replaceAll("&string", wrapReplacement(state.message.getColoredString())).
                    replaceAll("&rawstring", wrapReplacement(state.getOriginalMessage().getColoredString())).
                    replaceAll("&event", wrapReplacement(state.getListenerName())).
                    replaceAll("&ruleid", (state.rule != null)?wrapReplacement(state.rule.getId()):"-").
                    replaceAll("&ruledescr", (state.rule !=null)?wrapReplacement(state.rule.getDescription()):"''");
        }
        boolean pointsEnabled = PointManager.getInstance() != null;
        line = line.replaceAll("%world%", wrapReplacement(state.playerWorldName)).
                replaceAll("%player%", wrapReplacement(state.playerName)).
                replaceAll("%string%", wrapReplacement(state.message.getColoredString())).
                replaceAll("%rawstring%", wrapReplacement(state.getOriginalMessage().getColoredString())).
                replaceAll("%event%", wrapReplacement(state.getListenerName())).
                replaceAll("%points%",(pointsEnabled)?(df.format(PointManager.getInstance().getPlayerPoints(state.playerName))):"-").
                replaceAll("%ruleid%", (state.rule != null) ? wrapReplacement(state.rule.getId()) : "-").
                replaceAll("%ruledescr%", (state.rule != null) ? wrapReplacement(state.rule.getDescription()) : "''");
        return line;
    }

    private static String wrapReplacement(String s) {
        return (s != null)?Matcher.quoteReplacement(s):"-";
    }
}
