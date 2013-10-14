package com.pwn9.PwnFilter.util;

import com.pwn9.PwnFilter.FilterState;

import java.util.logging.Logger;
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
    static Logger logger = Logger.getLogger("Minecraft.PwnFilter");

    public static java.util.regex.Pattern compilePattern(String re) {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
            logger.fine("Successfully compiled regex: " + re);
            return pattern;
        }
        catch (PatternSyntaxException e) {
            logger.warning("Failed to compile regex: " + re);
            logger.warning(e.getMessage());
        }
        catch (Exception e) {
            logger.severe("Unexpected error while compiling expression '" + re + "'");
            e.printStackTrace();
        }
        return pattern;
    }

    public static String replaceVars(String line, FilterState state) {
        line = line.replaceAll("&world", wrapReplacement(state.playerWorldName)).
                replaceAll("&player", wrapReplacement(state.playerName)).
                replaceAll("&string", wrapReplacement(state.message.getColoredString())).
                replaceAll("&rawstring", wrapReplacement(state.getOriginalMessage().getColoredString())).
                replaceAll("&event", wrapReplacement(state.getListenerName())).
                replaceAll("&ruleid", (state.rule != null)?wrapReplacement(state.rule.getId()):"-").
                replaceAll("&ruledescr", (state.rule !=null)?wrapReplacement(state.rule.getDescription()):"''");
        return line;
    }

    private static String wrapReplacement(String s) {
        return (s != null)?Matcher.quoteReplacement(s):"-";
    }
}
