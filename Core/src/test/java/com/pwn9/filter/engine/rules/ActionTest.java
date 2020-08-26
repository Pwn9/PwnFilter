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
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class ActionTest {

    private final TestAuthor author = new TestAuthor();
    private RuleChain rs;
    private final FilterService filterServiceImpl = new FilterServiceImpl();

    @Before
    public void setUp() {
        filterServiceImpl.getActionFactory().addActionTokens(MinecraftAction.class);
        filterServiceImpl.getActionFactory().addActionTokens(TargetedAction.class);
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        filterServiceImpl.getConfig().setRulesDir(rulesDir);
        //filterService.getConfig().setTextDir(new File(getClass().getResource("/textfiles").getFile()));
        try {
            rs = filterServiceImpl.parseRules(new File(rulesDir, "actionTests.txt"));
        } catch (InvalidChainException ex) {
            fail();
        }
    }

    @Test
    public void testAbort() {
        FilterContextImpl testState = new FilterContextImpl("abort", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertTrue(testState.isAborted());
    }

    @Test
    public void testRandRep() {
        FilterContextImpl testState = new FilterContextImpl("randrep", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertTrue(testState.getModifiedMessage().toString().matches("(random|replace)"));
    }

    @Test
    public void testBurn() {
        FilterContextImpl testState = new FilterContextImpl("burn", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        Assert.assertTrue(author.burnt());
    }

    @Test
    public void testUpper() {
        FilterContextImpl testState = new FilterContextImpl("upper", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertEquals("UPPER", testState.getModifiedMessage().toString());
    }

    @Test
    public void testLower() {
        FilterContextImpl testState = new FilterContextImpl("LOWER", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertEquals("lower", testState.getModifiedMessage().toString());

        FilterContextImpl test2 = new FilterContextImpl("LOWERCASE ALL THIS STUFF!", author, new TestClient());
        rs.execute(test2, filterServiceImpl);
        assertEquals("lowercase all this stuff!", test2.getModifiedMessage().toString());
    }

// This doesn't work reliably on Jenkins, presumably because the encoding changes
//    @Test
//    public void testReadWindowsEncodedFile() throws InvalidActionException {
//        try {
//            filterService.getActionFactory().getAction("respondfile", "colors.txt");
//        } catch (InvalidActionException ex) {
//            assertTrue(ex.getCause() instanceof MalformedInputException);
//        }
//    }

}
