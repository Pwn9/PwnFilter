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

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;

/**
 * This Internal action stops processing of any more rules.
 * Abort is a singleton, because it does not have any parameters and always
 * performs the same action on a FilterContext ( calls setAborted())
 *
 * @author Sage905
 * @version $Id: $Id
 */
public enum Abort implements Action {

    INSTANCE;

    public void execute(final FilterContext filterTask) {
        filterTask.setAborted();
        filterTask.addLogMessage("<Abort> Not processing more rules.");
    }
}


