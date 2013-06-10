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

