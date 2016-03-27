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

import com.pwn9.filter.engine.api.MessageAuthor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * A Bukkit Console Abstraction
 * Created by Sage905 on 15-09-08.
 */
public class MinecraftConsole implements MessageAuthor {

    private static final MinecraftConsole ourInstance = new MinecraftConsole();

    public static MinecraftConsole getInstance() {
        return ourInstance;
    }


    @Override
    public boolean hasPermission(String permString) {
        return true;
    }

    @NotNull
    @Override
    public String getName() {
        return "CONSOLE";
    }

    @NotNull
    @Override
    public UUID getID() {
        return UUID.fromString("CONSOLE");
    }

    private MinecraftConsole() {}

    @Override
    public void sendMessage(final String message) {
       MinecraftServer.getAPI().sendConsoleMessage(message);
    }

    @Override
    public void sendMessages(final List<String> messageList) {
        MinecraftServer.getAPI().sendConsoleMessages(messageList);
    }

    public void sendBroadcast(final List<String> preparedMessages) {
       MinecraftServer.getAPI().sendBroadcast(preparedMessages);

    }

    public void sendBroadcast(final String message) {
        MinecraftServer.getAPI().sendBroadcast(message);

    }

    public void executeCommand(final String command) {
        MinecraftServer.getAPI().executeCommand(command);
    }

    public void notifyWithPerm(final String permissionString, final String sendString) {
        MinecraftServer.getAPI().notifyWithPerm(permissionString, sendString);
    }
}
