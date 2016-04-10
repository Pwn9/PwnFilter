/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit;

import com.pwn9.filter.TestMinecraftAPI;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Sage905 on 2016-04-09.
 */
public class BukkitPlayerTest {

    TestTicker ticker = new TestTicker();


    @Test
    public void testHasPermissionCachesValues() throws Exception {
        TestMinecraftAPI api = new TestMinecraftAPI();

        // Set the ticker elapsed time to be 0, so all answers are cached.
        BukkitPlayer bukkitPlayer =
                new BukkitPlayer(UUID.randomUUID(), api, ticker);

        // Test a simple permission
        api.permReturnValue = Boolean.TRUE;
        assertTrue(bukkitPlayer.hasPermission("TestTrue"));

        // Ensure that the value is cached
        api.permReturnValue = Boolean.FALSE;
        assertTrue(bukkitPlayer.hasPermission("TestTrue"));

        // Now trigger a cache reset and ensure the value has been updated
        api.permReturnValue = Boolean.FALSE;
        ticker.setElapsed(TimeUnit.SECONDS.toNanos(BukkitPlayer.MAX_CACHE_AGE_SECS+1));
        assertFalse(bukkitPlayer.hasPermission("TestTrue"));

    }


}