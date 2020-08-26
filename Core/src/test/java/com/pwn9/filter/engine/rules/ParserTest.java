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
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.rules.action.core.Deny;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.Respond;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.engine.rules.chain.ChainEntry;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for RuleSets
 * User: Sage905
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class ParserTest {

    private RuleChain rs;
    private final FilterService filterServiceImpl = new FilterServiceImpl();
    private final File testFile = new File(getClass().getResource("/testrules.txt").getFile());
    private final File parentDir = new File(testFile.getParent());

    @Before
    public void setUp() {
        filterServiceImpl.getActionFactory().addActionTokens(MinecraftAction.class);
        filterServiceImpl.getActionFactory().addActionTokens(TargetedAction.class);
        filterServiceImpl.getConfig().setRulesDir(parentDir);
        filterServiceImpl.getConfig().setTextDir(parentDir);
        try {
            rs = filterServiceImpl.parseRules(testFile);
            assertNotNull(rs);
        } catch (InvalidChainException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testSimple() throws InvalidChainException {
        File simpleFile = new File(getClass().getResource("/rules/simpleTest.txt").getFile());
        RuleChain simple = filterServiceImpl.parseRules(simpleFile);
        Assert.assertEquals(simple.ruleCount(), 1);
    }

    @Test
    public void testShortcuts() {
        List<ChainEntry> ruleChain = rs.getChain();
        for (ChainEntry e : ruleChain) {
            if (e.toString().equals("ShortCutPattern")) {
                return;
            }
        }
        Assert.fail("Shortcut was not applied to rule!");
    }

    @Test
    // Requires actiongroup.txt in the resources folder.
    public void testActionGroupParser() throws IOException {
        File actionFile = new File(parentDir, "actiongroup.txt");
        try {
            RuleChain ruleChain = filterServiceImpl.parseRules(actionFile);
            assertNotNull(ruleChain);

            assertTrue(ruleChain.getActionGroups().containsKey("aGroupTest"));

            RuleImpl rule = (RuleImpl) ruleChain.getChain().get(0);
            List<Action> actionList = rule.getActions();

            assertTrue(actionList.remove(0) instanceof Deny);
            assertTrue(actionList.remove(0) instanceof Respond);

        } catch (InvalidChainException e) {
            fail(e.getMessage());
        }

    }

    @Test
    // Requires conditiongroup.txt in the resources folder.
    public void testConditionGroupParser() throws IOException {
        File conditionFile = new File(parentDir, "conditiongroup.txt");
        try {
            RuleChain ruleChain = filterServiceImpl.parseRules(conditionFile);
            assertNotNull(ruleChain);

            assertTrue(ruleChain.getConditionGroups().containsKey("cGroupTest"));

            RuleImpl rule = (RuleImpl) ruleChain.getChain().get(0);
            List<Condition> conditionList = rule.getConditions();

            Condition cTest = conditionList.remove(0);
            assertEquals(cTest.getFlag(), Condition.CondFlag.require);
            assertEquals(cTest.getType(), Condition.CondType.permission);
            assertEquals(cTest.getParameters(), "pwnfilter.test");

            cTest = conditionList.remove(0);
            assertEquals(cTest.getFlag(), Condition.CondFlag.ignore);
            assertEquals(cTest.getType(), Condition.CondType.user);
            assertEquals(cTest.getParameters(), "Sage905");

            cTest = conditionList.remove(0);
            assertEquals(cTest.getFlag(), Condition.CondFlag.ignore);
            assertEquals(cTest.getType(), Condition.CondType.string);
            assertEquals(cTest.getParameters(), "derp");

            cTest = conditionList.remove(0);
            assertEquals(cTest.getFlag(), Condition.CondFlag.ignore);
            assertEquals(cTest.getType(), Condition.CondType.command);
            assertEquals(cTest.getParameters(), "me");
        } catch (InvalidChainException e) {
            fail(e.getMessage());
        }
    }


}
