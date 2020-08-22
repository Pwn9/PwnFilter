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

package com.pwn9.filter.bukkit.config;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.PointManager;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.rules.action.ActionFactory;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.util.PwnFormatter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A largely static object, which serves as an interface to the PwnFilter Bukkit
 * configuration.
 * Created by Sage905 on 15-09-10.
 */
public class BukkitConfig {

    // Global Plugin switches
    private static boolean globalMute = false;
    private static Configuration config;

    private static File dataFolder;

    public static void loadConfiguration(Configuration configuration, File folder, FilterService filterService) throws InvalidConfigurationException {

        dataFolder = folder;
        config = configuration;

        if (config.getBoolean("logfile")) {
            filterService.setLogFileHandler(new File(dataFolder, "pwnfilter.log"));
            filterService.setDebugMode(config.getString("debug", "off"));

        } else { // Needed during configuration reload to turn off logging if the option changes
            filterService.clearLogFileHandler();
        }

        // Set the directory containing rules files.
        File ruleDir = setupDirectory(config.getString("ruledirectory", "rules"),
                filterService.getLogger());
        if (ruleDir != null) {
            filterService.getConfig().setRulesDir(ruleDir);
        } else {
            throw new InvalidConfigurationException(
                    "Unable to create or access rule directory.");
        }

        // Set the directory containing Text Files
        filterService.getConfig().setTextDir(
                setupDirectory(config.getString("textdir", "textfiles"),
                        filterService.getLogger())
        );

        // Set up the default action messages
        TargetedAction.getActionsWithDefaults().
                filter(targetedAction -> !(config.getString(targetedAction.getDefaultMsgConfigName()) == null)).
                forEach(targetedAction -> targetedAction.setDefMsg(PwnFormatter.legacyTextConverter(
                                config.getString(targetedAction.getDefaultMsgConfigName())))
                );

        // Setup logging
        Level logLevel;
        try {
            logLevel = Level.parse(config.getString("loglevel", "info").toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidConfigurationException("Could not parse loglevel.  Must be either 'info' or 'fine'.  Found: " + config.getString("loglevel"));
        }
        filterService.getConfig().setLogLevel(logLevel);

        setupPoints(filterService);

    }

    private static void setupPoints(FilterService filterService) {
        PointManager pointManager = filterService.getPointManager();
        ConfigurationSection pointsSection = config.getConfigurationSection("points");
        if (!pointsSection.getBoolean("enabled")) {
            if (pointManager.isEnabled()) {
                pointManager.shutdown();
            }
        } else {
            if (!pointManager.isEnabled()) {
                pointManager.setLeakPoints(pointsSection.getDouble("leak.points", 1));
                pointManager.setLeakInterval(pointsSection.getInt("leak.interval", 30));

                try {
                    parseThresholds(pointsSection.getConfigurationSection("thresholds"), pointManager, filterService.getActionFactory());
                } catch (InvalidActionException ex) {
                    filterService.getLogger().warning("Invalid Action parsing Thresholds: " + ex.getMessage());
                    pointManager.shutdown();
                }
                pointManager.start();
            }
        }
    }

    private static void parseThresholds(ConfigurationSection configSection,
                                        PointManager pointManager,
                                        ActionFactory actionFactory)
            throws InvalidActionException {

        for (String threshold : configSection.getKeys(false)) {
            pointManager.getFilterService().getLogger().
                    finest("Parsing Threshold: " + threshold);

            List<Action> ascending = new ArrayList<>();
            List<Action> descending = new ArrayList<>();

            for (String action : configSection.getStringList(threshold + ".actions.ascending")) {
                pointManager.getFilterService().getLogger().
                        finest("Adding Ascending Action: " + action);
                ascending.add(actionFactory.getActionFromString(action));
            }
            for (String action : configSection.getStringList(threshold + ".actions.descending")) {
                pointManager.getFilterService().getLogger().
                        finest("Adding Descending Action: " + action);
                descending.add(actionFactory.getActionFromString(action));
            }
            pointManager.addThreshold(
                    configSection.getString(threshold + ".name"),
                    configSection.getDouble(threshold + ".points"),
                    ascending,
                    descending);
            pointManager.getFilterService().getLogger().
                    finest("Adding Threshold: " + configSection.getString(threshold + ".name") +
                            " at points: " + configSection.getDouble(threshold + ".points"));
        }

    }

    /**
     * Ensure that the named directory exists and is accessible.  If the
     * directory begins with a / (slash), it is assumed to be an absolute
     * path.  Otherwise, the directory is assumed to be relative to the root
     * data folder.
     * <p/>
     * If the directory doesn't exist, an attempt is made to create it.
     *
     * @param directoryName relative or absolute path to the directory
     * @return {@link File} referencing the directory.
     */
    private static File setupDirectory(@NotNull String directoryName,
                                       Logger logger) {
        File dir;
        if (directoryName.startsWith("/")) {
            dir = new File(directoryName);
        } else {
            dir = new File(dataFolder, directoryName);
        }
        try {
            if (!dir.exists()) {
                if (dir.mkdirs())
                    logger.info("Created directory: " + dir.getAbsolutePath());
            }
            return dir;
        } catch (Exception ex) {
            logger.warning("Unable to access or create directory: " + dir.getAbsolutePath());
            return null;
        }

    }

    public static boolean decolor() {
        return config.getBoolean("decolor");
    }

    public static boolean globalMute() {
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
        return config.getStringList("cmdchat");
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
