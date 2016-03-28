/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.core.Deny;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.Respond;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.engine.rules.chain.ChainEntry;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Tests for RuleSets
 * User: Sage905
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class ParserTest {

    RuleChain rs, sc;
    FilterService filterService = new FilterService(new TestStatsTracker());
    Logger logger = filterService.getLogger();
    final MessageAuthor author = new TestAuthor();
    File testFile = new File(getClass().getResource("/testrules.txt").getFile());
    File parentDir = new File(testFile.getParent());

    @Before
    public void setUp() {
        // For debugging purposes
//        filterService.setLogFileHandler(new File("/tmp/pwnfilter.log"));
//        logger.setLevel(Level.FINEST);
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        filterService.getConfig().setRulesDir(parentDir);
        filterService.getConfig().setRulesDir(parentDir);
        try {
            rs = filterService.parseRules(testFile);
            assertNotNull(rs);
        } catch (InvalidChainException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testShortcuts() {
        List<ChainEntry> ruleChain = rs.getChain();
        for (ChainEntry e : ruleChain) {
            if(e.toString().equals("ShortCutPattern")) {
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
            RuleChain ruleChain = filterService.parseRules(actionFile);
            assertNotNull(ruleChain);

            assertTrue(ruleChain.getActionGroups().containsKey("aGroupTest"));

            Rule rule = (Rule)ruleChain.getChain().get(0);
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
            RuleChain ruleChain = filterService.parseRules(conditionFile);
            assertNotNull(ruleChain);

            assertTrue(ruleChain.getConditionGroups().containsKey("cGroupTest"));

            Rule rule = (Rule)ruleChain.getChain().get(0);
            List<Condition> conditionList = rule.getConditions();

            Condition cTest = conditionList.remove(0);
            assertEquals(cTest.flag, Condition.CondFlag.require);
            assertEquals(cTest.type, Condition.CondType.permission);
            assertEquals(cTest.parameters, "pwnfilter.test");

            cTest = conditionList.remove(0);
            assertEquals(cTest.flag, Condition.CondFlag.ignore);
            assertEquals(cTest.type, Condition.CondType.user);
            assertEquals(cTest.parameters, "Sage905");

            cTest = conditionList.remove(0);
            assertEquals(cTest.flag, Condition.CondFlag.ignore);
            assertEquals(cTest.type, Condition.CondType.string);
            assertEquals(cTest.parameters, "derp");

            cTest = conditionList.remove(0);
            assertEquals(cTest.flag, Condition.CondFlag.ignore);
            assertEquals(cTest.type, Condition.CondType.command);
            assertEquals(cTest.parameters, "me");
        } catch (InvalidChainException e) {
            fail(e.getMessage());
        }
    }



}
