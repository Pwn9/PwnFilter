/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.bukkit;

import com.pwn9.PwnFilter.api.MessageAuthor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A Bukkit Console Abstraction
 * Created by ptoal on 15-09-08.
 */
public class BukkitConsole implements MessageAuthor {

    private static BukkitConsole ourInstance = new BukkitConsole();

    public static BukkitConsole getInstance() {
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

    private BukkitConsole() {

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

    public void executeCommand(final String command) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }.runTask(PwnFilterPlugin.getInstance());
    }

}
