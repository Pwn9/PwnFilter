
/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.api;

import com.pwn9.PwnFilter.rules.RuleChain;

/**
 * Listeners that can call PwnFilter
 * User: ptoal
 * Date: 13-09-28
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author ptoal
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
     *
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     *
     */
    void activate();

    /**
     * Shutdown this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the activate / shutdown methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     *
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    void shutdown();

}
