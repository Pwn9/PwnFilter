/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.config.FilterConfig;
import com.pwn9.PwnFilter.util.LogManager;
import com.pwn9.PwnFilter.util.Patterns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage all the shortcut mappings
 * User: ptoal
 * Date: 13-10-11
 * Time: 11:10 PM
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class ShortCutManager {
    // TODO: Create a "ShortCuts" object to pass around, instead of HashMaps.
    private static ShortCutManager _instance;
    private static Map<String, Map<String,String>> shortcutFiles = new HashMap<String, Map<String, String>>();

    private ShortCutManager() {}

    /**
     * <p>getInstance.</p>
     *
     * @return a {@link com.pwn9.PwnFilter.rules.ShortCutManager} object.
     */
    public static ShortCutManager getInstance() {
        if (_instance == null) {
            _instance = new ShortCutManager();
        }
        return _instance;
    }

    /**
     * <p>replace.</p>
     *
     * @param shortcuts a {@link java.util.Map} object.
     * @param lineData a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String replace(Map<String,String> shortcuts, String lineData) {
        // If we don't have a shortcuts file to process, just return what we were given.
        if (shortcuts == null) return lineData;

        Pattern shortcutMatch = Patterns.compilePattern("<[a-zA-Z_]{0,3}>");
        Matcher matcher = shortcutMatch.matcher(lineData);
        StringBuffer newLineData = new StringBuffer();
        while (matcher.find()) {
            String thisMatch = matcher.group();
            String var = thisMatch.substring(1,thisMatch.length()-1);
            String replacement = shortcuts.get(var.toLowerCase());
            if (replacement == null || replacement.isEmpty()) {
                LogManager.logger.warning("Could not find shortcut: <"+var+">" +
                        "when parsing: '"+lineData+"'");
                matcher.appendReplacement(newLineData,"");
            } else {
                matcher.appendReplacement(newLineData, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(newLineData);
        if (!newLineData.toString().equals(lineData)) {
            LogManager.getInstance().debugHigh("Original regex: " + lineData + "\n New regex: " + newLineData);
        }
        return newLineData.toString();

    }

    /**
     * <p>getShortcutMap.</p>
     *
     * @param mapFileName a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getShortcutMap(String mapFileName) {
        Map<String, String> returnValue = shortcutFiles.get(mapFileName);

        if (returnValue != null) {
            return returnValue;
        } else {
            loadFile(mapFileName);
        }
        return shortcutFiles.get(mapFileName);
    }

    /**
     * <p>reloadFiles.</p>
     */
    public void reloadFiles() {
        // Just wipe out the old.  They will be reloaded on first access.
        shortcutFiles.clear();
    }

    /**
     * <p>loadFile.</p>
     *
     * @param fileName a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean loadFile(String fileName) {

        Map<String,String> varset = new HashMap<String, String>();

        File shortcutFile = getFile(fileName);

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(shortcutFile));

            String line;
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {
                lineNo++;
                line = line.trim();

                String[] parts = line.split(" ",2);

                // Line must have shortcut/replacement.  shortcut must be < 3 characters long
                if (parts.length < 2 || parts[0].length() > 3) {
                    LogManager.logger.info("Syntax error in " + fileName + " line: " + lineNo);
                    continue;
                }
                varset.put(parts[0].toLowerCase(),parts[1]);

            }

        } catch (Exception e) {
            return false;
        }

        shortcutFiles.put(fileName,varset);
        return true;
    }

    /**
     * <p>getFile.</p>
     *
     * @param fileName a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     */
    public File getFile(String fileName) {
        File shortcutdir = FilterConfig.getInstance().getRulesDir();
        if (shortcutdir.exists()) {
            File shortcutFile = new File(shortcutdir,fileName);
            if (shortcutFile.exists()) {
                return shortcutFile;
            } else {
                return null;
            }
        }
        LogManager.logger.warning("Unable to find shortcut definition file:" + fileName);
        return null;
    }
}

