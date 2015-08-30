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

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.api.ClientManager;
import com.pwn9.PwnFilter.rules.RuleManager;
import com.pwn9.PwnFilter.util.LogManager;
import com.pwn9.PwnFilter.util.PointManager;
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
public class pfreload implements CommandExecutor {

    private final PwnFilter plugin;

    /**
     * <p>Constructor for pfreload.</p>
     *
     * @param plugin a {@link com.pwn9.PwnFilter.PwnFilter} object.
     */
    public pfreload(PwnFilter plugin) {
        this.plugin = plugin;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "Reloading config.yml and rules/*.txt files.");

        LogManager.logger.info("Disabling all listeners");
        ClientManager.getInstance().disableClients();

        // Shut down the DataCache
        DataCache.getInstance().stop();

        plugin.reloadConfig();
        plugin.configurePlugin();

        LogManager.logger.config("Reloaded config.yml as requested by " + sender.getName());

        PointManager.setup(plugin);

        RuleManager.getInstance().reloadAllConfigs();
        LogManager.logger.config("All rules reloaded by " + sender.getName());

        // Start the DataCache again
        DataCache.getInstance().start();

        // Re-register our listeners
        ClientManager.getInstance().enableClients();
        LogManager.logger.info("All listeners re-enabled");

        return true;

    }

}
