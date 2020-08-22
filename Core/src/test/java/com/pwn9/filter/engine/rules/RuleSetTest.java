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
import com.pwn9.filter.engine.api.FilterContext;
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
    private final FilterService filterService = new FilterService();

    @Before
    public void setUp() {
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        File testRules = new File(getClass().getResource("/testrules.txt").getFile());
        File parentDir = testRules.getParentFile();
        filterService.getConfig().setRulesDir(parentDir);
        filterService.getConfig().setTextDir(parentDir);
        try {
            rs = filterService.parseRules(testRules);
            sc = filterService.parseRules(new File(parentDir, "shortcutTest.txt"));
        } catch (InvalidChainException ex) {
            fail();
        }
    }

    @Test
    public void testApplyRules() throws IOException {
        FilterContext testState = new FilterContext("This is a test", author, new TestClient());
        rs.execute(testState, filterService);
        assertEquals("This WAS a test", testState.getModifiedMessage().toString());
    }

    @Test
    public void testDollarSignInMessage() {
        FilterContext testState = new FilterContext("notATestPerson {test] $ (test 2}", author, new TestClient());
        rs.execute(testState, filterService);
    }

    // DBO Ticket # 13
    @Test
    public void testBackslashAtEndOfLine() {
        try {
            FilterContext testState = new FilterContext("Message that ends with \\", author, new TestClient());
            rs.execute(testState, filterService);
        } catch (StringIndexOutOfBoundsException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testShortcuts() {
        FilterContext testState = new FilterContext("ShortCutPattern", author, new TestClient());
        sc.execute(testState, filterService);
        Assert.assertEquals("Replaced", testState.getModifiedMessage().toString());
    }


}
