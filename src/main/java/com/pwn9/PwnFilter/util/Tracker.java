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

import org.mcstats.Metrics;

/**
 * Plugin Metrics Tracker class (courtesy of mbaxter)
 */

public class Tracker extends Metrics.Plotter {

    private final String name;
    private int value, last;

    public Tracker(String name) {
        this.name = name;
        this.value = 0;
        this.last = 0;
    }

    @Override
    public String getColumnName() {
        return this.name;
    }

    @Override
    public int getValue() {
        this.last = this.value;
        return this.value;
    }

    public void increment() {
        this.value++;
    }

    @Override
    public void reset() {
        this.value = this.value - this.last;
    }

}

