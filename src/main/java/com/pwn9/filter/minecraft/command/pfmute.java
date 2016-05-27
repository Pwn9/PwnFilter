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

    public pfmute(Logger logger, MinecraftConsole console) {
        this.logger = logger;
        this.console = console;
    }

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
