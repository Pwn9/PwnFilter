/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.core;

import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.Action;

/**
 * Deny this event from propagating by cancelling it (i.e.: throw the message
 * in the trash)
 *
 * Deny is a singleton, because it does not have any parameters and always
 * performs the same action on a FilterContext ( calls setCancelled())
 *
 * @author Sage905
 * @version $Id: $Id
 */
public enum Deny implements Action {

    INSTANCE;

    @Override
    public void execute(final FilterContext filterTask) {
        filterTask.setCancelled();
    }
}
