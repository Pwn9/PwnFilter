package com.pwn9.PwnFilter.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

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
}
