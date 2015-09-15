/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.api;

import java.util.List;

/**
 * An interface for tracking statistics about FilterEngine parameters
 *
 * Created by ptoal on 15-09-13.
 */
public interface StatsTracker {

    void updateClients(List<FilterClient> filterClientSet);

    void startTracking();

    void incrementMatch();


}
