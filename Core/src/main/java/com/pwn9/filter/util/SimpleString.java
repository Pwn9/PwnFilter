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

import com.pwn9.filter.engine.api.EnhancedString;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Simple wrapper around a String object.
 * <p>
 * Created by Sage905 on 15-09-09.
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


