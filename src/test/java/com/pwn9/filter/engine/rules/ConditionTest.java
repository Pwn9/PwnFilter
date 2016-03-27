package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.config.FilterConfig;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.bukkit.listener.PwnFilterCommandListener;
import com.pwn9.filter.bukkit.listener.PwnFilterPlayerListener;
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

/**
 * Test Conditions
 * User: Sage905
 * Date: 13-05-04
 * Time: 11:30 AM
 */
public class ConditionTest {

    RuleManager ruleManager;
    RuleChain rs;
    PwnFilterPlugin mockPlugin;
    Logger pwnLogger;
    final FilterClient mockClient = new FilterClient() {
        public String getShortName() { return "CONDITIONTEST"; }
        public RuleChain getRuleChain() { return ruleManager.getRuleChain("conditionTests.txt");}
        public boolean isActive() { return true; }
        public void activate() {}
        public void shutdown() {}
    };
    final MessageAuthor author = new MessageAuthor() {
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
        //TODO: Remove this, and add a FilterService initialization call.
        ruleManager = RuleManager.getInstance();
        File testFile = new File(getClass().getResource("/conditionTests.txt").getFile());
        FilterConfig.getInstance().setRulesDir(testFile.getParentFile());
        rs = ruleManager.getRuleChain("conditionTests.txt");
        Logger logger = Logger.getAnonymousLogger();
        pwnLogger = FileLogger.getInstance(logger, new File("/tmp/"));
        rs.load();
    }

    @Test
    public void testIgnoreString() {
        FilterContext testState = new FilterContext("Ignore string baseline test.", author, mockClient);
        rs.apply(testState);
        assertEquals("Ignore replaced baseline test.", testState.getModifiedMessage().toString());
        FilterContext state2 = new FilterContext("Ignore string qwerty test.", author, mockClient);
        rs.apply(state2);
        assertEquals("Ignore string qwerty test.",state2.getModifiedMessage().toString());

    }

    @Test
    public void testIgnoreCommand() {
        FilterContext testState1 = new FilterContext("Ignore baseline command test", author, new PwnFilterCommandListener());
        rs.apply(testState1);
        assertEquals("Ignore baseline replace command", testState1.getModifiedMessage().toString());

        FilterContext testState2 = new FilterContext("/tell Ignore command test", author, new PwnFilterCommandListener());
        rs.apply(testState2);
        assertEquals("/tell Ignore command test",testState2.getModifiedMessage().toString());
    }

    @Test
    public void testIgnoreDoesntMatch() {
        FilterContext testState2 = new FilterContext("testestest banned", author, mockClient);
        rs.apply(testState2);
        assertEquals("testestest matched",testState2.getModifiedMessage().toString());
    }

    @Test
    public void testComandConditionOnlyMatchesCommandHandler() {
        FilterContext testState = new FilterContext("tell banned", author, new PwnFilterPlayerListener());
        rs.apply(testState);
        assertEquals("tell matched",testState.getModifiedMessage().toString());
        FilterContext testState2 = new FilterContext("tell banned", author, new PwnFilterCommandListener());
        rs.apply(testState2);
        assertEquals("tell banned", testState2.getModifiedMessage().toString());
    }



}
