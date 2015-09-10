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

import com.pwn9.PwnFilter.FilterTask;

/**
 * During message processing, a rule can specify a %tag%, which will be replaced
 * with the content of a Tag object.
 *
 * Tags are registered by calling RegisterTags.addTag("keyword", Tag )
 *
 * Created by ptoal on 15-09-04.
 */
public interface Tag {

    String getValue(FilterTask filterTask);

}
