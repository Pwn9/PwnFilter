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
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.Condition;

import java.util.Set;

/**
 * This is just an empty chain object, to be used instead of a null value.
 *
 * Created by Sage905 on 2016-03-20.
 */
public enum EmptyChain implements Chain, ChainEntry {

    INSTANCE {

        private final Multimap<String, Action> actionGroups
                = ImmutableListMultimap.of();

        private final Multimap<String, Condition> conditionGroups
                = ImmutableListMultimap.of();


        @Override
        public String getConfigName() {
            return "EMPTY";
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
        public void apply(FilterContext state, FilterService filterService) {
            // Nothing to do.
        }

        @Override
        public Set<? extends String> getConditionsMatching(String matchString) {
            // Never going to match anything.
            return null;
        }
    }
}
