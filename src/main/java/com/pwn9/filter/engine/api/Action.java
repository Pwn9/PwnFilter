/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.api;

import com.pwn9.filter.engine.FilterService;

/**
 * Actions are compiled into RuleChains for execution when matched by a rule.
 *
 * Actions must be immutable, and only created by the newAction() method.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public interface Action {

    /**
     * <p>Execute this action on a FilterContext</p>
     *
     * @param task a {@link FilterContext} object.
     * @param filterService
     */
    void execute(FilterContext task, FilterService filterService);
}
