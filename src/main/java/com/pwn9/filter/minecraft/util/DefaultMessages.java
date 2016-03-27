/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.minecraft.util;

import com.pwn9.filter.bukkit.PwnFilterPlugin;
import org.bukkit.ChatColor;

/**
 * Simple helper to get default messages from the PwnFilter config.yml
 * User: Sage905
 * Date: 13-10-01
 * Time: 3:56 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class DefaultMessages {

    /**
     * Selects string from the first not null of: message, default from config.yml or null.
     * Converts & to u00A7
     * Used by Action.newAction() methods.
     *
     * @return String containing message to be used.
     * @param message a {@link java.lang.String} object.
     * @param configVarName a {@link java.lang.String} object.
     */
    public static String prepareMessage(String message, String configVarName) {
        String result;
        if ( message == null || message.isEmpty()) {
            String defmsg = PwnFilterPlugin.getInstance().getConfig().getString(configVarName);
            result = (defmsg != null) ? defmsg : "";
        } else {
            result = message;
        }
        return ChatColor.translateAlternateColorCodes('&',result);
    }

}
