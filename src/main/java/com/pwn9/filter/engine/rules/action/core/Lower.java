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
import com.pwn9.filter.engine.api.EnhancedString;

/**
 * Convert the matched text to lowercase.
 *
 * Lower is a singleton, because it does not have any parameters and always
 * performs the same action on a FilterContext.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public enum Lower implements Action {

    INSTANCE;

    /** {@inheritDoc} */
    public void execute(final FilterContext filterTask) {
        EnhancedString cs = filterTask.getModifiedMessage();
        filterTask.addLogMessage("Converting to lowercase.");
        filterTask.setModifiedMessage(cs.patternToLower(filterTask.getPattern()));
    }
}
