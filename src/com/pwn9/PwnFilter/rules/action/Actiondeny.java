package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;

/**
 * Deny this event by cancelling it.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actiondeny implements Action {

    public void init(String s)
    {
        // Do nothing with a string, if one is provided.
    }

    public boolean execute(final FilterState state ) {
        state.cancel = true;
        return true;
    }
}
