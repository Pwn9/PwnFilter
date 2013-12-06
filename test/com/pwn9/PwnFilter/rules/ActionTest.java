package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.logging.Logger;

import static junit.framework.Assert.assertTrue;

/**
 * Tests for Actions
 * User: ptoal
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class ActionTest {

    RuleManager ruleManager;
    RuleChain rs;
    PwnFilter mockPlugin = new PwnFilter();
    LogManager pwnLogger;
    FilterClient mockClient = new FilterClient() {
        public String getShortName() { return "ACTIONTEST"; }
        public RuleChain getRuleChain() { return ruleManager.getRuleChain("actionTests.txt");}
        public boolean isActive() { return true; }
        public void activate(Configuration config) {}
        public void shutdown() {}
    };

    @Before
    public void setUp() throws Exception {
        ruleManager = RuleManager.getInstance();
        File testFile = new File(getClass().getResource("/actionTests.txt").getFile());
        ruleManager.setRuleDir(testFile.getParent());
        rs = ruleManager.getRuleChain("actionTests.txt");
        Logger logger = Logger.getAnonymousLogger();
        pwnLogger = LogManager.getInstance(logger, new File("/tmp/"));
        DataCache.getInstance();
        rs.loadConfigFile();
    }

    @Test
    public void testAbort() {
        FilterState testState = new FilterState(mockPlugin,"abort", null, mockClient);
        rs.apply(testState);
        assertTrue(testState.stop);
    }

    @Test
    public void testRandRep() {
        FilterState testState = new FilterState(mockPlugin,"randrep", null, mockClient);
        rs.apply(testState);
        System.out.println(testState.getModifiedMessage().getPlainString());
        assertTrue(testState.getModifiedMessage().getPlainString().matches("(random|replace)"));
    }

    @Test
    public void testBurn() {
        FilterState testState = new FilterState(mockPlugin,"burn", null, mockClient);
        rs.apply(testState);
    }

    @After
    public void tearDown() throws Exception {
        // TODO: Anything?  Probably not.
    }


}
