package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test Conditions
 * User: Sage905
 * Date: 13-05-04
 * Time: 11:30 AM
 */
public class ConditionTest {

    RuleChain rs;
    FilterService filterService = new FilterService(new TestStatsTracker());
    final MessageAuthor author = new TestAuthor();
    File testFile = new File(getClass().getResource("/conditionTests.txt").getFile());
    File parentDir = new File(testFile.getParent());


    @Before
    public void setUp() {
        // For debugging purposes
//        filterService.setLogFileHandler(new File("/tmp/pwnfilter.log"));
//        filterService.setLevel(Level.FINEST);
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        filterService.getConfig().setRulesDir(parentDir);
        try {
            rs = filterService.parseRules(testFile);
        } catch (InvalidChainException e) {
            fail();
        }
    }

    @Test
    public void testIgnoreString() {
        FilterContext testState = new FilterContext("Ignore string baseline test.", author, new TestClient());
        rs.execute(testState, filterService);
        assertEquals("Ignore replaced baseline test.", testState.getModifiedMessage().toString());
        FilterContext state2 = new FilterContext("Ignore string qwerty test.", author, new TestClient());
        rs.execute(state2, filterService);
        assertEquals("Ignore string qwerty test.",state2.getModifiedMessage().toString());

    }

    @Test
    public void testIgnoreCommand() {
        FilterContext testState1 = new FilterContext("Ignore baseline command test", author, new TestClient());
        rs.execute(testState1, filterService);
        assertEquals("Ignore baseline replace command", testState1.getModifiedMessage().toString());

        FilterContext testState2 = new FilterContext("/tell Ignore command test", author, new TestClient("COMMAND"));
        rs.execute(testState2, filterService);
        assertEquals("/tell Ignore command test",testState2.getModifiedMessage().toString());
    }

    @Test
    public void testIgnoreDoesntMatch() {
        FilterContext testState2 = new FilterContext("testestest banned", author, new TestClient());
        rs.execute(testState2, filterService);
        assertEquals("testestest matched",testState2.getModifiedMessage().toString());
    }

    @Test
    public void testComandConditionOnlyMatchesCommandHandler() {
        FilterContext testState = new FilterContext("tell banned", author, new TestClient());
        rs.execute(testState, filterService);
        assertEquals("tell matched",testState.getModifiedMessage().toString());
        FilterContext testState2 = new FilterContext("tell banned", author, new TestClient("COMMAND"));
        rs.execute(testState2, filterService);
        assertEquals("tell banned", testState2.getModifiedMessage().toString());
    }



}
