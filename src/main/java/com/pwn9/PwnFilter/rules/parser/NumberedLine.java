/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.parser;

/**
 * <p>NumberedLine class.</p>
 *
 * @author ptoal
 * @version $Id: $Id
 */
class NumberedLine {
    public final Integer number;
    public final String string;

    /**
     * <p>Constructor for NumberedLine.</p>
     *
     * @param number a {@link java.lang.Integer} object.
     * @param string a {@link java.lang.String} object.
     */
    public NumberedLine(Integer number, String string) {
        this.number = number;
        this.string = string;
    }
}
