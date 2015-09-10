/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterTask;

import java.util.Set;

/**
 * Objects that can be attached to ruleChains (eg: rules, and other ruleChains)
 * User: ptoal
 * Date: 13-09-24
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author ptoal
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
     * <p>isValid.</p>
     *
     * @return a boolean.
     */
    boolean isValid();

    /**
     * <p>apply.</p>
     *
     * @param state a {@link FilterTask} object.
     */
    void apply(FilterTask state);

    /**
     * <p>getPermissionList.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    Set<String> getPermissionList();

}
