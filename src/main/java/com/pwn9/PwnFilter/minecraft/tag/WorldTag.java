/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.minecraft.tag;

import com.pwn9.PwnFilter.FilterTask;
import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.minecraft.api.MinecraftPlayer;
import com.pwn9.PwnFilter.util.tags.Tag;

/**
 * Return the World a Player currently inhabits.
 * Created by ptoal on 15-09-04.
 */
public class WorldTag implements Tag {

    @Override
    public String getValue(FilterTask filterTask) {
        MessageAuthor author = filterTask.getAuthor();
        if (author instanceof MinecraftPlayer) {
            return ((MinecraftPlayer) author).getWorldName();
        }
        return "";
    }
}
