package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;

/**
 * Actions are triggered by Rules when they match.
 */

public interface Action {

    void init(String s);
    boolean execute(FilterState state );
}
