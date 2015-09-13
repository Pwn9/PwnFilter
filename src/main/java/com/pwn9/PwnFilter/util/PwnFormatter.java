/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * <p>PwnFormatter class.</p>
 *
 * @author ptoal
 * @version $Id: $Id
 */
class PwnFormatter extends SimpleFormatter {

    /** {@inheritDoc} */
    @Override
    public synchronized String format(LogRecord record) {

        String dateStr;
        Format formatter;
        Date date = new Date(record.getMillis());
        formatter = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");
        dateStr = formatter.format(date);
        return dateStr + " " + record.getMessage() + "\n";
    }
}
