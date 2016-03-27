/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.config;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * Object to hold the configuration of the PwnFilter Engine
 *
 * Created by Sage905 on 15-09-10.
 */

public class FilterConfig {

    private volatile File textDir;
    private volatile File rulesDir;

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

    private Level ruleLogLevel;
    // Logging variables

    private File logFile;

    private FileHandler logfileHandler;

    public void setRuleLogLevel(String level) {
        try {
            ruleLogLevel = Level.parse(level.toUpperCase());
        } catch (IllegalArgumentException e ) {
            ruleLogLevel = Level.INFO;
        }
    }

    public Level getRuleLogLevel() {
        return ruleLogLevel;
    }

}
