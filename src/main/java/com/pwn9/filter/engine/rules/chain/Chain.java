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

import com.google.common.collect.Multimap;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.rules.Condition;

/**
 * Interface for a RuleChain
 * User: Sage905
 * Date: 13-11-16
 * Time: 7:17 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */
public interface Chain {

    /**
     * <p>getConfigName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getConfigName();

    /**
     * <p>getActionGroups.</p>
     *
     * @return a {@link com.google.common.collect.Multimap} object.
     */
    Multimap<String, Action> getActionGroups();

    /**
     * <p>getConditionGroups.</p>
     *
     * @return a {@link com.google.common.collect.Multimap} object.
     */
    Multimap<String, Condition> getConditionGroups();

}
