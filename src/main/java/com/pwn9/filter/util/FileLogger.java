/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.util;

import java.util.logging.Logger;

/**
 * Unified Logging interface for PwnFilter-related messages
 * User: Sage905
 * Date: 13-10-02
 * Time: 4:50 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class FileLogger extends Logger {

    protected FileLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }
}
