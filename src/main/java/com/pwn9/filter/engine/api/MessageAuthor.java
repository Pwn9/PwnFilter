/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * MessageAuthors are the entities that write the message.  For example,
 * A Bukkit Player, an IRC user, etc.
 *
 * Created by ptoal on 15-08-31.
 */
public interface MessageAuthor {

    boolean hasPermission(String permString);

    @NotNull
    String getName();

    @NotNull
    UUID getID();

    void sendMessage(String message);

    void sendMessages(List<String> messages);

}
