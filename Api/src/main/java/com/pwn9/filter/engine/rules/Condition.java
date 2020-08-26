package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.FilterContext;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/08/2020.
 */
public interface Condition {
    boolean check(FilterContext state);
    String getParameters();
    CondType getType();
    CondFlag getFlag();

    enum CondFlag {
        NONE, ignore, require
    }

    enum CondType {
        permission, user, string, command,
    }
}
