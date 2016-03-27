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

import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.engine.FilterService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Reload the PwnFilter config.
 * User: Sage905
 * Date: 13-08-10
 * Time: 9:23 AM
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class pfreload implements CommandExecutor {
    private final FilterService filterService;
    /**
     * <p>Constructor for pfreload.</p>
     *
     */
    public pfreload(FilterService filterService) {
        this.filterService = filterService;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "Reloading config.yml and rules/*.txt files.");

        filterService.getLogger().info("Disabling all listeners");
        filterService.disableClients();

        PwnFilterPlugin.getInstance().reloadConfig();
        PwnFilterPlugin.getInstance().configurePlugin();

        filterService.getLogger().config("Reloaded config.yml as requested by " + sender.getName());

        //TODO: MAKE THIS COMMAND WORK AGAIN!
        //
        // reloadAllConfigs();

        filterService.getLogger().config("All rules reloaded by " + sender.getName());

        // Re-register our listeners
        filterService.enableClients();
        filterService.getLogger().info("All listeners re-enabled");

        return true;

    }

}
