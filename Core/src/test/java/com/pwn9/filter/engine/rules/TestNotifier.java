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

package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.api.NotifyTarget;

import java.util.HashMap;
import java.util.Map;

class TestNotifier implements NotifyTarget {

    private final Map<String, String> notificationMap = new HashMap<>();

    @Override
    public void notifyWithPerm(String permissionString, String sendString) {
        notificationMap.putIfAbsent(permissionString, sendString);
    }

    String getNotification(String permission) {
        return notificationMap.get(permission);
    }
}
