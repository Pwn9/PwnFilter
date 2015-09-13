/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.config;

import java.io.File;

/**
 * Object to hold the configuration of the PwnFilter Engine
 *
 * Created by ptoal on 15-09-10.
 */

public class FilterConfig {

    private File textDir;
    private File rulesDir;

    private static final FilterConfig _instance = new FilterConfig();

    // Global Plugin switches
    public static boolean decolor = false;
    public static boolean globalMute = false;

    public static FilterConfig getInstance() {
        return _instance;
    }


    /* Getters and Setters */

    public File getTextDir() {
        return textDir;
    }

    public void setTextDir(File textDir) {
        this.textDir = textDir;
    }

    public File getRulesDir() {
        return rulesDir;
    }

    public void setRulesDir(File rulesDir) {
        this.rulesDir = rulesDir;
    }

}
