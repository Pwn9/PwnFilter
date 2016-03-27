package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.config.FilterConfig;
import com.pwn9.filter.engine.rules.action.RegisterActions;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import com.pwn9.filter.util.FileLogger;
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
 * User: Sage905
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
        FileLogger.getInstance(Logger.getAnonymousLogger(), new File("/tmp/"));
        rs.load();
    }

    @Test
    public void testAbort() {
        FilterContext testState = new FilterContext("abort", author, mockClient);
        rs.apply(testState);
        assertTrue(testState.isAborted());
    }

    @Test
    public void testRandRep() {
        FilterContext testState = new FilterContext("randrep", author, mockClient);
        rs.apply(testState);
        assertTrue(testState.getModifiedMessage().toString().matches("(random|replace)"));
    }

    @Test
    public void testBurn() {
        FilterContext testState = new FilterContext("burn", author, mockClient);
        rs.apply(testState);
    }

    @Test
    public void testUpper() {
        FilterContext testState = new FilterContext("upper", author, mockClient);
        rs.apply(testState);
        assertEquals("UPPER", testState.getModifiedMessage().toString());
    }

    @Test
    public void testLower() {
        FilterContext testState = new FilterContext("LOWER", author, mockClient);
        rs.apply(testState);
        assertEquals("lower", testState.getModifiedMessage().toString());

        FilterContext test2 = new FilterContext("LOWERCASE ALL THIS STUFF!", author, mockClient);
        rs.apply((test2));
        assertEquals("lowercase all this stuff!", test2.getModifiedMessage().toString());
    }


}
