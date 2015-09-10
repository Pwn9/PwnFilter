/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.bukkit.api;

import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.bukkit.PwnFilterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * A Bukkit Console Abstraction
 * Created by ptoal on 15-09-08.
 */
public class BukkitServer implements MessageAuthor {

    private static BukkitServer ourInstance = new BukkitServer();

    public static BukkitServer getInstance() {
        return ourInstance;
    }

    @Override
    public boolean hasPermission(String permString) {
        return false;
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

    private BukkitServer() {

    }

    @Override
    public void sendMessage(final String message) {
        PwnFilterPlugin.getBukkitAPI().safeBukkitDispatch(new Runnable() {
            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage(message);
            }
        });
    }

    @Override
    public void sendMessages(final List<String> messageList) {
        PwnFilterPlugin.getBukkitAPI().safeBukkitDispatch(new Runnable() {
            @Override
            public void run() {
                for (String message : messageList) {
                    Bukkit.getConsoleSender().sendMessage(message);
                }
            }
        });
    }

    public void executeCommand(final String command) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }.runTask(PwnFilterPlugin.getInstance());
    }

    public boolean notifyWithPerm(final String permissionString, final String sendString) {

        PwnFilterPlugin.getBukkitAPI().safeBukkitDispatch(
                new Runnable() {
                    @Override
                    public void run() {
                        if (permissionString.equalsIgnoreCase("console")) {
                            Bukkit.getConsoleSender().sendMessage(sendString);
                        } else {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.hasPermission(permissionString)) {
                                    p.sendMessage(sendString);
                                }
                            }
                        }
                    }
                });

        return true;
    }
}
