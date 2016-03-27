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

import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple storage for cached Bukkit Player Data.
 *
 * <p/>
 * Created by Sage905 on 15-09-06.
 */
public class PlayerData {
    // Store cached data about players.
    private Set<String> permissionSet = new HashSet<String>();
    private String name;
    private World world;


    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPermissionSet(Set<String> permSet) {
        this.permissionSet = permSet;
    }

    public Set<String> getPermissionSet() {
        return permissionSet;
    }
}