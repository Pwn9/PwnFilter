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
 * Log this event.
 *
 * For now, the Logging is a simple switch, but in future, there may be an
 * option to specify where to log and/or at what level.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Log implements Action {

    private Log() {
    }

    public static Action getAction(String s)
    {
        return new Log();
    }

    @Override
    public void execute(final FilterContext filterTask) {
        filterTask.setLogging();
    }
}
