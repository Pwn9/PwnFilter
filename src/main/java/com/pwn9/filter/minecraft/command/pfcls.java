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

import com.pwn9.filter.minecraft.api.MinecraftConsole;
import com.pwn9.filter.util.LogManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * <p>Constructor for pfcls.</p>
     *
     */
    public pfcls() {}

    /** {@inheritDoc} */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "Clearing chat screen");
        LogManager.logger.info("chat screen cleared by " + sender.getName());
        int i = 0;
        List<String> messages = new ArrayList<String>();
        while (i <= 120) {
            messages.add(" ");
            i++;
        }
        MinecraftConsole.getInstance().sendBroadcast(messages);

        return true;
    }

}
