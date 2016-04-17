/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.minecraft.api;

import java.util.List;
import java.util.UUID;

/**
 * A generic Minecraft API interface containing all the calls we require into
 * the server.
 *
 * Created by Sage905 on 15-09-11.
 */
public interface MinecraftAPI {
    void reset();

    boolean burn(UUID uuid, final int duration, final String messageString);

    void sendMessage(UUID uuid, final String message);

    void sendMessages(UUID uuid, final List<String> messages);

    void executePlayerCommand(UUID uuid, final String command);

    boolean withdrawMoney(UUID uuid, final Double amount, final String messageString);

    void kick(UUID uuid, final String messageString);

    void kill(UUID uuid, final String messageString);

    String getPlayerWorldName(UUID uuid);

    String getPlayerName(UUID uuid);

    Boolean playerIdHasPermission(UUID u, String s);

    // Console APIs

    void sendConsoleMessage(String message);

    void sendConsoleMessages(List<String> messageList);

    void sendBroadcast(String message);

    void sendBroadcast(List<String> messageList);

    void executeCommand(String command);

}
