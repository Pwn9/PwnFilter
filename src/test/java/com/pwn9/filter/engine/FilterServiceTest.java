/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine;

import com.pwn9.filter.engine.rules.TestClient;
import com.pwn9.filter.engine.rules.TestStatsTracker;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Sage905 on 2016-04-12.
 */
public class FilterServiceTest {

    private TestClient client;
    private FilterService service;

    @Before
    public void setup() {
        client = new TestClient();
        service = new FilterService(new TestStatsTracker());
    }

    @Test
    public void clientActivationDeactivation() throws Exception {
        service.registerClient(client);
        client.activate();
        assertTrue(service.getActiveClients().contains(client));
        service.shutdown();
        assertFalse(service.getActiveClients().contains(client));
        assertFalse(service.getRegisteredClients().contains(client));
    }

}