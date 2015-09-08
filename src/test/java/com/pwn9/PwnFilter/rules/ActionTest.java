package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.bukkit.PwnFilterPlugin;
import com.pwn9.PwnFilter.rules.action.RegisterActions;
import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.configuration.Configuration;
import org.easymock.EasyMock;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.UUID;
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
    PwnFilterPlugin mockPlugin;
    LogManager pwnLogger;
    FilterClient mockClient = new FilterClient() {
        public String getShortName() { return "ACTIONTEST"; }
        public RuleChain getRuleChain() { return ruleManager.getRuleChain("actionTests.txt");}
        public boolean isActive() { return true; }
        public void activate(Configuration config) {}
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
        mockPlugin = EasyMock.createMock(PwnFilterPlugin.class);
        ruleManager = RuleManager.init(mockPlugin);
        File testFile = new File(getClass().getResource("/actionTests.txt").getFile());
        ruleManager.setRuleDir(testFile.getParent());
        rs = ruleManager.getRuleChain("actionTests.txt");
        Logger logger = Logger.getAnonymousLogger();
        pwnLogger = LogManager.getInstance(logger, new File("/tmp/"));
        rs.loadConfigFile();
    }

    @Test
    public void testAbort() {
        FilterState testState = new FilterState(mockPlugin,"abort", author, mockClient);
        rs.apply(testState);
        assertTrue(testState.stop);
    }

    @Test
    public void testRandRep() {
        FilterState testState = new FilterState(mockPlugin,"randrep", author, mockClient);
        rs.apply(testState);
        System.out.println(testState.getModifiedMessage().getPlainString());
        assertTrue(testState.getModifiedMessage().getPlainString().matches("(random|replace)"));
    }

    @Test
    public void testBurn() {
        FilterState testState = new FilterState(mockPlugin,"burn", author, mockClient);
        rs.apply(testState);
    }

    @After
    public void tearDown() throws Exception {
    }


}
