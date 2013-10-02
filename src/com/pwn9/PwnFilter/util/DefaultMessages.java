package com.pwn9.PwnFilter.util;

import org.bukkit.configuration.Configuration;

/**
 * Simple helper to get default messages from the PwnFilter config.yml
 * User: ptoal
 * Date: 13-10-01
 * Time: 3:56 PM
 */

public class DefaultMessages {

    private static Configuration config;

    /**
     * Selects string from the first not null of: message, default from config.yml or null.
     * Converts & to u00A7
     * Used by Action.init() methods.
     * @return String containing message to be used.
     */
    public static String prepareMessage(String message, String configName) {
        String retVal;
        if (message.isEmpty()) {
            String defmsg = config.getString(configName);
            retVal = (defmsg != null) ? defmsg : "";
        } else {
            retVal = message;
        }
        return retVal.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    }

    public static void setConfig(Configuration c) {
        config = c;
    }

}
