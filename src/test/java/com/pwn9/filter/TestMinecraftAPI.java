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

import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import com.pwn9.filter.minecraft.api.PlayerData;
import org.bukkit.permissions.Permission;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 *
 * Stubbed out MinecraftAPI for our tests.
 * Created by Sage905 on 15-09-11.
 */
public class TestMinecraftAPI implements MinecraftAPI {

    final Set<String> permSet = new HashSet<String>();

    @Override
    public void reset() {

    }

    @Override
    public synchronized void addCachedPermission(String permission) {
        permSet.add(permission);
    }


    /**
     * <p>addCachedPermissions.</p>
     *
     * @param permissions a {@link java.util.List} object.
     */
    @Override
    public synchronized void addCachedPermissions(List<Permission> permissions) {
        for (Permission p : permissions) {
            permSet.add(p.getName());
        }
    }

    @Override
    public MessageAuthor getAuthor(UUID uuid) {
        return null;
    }

    @Override
    public PlayerData getData(UUID uuid) throws ExecutionException {
        return null;
    }

    @Override
    public boolean burn(UUID uuid, int duration, String messageString) {
        return false;
    }

    @Override
    public void sendMessage(UUID uuid, String message) {

    }

    @Override
    public void sendMessages(UUID uuid, List<String> messages) {

    }

    @Override
    public void executePlayerCommand(UUID uuid, String command) {

    }

    @Override
    public boolean withdrawMoney(UUID uuid, Double amount, String messageString) {
        return false;
    }

    @Override
    public void kick(UUID uuid, String messageString) {

    }

    @Override
    public void kill(UUID uuid, String messageString) {

    }

    @Override
    public String getPlayerWorldName(UUID uuid) {
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

    }

    @Override
    public boolean notifyWithPerm(String permissionString, String sendString) {
        return false;
    }
}
