package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.bukkit.PwnFilterPlugin;
import com.pwn9.PwnFilter.rules.action.RegisterActions;
import com.pwn9.PwnFilter.util.LogManager;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.jetbrains.annotations.NotNull;
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
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Tests for RuleSets
 * User: ptoal
 * Date: 13-05-04
 * Time: 11:28 AM
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PwnFilterPlugin.class})
public class RuleSetTest {

    RuleManager ruleManager;
    RuleChain rs;
    PwnFilterPlugin mockPlugin;
    LogManager pwnLogger;
    FilterClient mockClient = new FilterClient() {
        public String getShortName() { return "TEST"; }
        public RuleChain getRuleChain() { return ruleManager.getRuleChain("testrules.txt");}
        public boolean isActive() { return true; }
        public void activate() {}
        public void shutdown() {}
    };
    MessageAuthor author = new MessageAuthor() {
        @Override
        public boolean hasPermission(String permString) {
            return false;
        }

        @NotNull
        @Override
        public String getName() {
            return "";
        }

        @NotNull
        @Override
        public UUID getID() {
            return UUID.randomUUID();
        }

        @Override
        public void sendMessage(String message) {

        }
    };

    @Before
    public void setUp() throws Exception {
        RegisterActions.all();
        //TODO: Remove this, and add a FilterEngine initialization call.
        PowerMock.mockStatic(PwnFilterPlugin.class);
        mockPlugin = PowerMock.createMock(PwnFilterPlugin.class);
        EasyMock.expect(PwnFilterPlugin.getInstance()).andReturn(mockPlugin).anyTimes();
        EasyMock.expect(mockPlugin.getBufferedReader("testfile.txt"))
                .andReturn(new BufferedReader(new StringReader("test"))).anyTimes();
        PowerMock.replay(PwnFilterPlugin.class);
        PowerMock.replay(mockPlugin);
        ruleManager = RuleManager.init(mockPlugin);
        File testFile = new File(getClass().getResource("/testrules.txt").getFile());
        ruleManager.setRuleDir(testFile.getParent());
        rs = ruleManager.getRuleChain("testrules.txt");
        Logger logger = Logger.getAnonymousLogger();
        pwnLogger = LogManager.getInstance(logger, new File("/tmp/"));
        rs.loadConfigFile();
    }

    @Test
    public void testApplyRules() {
        rs.loadConfigFile();
        FilterState testState = new FilterState("This is a test", author, mockClient);
        rs.apply(testState);
        assertEquals("This WAS a test", testState.getModifiedMessage().toString());
    }

    @Test
    public void testDollarSignInMessage() {
        rs.loadConfigFile();
        FilterState testState = new FilterState("notATestPerson {test] $ (test 2}",author,mockClient);
        rs.apply(testState);
    }

    // DBO Ticket # 13
    @Test
    public void testBackslashAtEndOfLine() {
        try {
            rs.loadConfigFile();
            FilterState testState = new FilterState("Message that ends with \\",author,mockClient);
            rs.apply(testState);
        } catch (StringIndexOutOfBoundsException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testShortcuts() {
        RuleChain ruleChain = ruleManager.getRuleChain("shortcutTest.txt");
        ruleChain.loadConfigFile();
        FilterState testState = new FilterState("ShortCutPattern",author,mockClient);
        ruleChain.apply(testState);
        Assert.assertEquals("Replaced", testState.getModifiedMessage().toString());
    }

    @After
    public void tearDown() throws Exception {
    }


}
