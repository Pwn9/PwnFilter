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

import java.util.regex.Pattern;

/**
 * Enhanced String-like objects, that provide functions for manipulating the
 * text content, while abstracting any embedded codes (eg: Bukkit Color codes)
 *
 * The important aspect of this type of object is that the filter can perform
 * matches and replacements on the text content, independent of any embedded
 * encoding.
 *
 * Created by ptoal on 15-09-09.
 */
public interface EnhancedString extends CharSequence {

    /**
     * EnhancedString Implementations must provide a way of replacing Text, since
     * standard Regex might not preserve the special coding.
     *
     * @param p Regex {@link Pattern} to match
     * @param rText Replacement text
     * @return A new EnhancedString object with the changed text.
     */
    EnhancedString replaceText(Pattern p, String rText);


    /**
     * Convert a section of text to lowercase.
     * @param p Regex {@link Pattern} to match
     * @return A new EnhancedString object with the changed text.
     */
    EnhancedString patternToLower(Pattern p);

    /**
     * Convert a section of text to uppercase.
     * @param p Regex {@link Pattern} to match
     * @return A new EnhancedString object with the changed text.
     */
    EnhancedString patternToUpper(Pattern p);

    /**
     *
     * @return String representing the raw message, including any special codes.
     */
    String getRaw();

}
