package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;

/**
 * Log this event.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionlog implements Action {

    public void init(String s)
    {
        // Do nothing with a string, if one is provided.
    }

    public boolean execute(final FilterState state ) {
        state.log = true;
        return true;
    }
}
