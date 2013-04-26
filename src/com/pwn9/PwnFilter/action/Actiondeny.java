package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;

/**
 * Deny this event by cancelling it.
 */

public class Actiondeny implements Action {

    public void init(String s)
    {
        // Do nothing with a string, if one is provided.
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        state.cancel = true;
        return true;
    }
}
