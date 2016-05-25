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

    private final MinecraftAPI minecraftAPI;
    
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
    public UUID getId() {
        return java.util.UUID.fromString("CONSOLE");
    }

    public MinecraftConsole(MinecraftAPI minecraftAPI) {
        this.minecraftAPI = minecraftAPI;
    }
    

    @Override
    public void sendMessage(final String message) {
       minecraftAPI.sendConsoleMessage(message);
    }

    @Override
    public void sendMessages(final List<String> messageList) {
        minecraftAPI.sendConsoleMessages(messageList);
    }

    public void sendBroadcast(final List<String> preparedMessages) {
       minecraftAPI.sendBroadcast(preparedMessages);

    }

    public void sendBroadcast(final String message) {
        minecraftAPI.sendBroadcast(message);

    }

    public void executeCommand(final String command) {
        minecraftAPI.executeCommand(command);
    }

}
