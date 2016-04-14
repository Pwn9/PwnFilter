/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter;

import com.pwn9.filter.minecraft.api.MinecraftAPI;

import java.util.List;
import java.util.UUID;

/**
 *
 * Stubbed out MinecraftAPI for our tests.
 * Created by Sage905 on 15-09-11.
 */
public class MockMinecraftAPI implements MinecraftAPI {

    // Simple flag to set what we want permission checks to return.
    public Boolean permReturnValue = false;
    private String executedCommand = "";

    @Override
    public void reset() {
    }

    @Override
    public Boolean playerIdHasPermission(UUID u, String s) {
        return permReturnValue;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return null;
    }

    @Override
    public boolean burn(java.util.UUID uuid, int duration, String messageString) {
        return false;
    }

    @Override
    public void sendMessage(java.util.UUID uuid, String message) {

    }

    @Override
    public void sendMessages(java.util.UUID uuid, List<String> messages) {

    }

    @Override
    public void executePlayerCommand(java.util.UUID uuid, String command) {

    }

    @Override
    public boolean withdrawMoney(java.util.UUID uuid, Double amount, String messageString) {
        return false;
    }

    @Override
    public void kick(java.util.UUID uuid, String messageString) {

    }

    @Override
    public void kill(java.util.UUID uuid, String messageString) {

    }

    @Override
    public String getPlayerWorldName(java.util.UUID uuid) {
        return null;
    }

    @Override
    public void sendConsoleMessage(String message) {

    }

    @Override
    public void sendConsoleMessages(List<String> messageList) {

    }

    @Override
    public void sendBroadcast(String message) {

    }

    @Override
    public void sendBroadcast(List<String> messageList) {

    }

    @Override
    public void executeCommand(String command) {
        executedCommand = command;
    }

    public String getExecutedCommand() {
        return executedCommand;
    }

    @Override
    public boolean notifyWithPerm(String permissionString, String sendString) {
        return false;
    }

}
