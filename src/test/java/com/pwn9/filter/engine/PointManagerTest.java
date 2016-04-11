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

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.TestAuthor;
import com.pwn9.filter.engine.rules.TestClient;
import com.pwn9.filter.engine.rules.TestStatsTracker;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for the Point Manager
 * Created by Sage905 on 2016-04-10.
 */
public class PointManagerTest {
    @Test
    public void testPointsLeakEachInterval() throws Exception {
        FilterService filterService = new FilterService(new TestStatsTracker());
        PointManager pm = filterService.getPointManager();
        UUID author = UUID.randomUUID();
        pm.setLeakPoints(4d);
        pm.setPoints(author, 6d);
        assertEquals(pm.getPoints(author), new Double(6));
        pm.leakTask(pm);
        assertEquals(pm.getPoints(author), new Double(2));
        pm.leakTask(pm);
        assertFalse(pm.getPointsMap().contains(author));
    }

    @Test
    public void testPointsExecutesAscending() throws Exception {
        // TODO: Implement tests
    }

    @Test
    public void testPointsExecutesDescending() throws Exception {
        // TODO: Implement tests
    }

    @Test
    public void testPointsAction() throws Exception {
        FilterService filterService = new FilterService(new TestStatsTracker());
        PointManager pm = filterService.getPointManager();
        MessageAuthor author = new TestAuthor();
        assertEquals(pm.getPoints(author),new Double(0));
        Action pointsAction = filterService.getActionFactory().getAction("points","7.0 Test");
        pointsAction.execute(new FilterContext("test", author, new TestClient("Test")),filterService);
        assertEquals(pm.getPoints(author),new Double(7));
    }
}