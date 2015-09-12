/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.minecraft.util;

import org.mcstats.Metrics;

/**
 * Plugin Metrics Tracker class (courtesy of mbaxter)
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class Tracker extends Metrics.Plotter {

    private final String name;
    private int value, last;

    /**
     * <p>Constructor for Tracker.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public Tracker(String name) {
        this.name = name;
        this.value = 0;
        this.last = 0;
    }

    /** {@inheritDoc} */
    @Override
    public String getColumnName() {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public int getValue() {
        this.last = this.value;
        return this.value;
    }

    /**
     * <p>increment.</p>
     */
    public void increment() {
        this.value++;
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        this.value = this.value - this.last;
    }

}

