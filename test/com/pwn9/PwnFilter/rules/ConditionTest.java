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

import static junit.framework.Assert.assertEquals;

/**
 * Test Conditions
 * User: ptoal
 * Date: 13-05-04
 * Time: 11:30 AM
 */
public class ConditionTest {

    RuleManager ruleManager;
    RuleChain rs;
    PwnFilter mockPlugin = new PwnFilter();
    LogManager pwnLogger;
    FilterClient mockClient = new FilterClient() {
        public String getShortName() { return "CONDITIONTEST"; }
        public RuleChain getRuleChain() { return ruleManager.getRuleChain("conditionTests.txt");}
        public boolean isActive() { return true; }
        public void activate(Configuration config) {}
        public void shutdown() {}
    };

    @Before
    public void setUp() throws Exception {
        ruleManager = RuleManager.getInstance();
        File testFile = new File(getClass().getResource("/conditionTests.txt").getFile());
        ruleManager.setRuleDir(testFile.getParent());
        rs = ruleManager.getRuleChain("conditionTests.txt");
        Logger logger = Logger.getAnonymousLogger();
        pwnLogger = LogManager.getInstance(logger, new File("/tmp/"));
        DataCache.getInstance();
        rs.loadConfigFile();
    }

    @Test
    public void testIgnoreString() {
        FilterState testState = new FilterState(mockPlugin,"Ignore baseline test.", null, mockClient);
        rs.apply(testState);
        assertEquals("Pass baseline test.", testState.getModifiedMessage().getPlainString());
        FilterState state2 = new FilterState(mockPlugin,"Ignore qwerty test.", null, mockClient);
        rs.apply(state2);
        assertEquals("Ignore qwerty test.",state2.getModifiedMessage().getPlainString());

    }

    @After
    public void tearDown() throws Exception {
    }


}
