package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.util.LogManager;
import com.pwn9.PwnFilter.util.Patterns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage all the shortcut mappings
 * User: ptoal
 * Date: 13-10-11
 * Time: 11:10 PM
 */
public class ShortCutManager {

    private static ShortCutManager _instance;
    private static ConcurrentHashMap<String, HashMap<String,String>> shortcutFiles = new ConcurrentHashMap<String, HashMap<String, String>>();
    private static File shortcutDir;

    private ShortCutManager() {}

    public static ShortCutManager getInstance() {
        if (_instance == null) {
            _instance = new ShortCutManager();
        }
        return _instance;
    }

    public boolean setShortcutDir(File dir) {
        if (dir.exists()) {
            shortcutDir = dir;
            return true;
        } else return false;
    }

    public static String replace(HashMap<String,String> shortcuts, String lineData) {
        Pattern shortcutMatch = Patterns.compilePattern("<[a-zA-Z_]{0,3}>");
        Matcher matcher = shortcutMatch.matcher(lineData);
        StringBuffer newLineData = new StringBuffer();
        while (matcher.find()) {
            String thisMatch = matcher.group();
            String var = thisMatch.substring(1,thisMatch.length()-1);
            String replacement = shortcuts.get(var);
            if (replacement == null) {
                LogManager.logger.warning("Could not find shortcut: <"+var+">" +
                        "when parsing: '"+lineData+"'");
                matcher.appendReplacement(newLineData,"");
            } else {
                matcher.appendReplacement(newLineData, replacement);
            }
        }
        matcher.appendTail(newLineData);
        if (!newLineData.toString().equals(lineData)) {
            LogManager.getInstance().debugHigh("Original regex: " + lineData + "\n New regex: " + newLineData);
        }
        return newLineData.toString();

    }

    public HashMap<String, String> getShortcutMap(String mapFileName) {
        HashMap<String, String> returnValue = shortcutFiles.get(mapFileName);

        if (returnValue != null) {
            return returnValue;
        } else {
            loadFile(mapFileName);
        }
        return shortcutFiles.get(mapFileName);
    }

    public void reloadFiles() {
        // Just wipe out the old.  They will be reloaded on first access.
        shortcutFiles = new ConcurrentHashMap<String, HashMap<String, String>>();
    }

    public boolean loadFile(String fileName) {

        HashMap<String,String> varset = new HashMap<String, String>();

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
                varset.put(parts[0],parts[1]);

            }

        } catch (Exception e) {
            return false;
        }

        shortcutFiles.put(fileName,varset);
        return true;
    }

    public File getFile(String fileName) {
        if (shortcutDir.exists()) {
            File shortcutFile = new File(shortcutDir,fileName);
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

