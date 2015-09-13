/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterTask;

/**
 * Actions are triggered by Rules when they match.
 *
 * @author ptoal
 * @version $Id: $Id
 */
public interface Action {

    /**
     * <p>init.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    void init(String s);
    /**
     * <p>execute.</p>
     *
     * @param state a {@link FilterTask} object.
     */
    void execute(FilterTask state);
}
