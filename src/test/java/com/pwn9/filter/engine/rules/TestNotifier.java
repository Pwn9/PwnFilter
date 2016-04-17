/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.api.NotifyTarget;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sage905 on 2016-04-16.
 */
public class TestNotifier implements NotifyTarget {

    Map<String, String> notificationMap = new HashMap<>();

    @Override
    public void notifyWithPerm(String permissionString, String sendString) {
        notificationMap.putIfAbsent(permissionString, sendString);
    }

    public String getNotification(String permission) {
        return notificationMap.get(permission);
    }
}
