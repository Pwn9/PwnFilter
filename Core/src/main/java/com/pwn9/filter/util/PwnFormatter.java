/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.util;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * <p>PwnFormatter class.</p>
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class PwnFormatter extends SimpleFormatter {

    @Override
    public synchronized String format(LogRecord record) {
        String dateStr;
        Format formatter;
        Date date = new Date(record.getMillis());
        formatter = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");
        dateStr = formatter.format(date);
        return dateStr + " " + record.getMessage() + "\n";
    }

    public static String legacyTextConverter(String message){
        return LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }
}
