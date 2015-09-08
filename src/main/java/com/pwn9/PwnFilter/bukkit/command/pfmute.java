/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.bukkit.command;

import com.pwn9.PwnFilter.bukkit.PwnFilterPlugin;
import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Reload the PwnFilter config.
 * User: ptoal
 * Date: 13-08-10
 * Time: 9:23 AM
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class pfmute implements CommandExecutor {

    private final PwnFilterPlugin plugin;

    /**
     * <p>Constructor for pfmute.</p>
     *
     * @param plugin a {@link PwnFilterPlugin} object.
     */
    public pfmute(PwnFilterPlugin plugin) {
        this.plugin = plugin;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (PwnFilterPlugin.globalMute) {
            plugin.getServer().broadcastMessage(ChatColor.RED + "Global mute cancelled by " + sender.getName());
            LogManager.logger.info("global mute cancelled by " + sender.getName());
            PwnFilterPlugin.globalMute = false;
        }
        else {
            plugin.getServer().broadcastMessage(ChatColor.RED + "Global mute initiated by " + sender.getName());
            LogManager.logger.info("global mute initiated by " + sender.getName());
            PwnFilterPlugin.globalMute = true;
        }
        return true;
    }

}
