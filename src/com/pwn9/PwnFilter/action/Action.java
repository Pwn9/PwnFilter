package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;

/**
 * Actions are triggered by Rules when they match.
 */

public interface Action {

    void init(String s);
    boolean execute(PwnFilter plugin, FilterState state );
}
