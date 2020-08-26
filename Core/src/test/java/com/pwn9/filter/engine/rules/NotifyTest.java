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

import com.pwn9.filter.engine.FilterContext;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.FilterServiceImpl;
import com.pwn9.filter.engine.api.FilterContextImpl;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import com.pwn9.filter.util.tag.RegisterTags;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NotifyTest {

    private final TestAuthor author = new TestAuthor();
    private RuleChain rs;
    private final FilterService filterServiceImpl = new FilterServiceImpl();

    @BeforeClass
    public static void init() {
        RegisterTags.all();
    }

    @Before
    public void setUp() {
        filterServiceImpl.getActionFactory().addActionTokens(MinecraftAction.class);
        filterServiceImpl.getActionFactory().addActionTokens(TargetedAction.class);
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        filterServiceImpl.getConfig().setRulesDir(rulesDir);
        try {
            rs = filterServiceImpl.parseRules(new File(rulesDir, "notifyTests.txt"));
        } catch (InvalidChainException ex) {
            fail();
        }
    }

    @Test
    public void testNotifyAddsMessage() {
        FilterContextImpl testState = new FilterContextImpl("foo", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertEquals(testState.getNotifyMessages().get("pwnfilter.admins"),
                "Player " + author.getName() + " has broken " + testState.getMatchedRules().get(0).getId());
    }

    @Test
    public void testNotifyOnlyKeepsLastMessage() {
        FilterContextImpl testState = new FilterContextImpl("foo bar", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertEquals(testState.getNotifyMessages().get("pwnfilter.admins"),
                "Player " + author.getName() + " has broken " + testState.getMatchedRules().get(1).getId());
    }

    @Test
    public void testNotifySendsOneMessagePerPerm() {
        FilterContextImpl testState = new FilterContextImpl("foo bar baz", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertEquals(testState.getNotifyMessages().get("pwnfilter.admins"),
                "Player " + author.getName() + " has broken " + testState.getMatchedRules().get(1).getId());
        assertEquals(testState.getNotifyMessages().get("pwnfilter.baz"),
                "Player " + author.getName() + " has broken " + testState.getMatchedRules().get(2).getId());
        assertEquals(testState.getNotifyMessages().size(), 2);
    }

    @Test
    public void testNotifySentAtEndOfProcessing() {
        FilterContext testState = new FilterContextImpl("foo bar baz", author, new TestClient());
        TestNotifier target = new TestNotifier();
        filterServiceImpl.registerNotifyTarget(target);
        rs.execute(testState, filterServiceImpl);
        assertEquals(testState.getNotifyMessages().get("pwnfilter.admins"), target.getNotification("pwnfilter.admins"));
        assertEquals(testState.getNotifyMessages().get("pwnfilter.baz"), target.getNotification("pwnfilter.baz"));

    }

}
