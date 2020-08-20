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

/*
 * This executor is designed to handle commands which have sub-commands.
 * It provides command completion and help from sub-commands.
 * <p>
 * User: Sage905
 * Date: 13-07-01
 * Time: 4:19 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */


import com.pwn9.filter.engine.api.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseCommandExecutor implements PwnFilterCommandExecutor {

    private final Map<String, SubCommand> subCommands;

    /**
     * <p>Constructor for BaseCommandExecutor.</p>
     */
    public BaseCommandExecutor() {
        subCommands = new HashMap<>();
    }

    /**
     * <p>addSubCommand.</p>
     *
     * @param subCommand a {@link com.pwn9.filter.minecraft.command.SubCommand} object.
     */
    public void addSubCommand(SubCommand subCommand) {
        if (subCommand == null) {
            throw new IllegalArgumentException("SubCommand was null.");
        }
        subCommands.put(subCommand.getName(), subCommand);
    }

    public boolean onCommand(final CommandSender sender, final String command, String alias, final String[] args) {
        if (args.length < 1) {
            sendHelpMsg(sender, alias);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sendHelpMsg(sender, alias);
        } else {
            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                return subCommand.execute(sender, alias, args);
            } else {
                sender.sendMessage("You don't have permission to do that.");
                return true;
            }
        }
        return true;
    }

    /**
     * <p>sendHelpMsg.</p>
     *
     * @param sender a {@link CommandSender} object.
     * @param alias a {@link String} object.
     */
    private void sendHelpMsg(CommandSender sender, String alias) {

        ArrayList<SubCommand> availableCommands = new ArrayList<>();

        for (SubCommand subCommand : subCommands.values()) {
            if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission()))
                continue;
            if (!subCommand.getHelpMessage().isEmpty()) {
                availableCommands.add(subCommand);
            }
        }

        if (availableCommands.size() != 0) {
            sender.sendMessage("Available commands for " + alias + ":");

            for (SubCommand subCommand : availableCommands) {
                sender.sendMessage("/" + alias + " " +
                        subCommand.getHelpMessage());
            }
        }

    }

    /**
     * {@inheritDoc}
     *
     * Requests a list of possible completions for a command argument.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (SubCommand subCommand : subCommands.values()) {
                if (!subCommand.getName().startsWith(args[0])) continue;

                if (subCommand.getPermission() == null ||
                        sender.hasPermission(subCommand.getPermission())) {
                    completions.add(subCommand.getName());
                }
            }
        } else if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                return subCommand.onTabComplete(sender, alias, args);
            }
        }

        if (completions.isEmpty()) return null;

        return completions;
    }
}
