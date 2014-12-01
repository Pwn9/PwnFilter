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

/**
 * This executor is designed to handle commands which have sub-commands.
 * It provides command completion and help from sub-commands.
 *
 * User: ptoal
 * Date: 13-07-01
 * Time: 4:19 PM
 */

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseCommandExecutor implements TabExecutor {
    protected final PwnFilter plugin;
    private final Map<String,SubCommand> subCommands;

    public BaseCommandExecutor(PwnFilter instance){
        plugin = instance;
        subCommands = new HashMap<String, SubCommand>();

    }

    public void addSubCommand(SubCommand subCommand) {
        if (subCommand == null) {
            throw new IllegalArgumentException("SubCommand was null.");
        }
        subCommands.put(subCommand.getName(), subCommand);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, String alias, final String[] args) {
        if (args.length < 1) {
            sendHelpMsg(sender, alias);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sendHelpMsg(sender, alias);
        } else {
            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                subCommand.execute(sender, alias, args);
            } else {
                sender.sendMessage("You don't have permission to do that.");
                return true;
            }
        }
        return true;
    }

    public boolean sendHelpMsg(CommandSender sender, String alias) {

        ArrayList<SubCommand> availableCommands = new ArrayList<SubCommand>();

        for ( SubCommand subCommand : subCommands.values()) {
            if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) continue;
            if (!subCommand.getHelpMessage().isEmpty()) {
                availableCommands.add(subCommand);
            }
        }

        if (availableCommands.size() != 0 ) {
            sender.sendMessage("Available commands for " + alias + ":");

            for ( SubCommand subCommand : availableCommands) {
                sender.sendMessage("/" + alias + " " +
                        subCommand.getHelpMessage());
            }
        }

        return true;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null to default to the command executor
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<String>();

        if (args.length == 1 ) {
            for ( SubCommand subCommand : subCommands.values() ) {
                if (!subCommand.getName().startsWith(args[0])) continue;

                if (subCommand.getPermission() == null ||
                        sender.hasPermission(subCommand.getPermission())) {
                    completions.add(subCommand.getName());
                }
            }
        } else if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if ( subCommand != null ) {
                return subCommand.tabComplete(sender, alias, args);
            }
        }

        if (completions.isEmpty()) return null;

        return completions;
    }
}
