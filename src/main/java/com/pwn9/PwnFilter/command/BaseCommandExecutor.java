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
 *
 * @author ptoal
 * @version $Id: $Id
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

    /**
     * <p>Constructor for BaseCommandExecutor.</p>
     *
     * @param instance a {@link com.pwn9.PwnFilter.PwnFilter} object.
     */
    public BaseCommandExecutor(PwnFilter instance){
        plugin = instance;
        subCommands = new HashMap<String, SubCommand>();

    }

    /**
     * <p>addSubCommand.</p>
     *
     * @param subCommand a {@link com.pwn9.PwnFilter.command.SubCommand} object.
     */
    public void addSubCommand(SubCommand subCommand) {
        if (subCommand == null) {
            throw new IllegalArgumentException("SubCommand was null.");
        }
        subCommands.put(subCommand.getName(), subCommand);
    }

    /** {@inheritDoc} */
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

    /**
     * <p>sendHelpMsg.</p>
     *
     * @param sender a {@link org.bukkit.command.CommandSender} object.
     * @param alias a {@link java.lang.String} object.
     * @return a boolean.
     */
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
     * {@inheritDoc}
     *
     * Requests a list of possible completions for a command argument.
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
