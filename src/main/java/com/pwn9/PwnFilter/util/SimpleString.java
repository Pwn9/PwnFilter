/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Simple wrapper around a String object.
 * <p/>
 * Created by ptoal on 15-09-09.
 */
public class SimpleString implements EnhancedString {

    final private String value;

    public SimpleString(String s) {
        value = s;
    }

    @Override
    public SimpleString replaceText(Pattern p, String rText) {
        Matcher m = p.matcher(value);
        return new SimpleString(m.replaceAll(rText));
    }

    @Override
    public SimpleString patternToLower(Pattern p) {
        Matcher m = p.matcher(value);

        StringBuffer buf = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(buf, m.group().toLowerCase());
        }
        m.appendTail(buf);
        return new SimpleString(buf.toString());
    }


    @Override
    public SimpleString patternToUpper(Pattern p) {
        Matcher m = p.matcher(value);

        StringBuffer buf = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(buf, m.group().toUpperCase());
        }
        m.appendTail(buf);
        return new SimpleString(buf.toString());
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleString) {
            return ((SimpleString) obj).getRaw().equals(getRaw());
        } else {
            return getRaw().equals(obj);
        }
    }

    @Override
    public String getRaw() {
        return value;
    }

    @NotNull
    @Override
    public String toString() {
        return value;
    }
}


