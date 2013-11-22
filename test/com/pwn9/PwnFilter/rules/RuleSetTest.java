package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.util.LogManager;
import junit.framework.Assert;
import org.bukkit.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Tests for RuleSets
 * User: ptoal
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class RuleSetTest {

    RuleManager ruleManager;
    RuleChain rs;
    PwnFilter mockPlugin = new PwnFilter();
    LogManager pwnLogger;
    FilterClient mockClient = new FilterClient() {
        public String getShortName() { return "TEST"; }
        public RuleChain getRuleChain() { return ruleManager.getRuleChain("testrules.txt");}
        public boolean isActive() { return true; }
        public void activate(Configuration config) {}
        public void shutdown() {}
    };

    @Before
    public void setUp() throws Exception {
        ruleManager = RuleManager.getInstance();
        File testFile = new File(getClass().getResource("/testrules.txt").getFile());
        ruleManager.setRuleDir(testFile.getParent());
        rs = ruleManager.getRuleChain("testrules.txt");
        Logger logger = Logger.getAnonymousLogger();
        pwnLogger = LogManager.getInstance(logger, new File("/tmp/"));
        DataCache.getInstance();
        rs.loadConfigFile();
    }

    @Test
    public void testApplyRules() {
        rs.loadConfigFile();
        FilterState testState = new FilterState(mockPlugin,"This is a test", null, mockClient);
        rs.apply(testState);
        System.out.println(rs.ruleCount());
        assertEquals("This WAS a test", testState.message.getPlainString());
    }

    @Test
    public void testShortcuts() {
        RuleChain ruleChain = ruleManager.getRuleChain("shortcutTest.txt");
        ruleChain.loadConfigFile();
        FilterState testState = new FilterState(mockPlugin,"ShortCutPattern",null,mockClient);
        ruleChain.apply(testState);
        Assert.assertEquals("Replaced", testState.message.getPlainString());
    }

    @After
    public void tearDown() throws Exception {
     // TODO: Anything?  Probably not.
    }


}
