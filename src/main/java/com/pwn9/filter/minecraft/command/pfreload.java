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

package com.pwn9.filter.minecraft.command;

import com.pwn9.filter.bukkit.PwnFilterBukkitPlugin;
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
    private final PwnFilterBukkitPlugin plugin;
    /**
     * <p>Constructor for pfreload.</p>
     *
     */
    public pfreload(FilterService filterService, PwnFilterBukkitPlugin plugin) {
        this.filterService = filterService;
        this.plugin = plugin;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "Reloading config.yml and rules/*.txt files.");

        filterService.getLogger().info("Disabling all listeners");
        filterService.disableClients();

        if (!plugin.configurePlugin()) return false;

        filterService.getLogger().config("Reloaded config.yml as requested by " + sender.getName());
        filterService.getLogger().config("All rules reloaded by " + sender.getName());

        // Re-register our listeners
        filterService.enableClients();
        filterService.getLogger().info("All listeners re-enabled");

        return true;

    }

}
