package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.util.ColoredString;

/**
 * Convert the matched text to lowercase.
 */
public class Actionlower implements Action {

    public void init(String s)
    {
        // Do nothing with a string, if one is provided.
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        ColoredString cs = state.message;
        return cs.patternToLower(state.pattern);

    }
}
