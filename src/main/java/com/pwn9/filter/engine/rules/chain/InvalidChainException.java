/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.chain;

/**
 * An Invalid Chain is generated from invalid configuration files, and other
 * errors.
 * <p/>
 * Created by Sage905 on 15-10-10.
 */
public class InvalidChainException extends Exception {

    public InvalidChainException(String message) {
        super(message);
    }
}
