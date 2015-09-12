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

import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.rules.action.ActionFactory;
import com.pwn9.PwnFilter.util.LogManager;
import com.pwn9.PwnFilter.util.PointManager;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * A largely static object, which serves as an interface to the PwnFilter Bukkit
 * configuration.
 *
 * Created by ptoal on 15-09-10.
 */
public class BukkitConfig {

    // Global Plugin switches
    private static boolean globalMute = false;
    private static Configuration config;

    private static File dataFolder;

    public static void loadConfiguration(Configuration configuration, File folder) {

        dataFolder = folder;
        config = configuration;

        if (config.getBoolean("logfile")) {
            LogManager.getInstance().start();
        } else { // Needed during configuration reload to turn off logging if the option changes
            LogManager.getInstance().stop();
        }

        // Set the directory containing rules files.
        File ruleDir = setupDirectory(config.getString("ruledirectory", "rules"));
        if (ruleDir != null) {
            FilterConfig.getInstance().setRulesDir(ruleDir);
        } else {
            throw new RuntimeException(
                    "Unable to create or access rule directory.");
        }

        // Set the directory containing Text Files
        FilterConfig.getInstance().setTextDir(
                setupDirectory(config.getString("textdir", "textfiles"))
        );

        // Setup logging
        LogManager.setRuleLogLevel(config.getString("loglevel", "info"));
        LogManager.setDebugMode(config.getString("debug"));

        setupPoints();
    }


    private static void setupPoints() {
        ConfigurationSection pointsSection = config.getConfigurationSection("points");
        if (!pointsSection.getBoolean("enabled")) {
            if (PointManager.isEnabled()) {
                PointManager.getInstance().shutdown();
            }
        } else {
            if (!PointManager.isEnabled()) {
                PointManager.setup(
                        pointsSection.getDouble("leak.points",1),
                        pointsSection.getInt("leak.interval",30)
                );

                parseThresholds(pointsSection);
            }
        }
    }

    private static void parseThresholds(ConfigurationSection cs) {

        for (String threshold : cs.getKeys(false)) {
            List<Action> ascending = new ArrayList<Action>();
            List<Action> descending = new ArrayList<Action>();

            for (String action : cs.getStringList(threshold + ".actions.ascending")) {
                Action actionObject = ActionFactory.getActionFromString(action);
                if (actionObject != null) {
                    ascending.add(actionObject);
                } else {
                    LogManager.logger.warning("Unable to parse action in threshold: " + threshold);
                }
            }
            for (String action : cs.getStringList(threshold + ".actions.descending")) {
                Action actionObject = ActionFactory.getActionFromString(action);
                if (actionObject != null) {
                    descending.add(actionObject);
                } else {
                    LogManager.logger.warning("Unable to parse action in threshold: " + threshold);
                }
            }
            PointManager.getInstance().addThreshold(
                    cs.getString(threshold + ".name"),
                    cs.getDouble(threshold + ".points"),
                    ascending,
                    descending);
        }

    }



    /**
     * Ensure that the named directory exists and is accessible.  If the
     * directory begins with a / (slash), it is assumed to be an absolute
     * path.  Otherwise, the directory is assumed to be relative to the root
     * data folder.
     *
     * If the directory doesn't exist, an attempt is made to create it.
     *
     * @param directoryName relative or absolute path to the directory
     * @return {@link File} referencing the directory.
     */
    private static File setupDirectory(@NotNull String directoryName) {
        File dir;
        if (directoryName.startsWith("/")) {
            dir = new File(directoryName);
        } else {
            dir = new File(dataFolder,directoryName);
        }
        try {
            if (!dir.exists()) {
                if (dir.mkdirs())
                    LogManager.logger.info("Created directory: " + dir.getAbsolutePath());
            }
            return dir;
        } catch (Exception ex) {
            LogManager.logger.warning("Unable to access/create directory: " + dir.getAbsolutePath());
            return null;
        }

    }

    /* Accessors */
    public File getDataFolder() {
        return dataFolder;
    }

    public static boolean decolor() {
        return config.getBoolean("decolor");
    }

    public static boolean isGlobalMute() {
        return globalMute;
    }

    public static void setGlobalMute(boolean globalMute) {
        BukkitConfig.globalMute = globalMute;
    }

    public static List<String> getCmdlist() {
        return config.getStringList("cmdlist");
    }

    public static List<String> getCmdblist() {
        return config.getStringList("cmdblist");
    }

    public static List<String> getCmdchat() {
        return  config.getStringList("cmdchat");
    }

    public static EventPriority getCmdpriority() {
        return EventPriority.valueOf(config.getString("cmdpriority", "LOWEST").toUpperCase());
    }

    public static EventPriority getChatpriority() {
        return EventPriority.valueOf(config.getString("chatpriority", "LOWEST").toUpperCase());
    }

    public static boolean cmdfilterEnabled() {
        return config.getBoolean("commandfilter");
    }

    public static boolean commandspamfilterEnabled() {
        return config.getBoolean("commandspamfilter");
    }

    public static boolean spamfilterEnabled() {
        return config.getBoolean("spamfilter");
    }

    public static EventPriority getBookpriority() {
        return EventPriority.valueOf(config.getString("bookpriority", "LOWEST").toUpperCase());
    }

    public static boolean bookfilterEnabled() {
        return config.getBoolean("bookfilter");
    }

    public static boolean itemFilterEnabled() {
        return config.getBoolean("itemfilter");
    }

    public static EventPriority getItempriority() {
        return EventPriority.valueOf(config.getString("itempriority", "LOWEST").toUpperCase());
    }

    public static boolean consolefilterEnabled() {
        return config.getBoolean("consolefilter");
    }

    public static EventPriority getSignpriority() {
        return EventPriority.valueOf(config.getString("signpriority", "LOWEST").toUpperCase());
    }

    public static boolean signfilterEnabled() {
        return config.getBoolean("signfilter");
    }


}
