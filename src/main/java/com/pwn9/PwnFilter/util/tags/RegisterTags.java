/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.util.tags;

import com.pwn9.PwnFilter.minecraft.tag.PlayerTag;
import com.pwn9.PwnFilter.minecraft.tag.WorldTag;

/**
 * Register Built-in Tags
 * Created by ptoal on 15-09-08.
 */
public class RegisterTags {

    public static void all() {
        TagRegistry.addTag("world", new WorldTag());
        TagRegistry.addTag("player", new PlayerTag());
        TagRegistry.addTag("string", new StringTag());
        TagRegistry.addTag("rawstring", new RawStringTag());
        TagRegistry.addTag("event", new EventTag());
        TagRegistry.addTag("points", new PointsTag());
        TagRegistry.addTag("ruleid", new RuleIdTag());
        TagRegistry.addTag("ruledescr", new RuleDescriptionTag());
    }
}
