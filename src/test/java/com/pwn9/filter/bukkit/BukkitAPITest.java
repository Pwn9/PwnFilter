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

import com.pwn9.filter.MockPlayer;
import com.pwn9.filter.MockPlugin;
import com.pwn9.filter.engine.FilterService;
import net.jodah.concurrentunit.Waiter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class BukkitAPITest {

    private final PwnFilterPlugin testPlugin = new MockPlugin();
    private MockServer server;
    private Player testPlayer;
    private BukkitAPI api;

    @Before
    public void setup() {

        /* TODO: This is potentially a test nightmare, because another test might
           Load a different server object.  Maybe wrap the Bukkit class?
         */

        if (Bukkit.getServer() == null) {
            Bukkit.setServer(new MockServer());
        }
        server = (MockServer) Bukkit.getServer();
        testPlayer = new MockPlayer();
        api = new BukkitAPI(testPlugin);

        FilterService filterService = testPlugin.getFilterService();
        filterService.registerAuthorService(MockPlugin.getMockAuthorService());
    }

    @Test
    public void getAuthorByIdReturnsNullForNoMatch() {
        server.setPrimaryThread(Thread.currentThread());
        server.setScheduler(new WorkingScheduler());
        server.setPlayer(null);
        assertNull(api.getAuthorById(UUID.randomUUID()));
    }

    @Test
    public void testBasicCacheLoad() {
        server.setPlayer(testPlayer);
        server.clearPrimaryThread();
        server.setScheduler(new WorkingScheduler());
        BukkitPlayer player = api.getAuthorById(testPlayer.getUniqueId());
        assertFalse(server.isPrimaryThread());
        assertEquals(player.getId(), testPlayer.getUniqueId());
    }

    @Test
    public void testAsyncCacheLoad() throws Throwable {
        server.setPlayer(testPlayer);
        server.setScheduler(new WorkingScheduler());
        final Waiter waiter = new Waiter();

        new Thread(() -> {
            BukkitPlayer player = api.getAuthorById(testPlayer.getUniqueId());
            waiter.assertEquals(player.getId(), testPlayer.getUniqueId());
            waiter.resume();
        }).start();

        waiter.await(1000);
        assertEquals(testPlayer.getUniqueId(), api.getAuthorById(testPlayer.getUniqueId()).getId());
    }


    /*
    1. Fire off Async Event to call getAuthorById()
    2. When getScheduler().callSyncMethod is called, don't execute the task.  This will block the Bukkit Call.
    3. Execute the getAuthorById() method from a different thread.
    4. If the method is blocking, this will block both threads.  If not, both will return.
     */
    @Test(timeout = 100)
    public void testCacheLoadDoesNotBlock() throws Throwable {
        final BlockingScheduler scheduler = new BlockingScheduler();
        server.setScheduler(scheduler);
        server.setPlayer(testPlayer);
        final Thread mainThread = Thread.currentThread();
        server.setPrimaryThread(mainThread);
        Waiter waiter = new Waiter();
        final AtomicReference<BukkitPlayer> asyncPlayer = new AtomicReference<>();

        scheduler.setWaiter(waiter);
        new Thread(() -> {
            // Wait for this thread to start it's query, so we know it runs first.
            // The BlockingScheduler will automatically call waiter.resume() when the
            // callSyncMethod() function is called.
            asyncPlayer.set(api.getAuthorById(testPlayer.getUniqueId()));
            waiter.assertEquals(asyncPlayer.get().getId(), testPlayer.getUniqueId());
            waiter.resume();
        }).start();

        // Wait for the call to be scheduled
        waiter.await();

        // This should not block
        BukkitPlayer myPlayer = api.getAuthorById(testPlayer.getUniqueId());

        // Now release the thread.
        scheduler.releaseTask();
        waiter.await();

        assertEquals(asyncPlayer.get(), myPlayer);

    }

    @Test(timeout = 100)
    public void testPermissionLoadDoesNotBlock() throws Throwable {
        final BlockingScheduler scheduler = new BlockingScheduler();
        server.setScheduler(scheduler);
        server.setPlayer(testPlayer);
        final Thread mainThread = Thread.currentThread();
        server.setPrimaryThread(mainThread);
        Waiter waiter = new Waiter();
        BukkitPlayer bukkitPlayer;

        bukkitPlayer = api.getAuthorById(testPlayer.getUniqueId());

        scheduler.setWaiter(waiter);
        new Thread(() -> {
            waiter.assertFalse(bukkitPlayer.hasPermission("test.permission"));
            waiter.resume();
        }).start();

        // Wait for the call to be scheduled
        waiter.await();

        // This should not block
        BukkitPlayer myPlayer = api.getAuthorById(testPlayer.getUniqueId());
        assertFalse(myPlayer.hasPermission("test.permission"));

        // Now release the thread.
        scheduler.releaseTask();
        waiter.await();

        assertFalse(myPlayer.hasPermission("test.permission"));

    }

}