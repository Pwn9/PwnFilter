/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.minecraft.api;

/**
 * Tiny class to store a reference to the Minecraft API we're using at runtime.
 *
 * Created by ptoal on 15-09-11.
 */
public class MinecraftServer {

    private static MinecraftAPI activeAPI;

    public static MinecraftAPI getAPI() {
        return activeAPI;
    }

    public static void setAPI(MinecraftAPI activeAPI) {
        MinecraftServer.activeAPI = activeAPI;
    }
}
