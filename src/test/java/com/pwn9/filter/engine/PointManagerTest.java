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

package com.pwn9.filter.engine;

import com.pwn9.filter.MockPlugin;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.AuthorService;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.TestAction;
import com.pwn9.filter.engine.rules.TestAuthor;
import com.pwn9.filter.engine.rules.TestClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for the Point Manager
 * Created by Sage905 on 2016-04-10.
 */
public class PointManagerTest {

    private FilterService filterService;
    private PointManager pm;
    private TestAction ascending1, descending1, ascending2, descending2;
    private final AuthorService authorService = MockPlugin.getMockAuthorService();
    private final UUID authorId = UUID.randomUUID();

    @Before
    public void setup() {
        filterService = new FilterService();
        pm = filterService.getPointManager();
        pm.start();
        filterService.registerAuthorService(authorService);
        ascending1 = new TestAction();
        List<Action> ascendingList1 = Collections.singletonList(ascending1);
        descending1 = new TestAction();
        List<Action> descendingList1 = Collections.singletonList(descending1);
        pm.addThreshold("Level1", 10d, ascendingList1, descendingList1);

        ascending2 = new TestAction();
        List<Action> ascendingList2 = Collections.singletonList(ascending2);
        descending2 = new TestAction();
        List<Action> descendingList2 = Collections.singletonList(descending2);
        pm.addThreshold("Level1", 20d, ascendingList2, descendingList2);
    }

    @Test
    public void testPointsLeakEachInterval() throws Exception {
        pm.setLeakPoints(4d);
        pm.setPoints(authorId, 6d);
        assertEquals(pm.getPoints(authorId), new Double(6));
        pm.leakTask(pm);
        assertEquals(pm.getPoints(authorId), new Double(2));
        pm.leakTask(pm);
        assertFalse(pm.getPointsMap().contains(authorId));
    }

    @Test
    public void testPointsExecutesAscending() throws Exception {
        pm.setPoints(authorId, 0d);

        // Don't trigger a threshold
        pm.addPoints(authorId, 5d);
        assertEquals(0, ascending1.getCounter());
        assertEquals(0, ascending2.getCounter());
        assertEquals(0, descending1.getCounter());
        assertEquals(0, descending2.getCounter());

        // Trigger the first threshold on the way up
        pm.addPoints(authorId, 5d);
        assertEquals(1, ascending1.getCounter());
        assertEquals(0, ascending2.getCounter());
        assertEquals(0, descending1.getCounter());
        assertEquals(0, descending2.getCounter());

        // Trigger the second threshold on the way up
        pm.addPoints(authorId, 10d);
        assertEquals(1, ascending1.getCounter());
        assertEquals(1, ascending2.getCounter());
        assertEquals(0, descending1.getCounter());
        assertEquals(0, descending2.getCounter());

    }

    @Test
    public void testPointsExecutesDescending() throws Exception {
        pm.setPoints(authorId, 29d);

        // Don't trigger a threshold
        pm.subPoints(authorId, 5d);
        assertEquals(0, ascending1.getCounter());
        assertEquals(0, ascending2.getCounter());
        assertEquals(0, descending1.getCounter());
        assertEquals(0, descending2.getCounter());

        // Trigger the second threshold on the way down
        pm.subPoints(authorId, 5d);
        assertEquals(0, ascending1.getCounter());
        assertEquals(0, ascending2.getCounter());
        assertEquals(0, descending1.getCounter());
        assertEquals(1, descending2.getCounter());

        // Trigger the first threshold on the way down
        pm.subPoints(authorId, 10d);
        assertEquals(0, ascending1.getCounter());
        assertEquals(0, ascending2.getCounter());
        assertEquals(1, descending1.getCounter());
        assertEquals(1, descending2.getCounter());

    }

    @Test
    public void testPointsAction() throws Exception {
        MessageAuthor messageAuthor = new TestAuthor();
        assertEquals(pm.getPoints(messageAuthor), new Double(0));
        Action pointsAction = filterService.getActionFactory().getAction("points", "7.0 Test");
        pointsAction.execute(new FilterContext("test", messageAuthor, new TestClient("Test")), filterService);
        assertEquals(pm.getPoints(messageAuthor), new Double(7));
    }
}