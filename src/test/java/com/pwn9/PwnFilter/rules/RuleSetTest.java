package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.util.LogManager;
import junit.framework.Assert;
import org.bukkit.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Tests for RuleSets
 * User: ptoal
 * Date: 13-05-04
 * Time: 11:28 AM
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PwnFilter.class})
public class RuleSetTest {

    RuleManager ruleManager;
    RuleChain rs;
    PwnFilter mockPlugin;
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
        PowerMock.mockStatic(PwnFilter.class);
        mockPlugin = PowerMock.createMock(PwnFilter.class);
        EasyMock.expect(PwnFilter.getInstance()).andReturn(mockPlugin).anyTimes();
        EasyMock.expect(mockPlugin.getBufferedReader("testfile.txt"))
                .andReturn(new BufferedReader(new StringReader("test"))).anyTimes();
        PowerMock.replay(PwnFilter.class);
        PowerMock.replay(mockPlugin);
        ruleManager = RuleManager.init(mockPlugin);
        File testFile = new File(getClass().getResource("/testrules.txt").getFile());
        ruleManager.setRuleDir(testFile.getParent());
        rs = ruleManager.getRuleChain("testrules.txt");
        Logger logger = Logger.getAnonymousLogger();
        pwnLogger = LogManager.getInstance(logger, new File("/tmp/"));
        DataCache.init(mockPlugin);
        rs.loadConfigFile();
    }

    @Test
    public void testApplyRules() {
        rs.loadConfigFile();
        FilterState testState = new FilterState(mockPlugin,"This is a test", null, mockClient);
        rs.apply(testState);
        assertEquals("This WAS a test", testState.getModifiedMessage().getPlainString());
    }

    @Test
    public void testDollarSignInMessage() {
        rs.loadConfigFile();
        FilterState testState = new FilterState(mockPlugin,"notATestPerson {test] $ (test 2}",null,mockClient);
        rs.apply(testState);
    }

    // DBO Ticket # 13
    @Test
    public void testBackslashAtEndOfLine() {
        try {
            rs.loadConfigFile();
            FilterState testState = new FilterState(mockPlugin,"Message that ends with \\",null,mockClient);
            rs.apply(testState);
        } catch (StringIndexOutOfBoundsException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testShortcuts() {
        RuleChain ruleChain = ruleManager.getRuleChain("shortcutTest.txt");
        ruleChain.loadConfigFile();
        FilterState testState = new FilterState(mockPlugin,"ShortCutPattern",null,mockClient);
        ruleChain.apply(testState);
        Assert.assertEquals("Replaced", testState.getModifiedMessage().getPlainString());
    }

    @After
    public void tearDown() throws Exception {
    }


}
