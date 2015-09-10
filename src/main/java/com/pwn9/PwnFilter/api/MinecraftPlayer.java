/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.api;

/**
 * Implements Minecraft Player Actions
 *
 * Created by ptoal on 15-09-10.
 */
public interface MinecraftPlayer {

    void executeCommand(String cmd);

    boolean withdrawMoney(final Double amount, final String messageString);

    void kick(final String messageString);

    void kill(final String messageString);

    boolean burn(final int duration, final String messageString);
}
