/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.util.tag;

import com.pwn9.filter.engine.api.FilterContext;

/**
 * Player Display Name Tag
 * Created by Sage905 on 15-09-04.
 */
public class EventTag implements Tag {
    @Override
    public String getValue(FilterContext filterTask) {
       return filterTask.getListenerName();
    }
}
