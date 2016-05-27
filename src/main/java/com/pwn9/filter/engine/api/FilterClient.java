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

package com.pwn9.filter.engine.api;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.rules.chain.RuleChain;

/**
 * Listeners that can call PwnFilter
 * User: Sage905
 * Date: 13-09-28
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public interface FilterClient {

    /**
     * A short name for this filter to be used in log messages and statistics.
     * eg: CHAT, COMMAND, ANVIL, etc.
     *
     * @return String containing the listeners short name.
     */
    String getShortName();


    FilterService getFilterService();

    /**
     * <p>getRuleChain.</p>
     *
     * @return The primary rulechain for this filter
     */
    RuleChain getRuleChain();

    /**
     * <p>isActive.</p>
     *
     * @return True if this FilterListener is currently active
     */
    boolean isActive();

    /**
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    void activate();

    /**
     * Shutdown this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the activate / shutdown methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    void shutdown();

}
