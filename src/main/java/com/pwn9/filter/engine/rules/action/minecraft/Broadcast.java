/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2014 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.minecraft;

import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.minecraft.api.MinecraftConsole;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.util.tag.TagRegistry;
import org.bukkit.ChatColor;

import java.util.ArrayList;

/**
 * Responds to the user with the string provided.
 * <p>
 * TODO: Extract Broadcast actions from Minecraft to make them universal.
 */
@SuppressWarnings("UnusedDeclaration")
public class Broadcast implements Action {
    private final String[] messageStrings;

    private Broadcast(String[] strings) {
        this.messageStrings = strings;
    }

    /**
     * {@inheritDoc}
     */
    static Action getAction(String s) {
        //TODO: Handle chatcolors outside of FilterService?

        return new Broadcast(ChatColor.translateAlternateColorCodes('&', s).split("\n"));
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask) {

        /*
        TODO: Abstract out to a BroadcastDestination object, so that broadcasts
        can be sent to more than just the minecraft server (eg: to IRC)
         */

        final ArrayList<String> preparedMessages = new ArrayList<String>();

        for (String message : messageStrings) {
            preparedMessages.add(TagRegistry.replaceTags(message, filterTask));
        }

        filterTask.addLogMessage("Broadcasted: " + preparedMessages.get(0) + (preparedMessages.size() > 1 ? "..." : ""));

        MinecraftConsole.getInstance().sendBroadcast(preparedMessages);

    }
}

