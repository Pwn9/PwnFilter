/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.util.tag;

import com.pwn9.filter.minecraft.tag.PlayerTag;
import com.pwn9.filter.minecraft.tag.WorldTag;

/**
 * Register Built-in Tags
 * Created by Sage905 on 15-09-08.
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
