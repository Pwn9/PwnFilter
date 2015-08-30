/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.command;

import com.pwn9.PwnFilter.PwnFilter;
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
public class pfcls implements CommandExecutor {

    private final PwnFilter plugin;

    /**
     * <p>Constructor for pfcls.</p>
     *
     * @param plugin a {@link com.pwn9.PwnFilter.PwnFilter} object.
     */
    public pfcls(PwnFilter plugin) {
        this.plugin = plugin;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "Clearing chat screen");
        LogManager.logger.info("chat screen cleared by " + sender.getName());
        int i = 0;
        while (i <= 120) {
            plugin.getServer().broadcastMessage(" ");
            i++;
        }
        return true;
    }

}
