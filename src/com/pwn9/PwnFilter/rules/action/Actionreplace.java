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
