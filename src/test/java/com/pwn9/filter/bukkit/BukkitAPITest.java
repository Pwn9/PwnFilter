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

import com.pwn9.filter.MockPlayer;
import com.pwn9.filter.MockPlugin;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.rules.TestAuthor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Sage905 on 2016-04-22.
 */
public class BukkitAPITest {

    private PwnFilterPlugin testPlugin = new MockPlugin();

    @Before
    public void setup() {

        //TODO: This is potentially a test nightmare, because another test might
        // Load a different server object.  Maybe wrap the Bukkit class?

        if (Bukkit.getServer() == null ) {
            Bukkit.setServer(new MockServer());
        }
        FilterService filterService = testPlugin.getFilterService();
        filterService.registerAuthorService(uuid -> new TestAuthor());

    }
    @Test
    public void getAuthorByIdReturnsPlayer() throws Exception {
        MockServer server = (MockServer) Bukkit.getServer();
        server.setPrimaryThread(true);
        Player mockPlayer = new MockPlayer();
        server.setPlayer(mockPlayer);
        BukkitAPI api = new BukkitAPI(testPlugin);
        assertEquals(api.getAuthorById(mockPlayer.getUniqueId()).getId(),mockPlayer.getUniqueId());
    }

    @Test
    public void getAuthorByIdReturnsNullForNoMatch() throws Exception {
        MockServer server = (MockServer) Bukkit.getServer();
        server.setPrimaryThread(true);
        server.setPlayer(null);
        BukkitAPI api = new BukkitAPI(testPlugin);
        assertNull(api.getAuthorById(UUID.randomUUID()));
    }

//    @Test
//    public void notifyWithPerm() throws Exception {
//
//    }

}