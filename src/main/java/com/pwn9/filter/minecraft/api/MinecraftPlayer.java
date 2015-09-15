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
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Author of a text string sent to us by Bukkit.  This is typically a player.
 * These objects are transient, and only last for as long as the message does.
 * <p/>
 * Created by ptoal on 15-08-31.
 */
public class MinecraftPlayer implements MessageAuthor {

    private final UUID playerId;

    private MinecraftPlayer(UUID uuid) {
        this.playerId = uuid;
    } // Do not allow external creation of class
    

    public static MinecraftPlayer getInstance(Player player) {
        return new MinecraftPlayer(player.getUniqueId());
    }

    public static MinecraftPlayer getInstance(UUID uuid) {
        return new MinecraftPlayer(uuid);
    }
    /**
     * <p>hasPermission.</p>
     *
     * @param s a {@link String} object.
     * @return a boolean.
     */
    public boolean hasPermission(String s) {
        try {
            return MinecraftServer.getAPI().getData(playerId).getPermissionSet().contains(s);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * <p>hasPermission.</p>
     *
     * @param perm a {@link org.bukkit.permissions.Permission} object.
     * @return a boolean.
     */
    public boolean hasPermission(Permission perm) {
        return hasPermission(perm.getName());
    }

    @NotNull
    public String getName() {
        try {
            return MinecraftServer.getAPI().getData(playerId).getName();
        } catch (Exception e) {
            return "";
        }

   }

    @NotNull
    public UUID getID() {
        return playerId;
    }

    public boolean burn(final int duration, final String messageString) {
        return MinecraftServer.getAPI().burn(playerId, duration, messageString);
    }

    @Override
    public void sendMessage(final String message) {
        MinecraftServer.getAPI().sendMessage(playerId, message);
    }

    @Override
    public void sendMessages(final List<String> messages) {
        MinecraftServer.getAPI().sendMessages(playerId, messages);
    }

    public void executeCommand(final String command) {
        MinecraftServer.getAPI().executePlayerCommand(playerId, command);
    }

    public boolean withdrawMoney(final Double amount, final String messageString) {
        return MinecraftServer.getAPI().withdrawMoney(playerId, amount, messageString);
    }

    public void kick(final String messageString) {
        MinecraftServer.getAPI().kick(playerId, messageString);
    }

    public void kill(final String messageString) {
        MinecraftServer.getAPI().kill(playerId, messageString);
    }

    public String getWorldName() {
        return MinecraftServer.getAPI().getPlayerWorldName(playerId);
    }


}
