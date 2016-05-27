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

package com.pwn9.filter.bukkit;

import org.mcstats.Metrics;

class Tracker extends Metrics.Plotter {

    private final String name;
    private int value, last;

    /**
     * <p>Constructor for Tracker.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    Tracker(String name) {
        this.name = name;
        this.value = 0;
        this.last = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getValue() {
        this.last = this.value;
        return this.value;
    }

    /**
     * <p>increment.</p>
     */
    void increment() {
        this.value++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        this.value = this.value - this.last;
    }

}

