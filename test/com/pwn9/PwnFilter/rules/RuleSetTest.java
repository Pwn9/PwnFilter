package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for RuleSets
 * User: ptoal
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class RuleSetTest {

    RuleManager ruleManager;
    RuleChain rs;
    Player mockPlayer;
    PwnFilter mockPlugin;

    @Before
    public void setUp() throws Exception {
        ruleManager = RuleManager.getInstance();
        File testFile = new File(this.getClass().getClassLoader().getResource("testrules.txt").getPath());
        ruleManager.setRuleDir(new File(testFile.getParent()));
        rs = ruleManager.getRuleChain("testrules.txt");
    }

    @Test
    public void testLoadRules() {
        assertTrue(rs.loadConfigFile());
    }

    @Test
    public void testApplyRules() {
        rs.loadConfigFile();
        FilterState testState = new FilterState(mockPlugin,"This is a test", null, PwnFilter.EventType.CHAT);
        rs.apply(testState);
        System.out.println(rs.ruleCount());
        assertEquals("This WAS a test", testState.message.getPlainString());
    }

    @After
    public void tearDown() throws Exception {
     // TODO: Anything?  Probably not.
    }


}
