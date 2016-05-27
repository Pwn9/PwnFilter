/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.engine.rules.chain;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.FilterContext;

import java.util.Set;

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

    String toString();

    void apply(FilterContext state, FilterService filterService);

    /**
     * Find all conditions in all RuleChain entries that match the passed
     * string.  Eg: if matchString = "permission", and a rule has this
     * condition: "ignore|require permission foo|bar|baz", return:
     * {"foo","bar","baz"}
     *
     * @param matchString Condition to match
     * @return Set of unique String objects in all conditions of this chain.
     */
    Set<? extends String> getConditionsMatching(String matchString);
}
