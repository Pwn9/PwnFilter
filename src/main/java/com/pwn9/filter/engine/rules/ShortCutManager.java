/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules;

import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage all the shortcut mappings
 * User: Sage905
 * Date: 13-10-11
 * Time: 11:10 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class ShortCutManager {
    private static ShortCutManager _instance;
    private static final Map<File, Map<String, String>> shortcutFiles = new HashMap<>();

    private ShortCutManager() {
    }

    public static ShortCutManager getInstance() {
        if (_instance == null) {
            _instance = new ShortCutManager();
        }
        return _instance;
    }

    public static String replace(Logger logger, Map<String, String> shortcuts, String lineData) {
        // If we don't have a shortcuts file to process, just return what we were given.
        if (shortcuts == null) return lineData;

        Pattern shortcutMatch = Pattern.compile("<[a-zA-Z_]{0,3}>",Pattern.CASE_INSENSITIVE);
        Matcher matcher = shortcutMatch.matcher(lineData);
        StringBuffer newLineData = new StringBuffer();
        while (matcher.find()) {
            String thisMatch = matcher.group();
            String var = thisMatch.substring(1, thisMatch.length() - 1);
            String replacement = shortcuts.get(var.toLowerCase());
            if (replacement == null || replacement.isEmpty()) {
                logger.warning("Could not find shortcut: <" + var + ">" +
                        "when parsing: '" + lineData + "'");
                matcher.appendReplacement(newLineData, "");
            } else {
                matcher.appendReplacement(newLineData, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(newLineData);
        if (!newLineData.toString().equals(lineData)) {
            logger.finer(() -> "Original regex: " + lineData + "\n New regex: " + newLineData);
        }
        return newLineData.toString();

    }

    /**
     * <p>getShortcutMap.</p>
     *
     * @param shortcutFile a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getShortcutMap(File shortcutFile ) throws IOException, ParseException {
        File absoluteFile = shortcutFile.getAbsoluteFile();
        Map<String, String> returnValue = shortcutFiles.get(absoluteFile);

        if (returnValue != null) {
            return returnValue;
        } else {
            loadFile(absoluteFile);
        }
        return shortcutFiles.get(absoluteFile);
    }

    public void reloadFiles() {
        // Just wipe out the old.  They will be reloaded on first access.
        shortcutFiles.clear();
    }

    private void loadFile(File shortcutFile) throws IOException, ParseException {

        Map<String, String> varset = new HashMap<>();

        BufferedReader reader;

        reader = new BufferedReader(new InputStreamReader(new FileInputStream(shortcutFile)));

        String line;
        int lineNo = 0;

        while ((line = reader.readLine()) != null) {
            lineNo++;
            line = line.trim();

            String[] parts = line.split(" ", 2);

            // Line must have shortcut/replacement.  shortcut must be < 3 characters long
            if (parts.length < 2 || parts[0].length() > 3) {
                throw new ParseException("Syntax error in " + shortcutFile.getPath() + " line: ", lineNo);
            }
            varset.put(parts[0].toLowerCase(), parts[1]);

        }
        shortcutFiles.put(shortcutFile, varset);
    }

}
