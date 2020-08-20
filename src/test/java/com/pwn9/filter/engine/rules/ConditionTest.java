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
    private final FilterService filterService = new FilterService();
    private final File testFile = new File(getClass().getResource("/conditionTests.txt").getFile());
    private final File parentDir = new File(testFile.getParent());


    @Before
    public void setUp() {
        // For debugging purposes
//        filterService.setLogFileHandler(new File("/tmp/pwnfilter.log"));
//        filterService.setLevel(Level.FINEST);
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        filterService.getConfig().setRulesDir(parentDir);
        try {
            rs = filterService.parseRules(testFile);
        } catch (InvalidChainException e) {
            fail();
        }
    }

    @Test
    public void testIgnoreString() {
        FilterContext testState = new FilterContext("Ignore string baseline test.", author, new TestClient());
        rs.execute(testState, filterService);
        assertEquals("Ignore replaced baseline test.", testState.getModifiedMessage().toString());
        FilterContext state2 = new FilterContext("Ignore string qwerty test.", author, new TestClient());
        rs.execute(state2, filterService);
        assertEquals("Ignore string qwerty test.", state2.getModifiedMessage().toString());

    }

    @Test
    public void testIgnoreCommand() {
        FilterContext testState1 = new FilterContext("Ignore baseline command test", author, new TestClient());
        rs.execute(testState1, filterService);
        assertEquals("Ignore baseline replace command", testState1.getModifiedMessage().toString());

        FilterContext testState2 = new FilterContext("/tell Ignore command test", author, new TestClient("COMMAND"));
        rs.execute(testState2, filterService);
        assertEquals("/tell Ignore command test", testState2.getModifiedMessage().toString());
    }

    @Test
    public void testIgnoreDoesntMatch() {
        FilterContext testState2 = new FilterContext("testestest banned", author, new TestClient());
        rs.execute(testState2, filterService);
        assertEquals("testestest matched", testState2.getModifiedMessage().toString());
    }

    @Test
    public void testComandConditionOnlyMatchesCommandHandler() {
        FilterContext testState = new FilterContext("tell banned", author, new TestClient());
        rs.execute(testState, filterService);
        assertEquals("tell matched", testState.getModifiedMessage().toString());
        FilterContext testState2 = new FilterContext("tell banned", author, new TestClient("COMMAND"));
        rs.execute(testState2, filterService);
        assertEquals("tell banned", testState2.getModifiedMessage().toString());
    }


}
