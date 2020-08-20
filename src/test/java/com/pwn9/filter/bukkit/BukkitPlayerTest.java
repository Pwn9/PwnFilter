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

package com.pwn9.filter.bukkit;

import com.pwn9.filter.MockMinecraftAPI;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BukkitPlayerTest {

    private final TestTicker ticker = new TestTicker();

    @Test
    public void hasPermissionWorksWithNull() throws Exception {
        MockMinecraftAPI api = new MockMinecraftAPI();

        BukkitPlayer bukkitPlayer =
                new BukkitPlayer(UUID.randomUUID(), api);

        // Test a simple permission
        api.permReturnValue = null;
        assertFalse(bukkitPlayer.hasPermission("TestTrue"));

    }


    @Test
    public void testHasPermissionCachesValues() throws Exception {
        MockMinecraftAPI api = new MockMinecraftAPI();

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
        ticker.setElapsed(TimeUnit.SECONDS.toNanos(BukkitPlayer.MAX_CACHE_AGE_SECS + 1));
        assertFalse(bukkitPlayer.hasPermission("TestTrue"));

    }


}