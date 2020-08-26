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

import com.pwn9.filter.engine.api.CommandSender;

import java.util.List;

/**
 * SubCommand adds the plugin to the instance of Command.
 * User: Sage905
 * Date: 13-08-16
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 *
 * @author Sage905
 * @version $Id: $Id
 */
abstract class SubCommand {

    private final String name;
    private final String permission;
    private final String usageMessage;
    private final String description;

    /**
     * <p>Constructor for SubCommand.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public SubCommand(String name,String permission,String usage, String description) {
        this.name = name;
        this.permission = permission;
        this.usageMessage = usage;
        this.description = description;

    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getUsage() {
        return this.usageMessage;
    }

    public String getDescription(){
        return this.description;
    }
    /**
     * <p>getHelpMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getHelpMessage() {
        String message = "";
        if (!getUsage().isEmpty()) message = getUsage();
        if (!getDescription().isEmpty()) {
            if (!message.isEmpty()) {
                message = message + " -- " + getDescription();
            } else {
                message = getDescription();
            }
        }
        return message;
    }

    public abstract boolean execute(CommandSender sender, String alias, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender,String alias,String[] args);
}
