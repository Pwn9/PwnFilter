package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;

/**
 * This action stops processing of any more rules.
 */
public class Actionabort implements Action {

    public void init(String s)
    {
        // Do nothing with a string, if one is provided.
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        state.stop = true;
        return true;
    }
}
