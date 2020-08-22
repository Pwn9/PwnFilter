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

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.FilterServiceImpl;
import com.pwn9.filter.engine.api.FilterContextImpl;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for RuleSets
 * User: Sage905
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class RuleSetTest {

    private final MessageAuthor author = new TestAuthor();
    private RuleChain rs, sc;
    private final FilterService filterServiceImpl = new FilterServiceImpl();

    @Before
    public void setUp() {
        filterServiceImpl.getActionFactory().addActionTokens(MinecraftAction.class);
        filterServiceImpl.getActionFactory().addActionTokens(TargetedAction.class);
        File testRules = new File(getClass().getResource("/testrules.txt").getFile());
        File parentDir = testRules.getParentFile();
        filterServiceImpl.getConfig().setRulesDir(parentDir);
        filterServiceImpl.getConfig().setTextDir(parentDir);
        try {
            rs = filterServiceImpl.parseRules(testRules);
            sc = filterServiceImpl.parseRules(new File(parentDir, "shortcutTest.txt"));
        } catch (InvalidChainException ex) {
            fail();
        }
    }

    @Test
    public void testApplyRules() throws IOException {
        FilterContextImpl testState = new FilterContextImpl("This is a test", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertEquals("This WAS a test", testState.getModifiedMessage().toString());
    }

    @Test
    public void testDollarSignInMessage() {
        FilterContextImpl testState = new FilterContextImpl("notATestPerson {test] $ (test 2}", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
    }

    // DBO Ticket # 13
    @Test
    public void testBackslashAtEndOfLine() {
        try {
            FilterContextImpl testState = new FilterContextImpl("Message that ends with \\", author, new TestClient());
            rs.execute(testState, filterServiceImpl);
        } catch (StringIndexOutOfBoundsException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testShortcuts() {
        FilterContextImpl testState = new FilterContextImpl("ShortCutPattern", author, new TestClient());
        sc.execute(testState, filterServiceImpl);
        Assert.assertEquals("Replaced", testState.getModifiedMessage().toString());
    }


}
