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
import com.pwn9.filter.engine.FilterServiceImpl;
import com.pwn9.filter.engine.api.FilterContextImpl;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test Conditions
 */
public class ConditionTest {

    private final MessageAuthor author = new TestAuthor();
    private RuleChain rs;
    private final FilterServiceImpl filterServiceImpl = new FilterServiceImpl();
    private final File testFile = new File(getClass().getResource("/conditionTests.txt").getFile());
    private final File parentDir = new File(testFile.getParent());


    @Before
    public void setUp() {
        // For debugging purposes
//        filterService.setLogFileHandler(new File("/tmp/pwnfilter.log"));
//        filterService.setLevel(Level.FINEST);
        filterServiceImpl.getActionFactory().addActionTokens(MinecraftAction.class);
        filterServiceImpl.getActionFactory().addActionTokens(TargetedAction.class);
        filterServiceImpl.getConfig().setRulesDir(parentDir);
        try {
            rs = filterServiceImpl.parseRules(testFile);
        } catch (InvalidChainException e) {
            fail();
        }
    }

    @Test
    public void testIgnoreString() {
        FilterContext testState = new FilterContextImpl("Ignore string baseline test.", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertEquals("Ignore replaced baseline test.", testState.getModifiedMessage().toString());
        FilterContext state2 = new FilterContextImpl("Ignore string qwerty test.", author, new TestClient());
        rs.execute(state2, filterServiceImpl);
        assertEquals("Ignore string qwerty test.", state2.getModifiedMessage().toString());

    }

    @Test
    public void testIgnoreCommand() {
        FilterContextImpl testState1 = new FilterContextImpl("Ignore baseline command test", author, new TestClient());
        rs.execute(testState1, filterServiceImpl);
        assertEquals("Ignore baseline replace command", testState1.getModifiedMessage().toString());

        FilterContextImpl testState2 = new FilterContextImpl("/tell Ignore command test", author, new TestClient("COMMAND"));
        rs.execute(testState2, filterServiceImpl);
        assertEquals("/tell Ignore command test", testState2.getModifiedMessage().toString());
    }

    @Test
    public void testIgnoreDoesntMatch() {
        FilterContextImpl testState2 = new FilterContextImpl("testestest banned", author, new TestClient());
        rs.execute(testState2, filterServiceImpl);
        assertEquals("testestest matched", testState2.getModifiedMessage().toString());
    }

    @Test
    public void testComandConditionOnlyMatchesCommandHandler() {
        FilterContextImpl testState = new FilterContextImpl("tell banned", author, new TestClient());
        rs.execute(testState, filterServiceImpl);
        assertEquals("tell matched", testState.getModifiedMessage().toString());
        FilterContextImpl testState2 = new FilterContextImpl("tell banned", author, new TestClient("COMMAND"));
        rs.execute(testState2, filterServiceImpl);
        assertEquals("tell banned", testState2.getModifiedMessage().toString());
    }


}
