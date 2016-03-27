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

import com.pwn9.filter.engine.api.FilterContext;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Objects that can be attached to ruleChains (eg: rules, and other ruleChains)
 * User: Sage905
 * Date: 13-09-24
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public interface ChainEntry {

    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String toString();

    /**
     * <p>apply.</p>
     *
     * @param state a {@link FilterContext} object.
     */
    void apply(FilterContext state, Chain parent, Logger logger);

    /**
     * Find all conditions in all RuleChain entries that match the passed
     * string.  Eg: if matchString = "permission", and a rule has this
     * condition: "ignore|require permission foo|bar|baz", return:
     * {"foo","bar","baz"}
     * @param matchString Condition to match
     * @return Set of unique String objects in all conditions of this chain.
     */
    Set<? extends String> getConditionsMatching(String matchString);
}
