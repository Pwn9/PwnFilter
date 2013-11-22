/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.rules.action.Actiondeny;
import com.pwn9.PwnFilter.rules.action.Actionrespond;
import com.pwn9.PwnFilter.util.LogManager;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for RuleSets
 * User: ptoal
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class ParserTest {

    RuleManager ruleManager;
    RuleChain rs;
    LogManager pwnLogger;
//    FilterClient mockClient = new FilterClient() {
//        public String getShortName() { return "TEST"; }
//        public RuleChain getRuleChain() { return ruleManager.getRuleChain("testrules.txt");}
//        public boolean isActive() { return true; }
//        public void activate(Configuration config) {}
//        public void shutdown() {}
//    };

    @Before
    public void setUp() throws Exception {
        ruleManager = RuleManager.getInstance();
        File testFile = new File(getClass().getResource("/testrules.txt").getFile());
        ruleManager.setRuleDir(testFile.getParent());
        Logger logger = Logger.getLogger("PwnFilter");
        pwnLogger = LogManager.getInstance(logger, new File("/tmp/"));
        //pwnLogger.start(); logger.setLevel(Level.FINEST);pwnLogger.debugMode = LogManager.DebugModes.high; // For debugging
        DataCache.getInstance();

    }

    @Test
    public void testLoadRules() {
        rs = ruleManager.getRuleChain("testrules.txt");
        assertTrue(rs.loadConfigFile());
    }

    @Test
    public void testShortcuts() {
        rs = ruleManager.getRuleChain("testrules.txt");
        rs.loadConfigFile();
        ArrayList<ChainEntry> ruleChain = rs.getChain();
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
        RuleChain ruleChain = ruleManager.getRuleChain("actiongroup.txt");
        ruleChain.loadConfigFile();

        assertTrue(ruleChain.getActionGroups().containsKey("aGroupTest"));

        Rule rule = (Rule)ruleChain.getChain().get(0);
        List<Action> actionList = rule.getActions();

        assertTrue(actionList.remove(0) instanceof Actiondeny);
        assertTrue(actionList.remove(0) instanceof Actionrespond);
    }

    @Test
    // Requires conditiongroup.txt in the resources folder.
    public void testConditionGroupParser() throws IOException {
        RuleChain ruleChain = ruleManager.getRuleChain("conditiongroup.txt");
        ruleChain.loadConfigFile();

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
    }

    @After
    public void tearDown() throws Exception {
     // Anything?  Probably not.
    }


}
