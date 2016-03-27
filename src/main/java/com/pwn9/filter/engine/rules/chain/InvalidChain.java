/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.chain;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.Condition;

import java.util.Set;
import java.util.logging.Logger;

/**
 * An Invalid Chain is generated from invalid configuration files, and other
 * errors.
 * <p/>
 * Created by Sage905 on 15-10-10.
 */
public class InvalidChain implements Chain, ChainEntry {

    private final String errorMessage;
    private final Multimap<String, Action> actionGroups
            = ImmutableListMultimap.of();

    private final Multimap<String, Condition> conditionGroups
            = ImmutableListMultimap.of();

    public InvalidChain(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getConfigName() {
        return "INVALID";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Multimap<String, Action> getActionGroups() {
        return actionGroups;
    }

    @Override
    public Multimap<String, Condition> getConditionGroups() {
        return conditionGroups;
    }

    @Override
    public void apply(FilterContext state, Chain parent, Logger logger) {
        throw new UnsupportedOperationException("Can not apply an Invalid Chain");
    }

    @Override
    public Set<? extends String> getConditionsMatching(String matchString) {
        return null;
    }
}
