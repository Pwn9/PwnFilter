package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;

/**
 * This action stops processing of any more rules.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionabort implements Action {

    public void init(String s)
    {
        // Do nothing with a string, if one is provided.
    }

    public boolean execute(final FilterState state ) {
        state.stop = true;
        return true;
    }
}
