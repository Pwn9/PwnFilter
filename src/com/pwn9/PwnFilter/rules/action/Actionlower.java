package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.ColoredString;

/**
 * Convert the matched text to lowercase.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionlower implements Action {

    public void init(String s)
    {
        // Do nothing with a string, if one is provided.
    }

    public boolean execute(final FilterState state ) {
        ColoredString cs = state.message;
        return cs.patternToLower(state.pattern);

    }
}
