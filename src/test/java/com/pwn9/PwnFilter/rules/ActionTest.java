package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterTask;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.config.FilterConfig;
import com.pwn9.PwnFilter.rules.action.RegisterActions;
import com.pwn9.PwnFilter.util.LogManager;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for Actions
 * User: ptoal
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class ActionTest {

    private RuleManager ruleManager;
    RuleChain rs;
    final FilterClient mockClient = new FilterClient() {
        public String getShortName() { return "ACTIONTEST"; }
        public RuleChain getRuleChain() { return ruleManager.getRuleChain("rules/actionTests.txt");}
        public boolean isActive() { return true; }
        public void activate() {}
        public void shutdown() {}
    };

    private final MessageAuthor author = new MessageAuthor() {
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

        @Override
        public void sendMessages(List<String> messages) {

        }
    };

    @Before
    public void setUp() {
        RegisterActions.all();
        ruleManager = RuleManager.getInstance();
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        FilterConfig.getInstance().setRulesDir(rulesDir);
        rs = ruleManager.getRuleChain("actionTests.txt");
        LogManager.getInstance(Logger.getAnonymousLogger(), new File("/tmp/"));
        rs.loadConfigFile();
    }

    @Test
    public void testAbort() {
        FilterTask testState = new FilterTask("abort", author, mockClient);
        rs.apply(testState);
        assertTrue(testState.isAborted());
    }

    @Test
    public void testRandRep() {
        FilterTask testState = new FilterTask("randrep", author, mockClient);
        rs.apply(testState);
        assertTrue(testState.getModifiedMessage().toString().matches("(random|replace)"));
    }

    @Test
    public void testBurn() {
        FilterTask testState = new FilterTask("burn", author, mockClient);
        rs.apply(testState);
    }

    @Test
    public void testUpper() {
        FilterTask testState = new FilterTask("upper", author, mockClient);
        rs.apply(testState);
        assertEquals("UPPER", testState.getModifiedMessage().toString());
    }

    @Test
    public void testLower() {
        FilterTask testState = new FilterTask("LOWER", author, mockClient);
        rs.apply(testState);
        assertEquals("lower", testState.getModifiedMessage().toString());

        FilterTask test2 = new FilterTask("LOWERCASE ALL THIS STUFF!", author, mockClient);
        rs.apply((test2));
        assertEquals("lowercase all this stuff!", test2.getModifiedMessage().toString());
    }


}
