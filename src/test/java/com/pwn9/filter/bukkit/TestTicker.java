/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit;

import com.google.common.base.Ticker;

/**
 * Created by Sage905 on 2016-04-10.
 */
public class TestTicker extends Ticker {

    private long elapsedTime = 0;

    public void setElapsed(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @Override
    public long read() {
        return elapsedTime;
    }
}

