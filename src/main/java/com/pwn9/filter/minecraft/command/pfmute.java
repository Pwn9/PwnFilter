/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.minecraft.command;

import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.minecraft.api.MinecraftConsole;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

/**
 * Reload the PwnFilter config.
 * User: Sage905
 * Date: 13-08-10
 * Time: 9:23 AM
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class pfmute implements CommandExecutor {
    private final Logger logger;
    private final MinecraftConsole console;

    /**
     * <p>Constructor for pfmute.</p>
     *
     */
    public pfmute(Logger logger, MinecraftConsole console) {
        this.logger = logger;
        this.console = console;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (BukkitConfig.globalMute()) {
            console.sendBroadcast(ChatColor.RED + "Global mute cancelled by " + sender.getName());
            logger.info("global mute cancelled by " + sender.getName());
            BukkitConfig.setGlobalMute(false);
        }
        else {
            console.sendBroadcast(ChatColor.RED + "Global mute initiated by " + sender.getName());
            logger.info("global mute initiated by " + sender.getName());
            BukkitConfig.setGlobalMute(true);
        }
        return true;
    }

}
