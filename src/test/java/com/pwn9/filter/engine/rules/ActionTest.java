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
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for Actions
 * User: Sage905
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class ActionTest {

    RuleChain rs;
    FilterService filterService = new FilterService(new TestStatsTracker());
    Logger logger = filterService.getLogger();
    final MessageAuthor author = new TestAuthor();

    @Before
    public void setUp() {
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        filterService.getConfig().setRulesDir(rulesDir);
        try {
            rs = filterService.parseRules(new File(rulesDir, "actionTests.txt"));
        } catch (InvalidChainException ex) {
            fail();
        }
    }

    @Test
    public void testAbort() {
        FilterContext testState = new FilterContext("abort", author, new TestClient());
        rs.execute(testState, logger);
        assertTrue(testState.isAborted());
    }

    @Test
    public void testRandRep() {
        FilterContext testState = new FilterContext("randrep", author, new TestClient());
        rs.execute(testState, logger);
        assertTrue(testState.getModifiedMessage().toString().matches("(random|replace)"));
    }

    @Test
    public void testBurn() {
        FilterContext testState = new FilterContext("burn", author, new TestClient());
        rs.execute(testState, logger);
    }

    @Test
    public void testUpper() {
        FilterContext testState = new FilterContext("upper", author, new TestClient());
        rs.execute(testState, logger);
        assertEquals("UPPER", testState.getModifiedMessage().toString());
    }

    @Test
    public void testLower() {
        FilterContext testState = new FilterContext("LOWER", author, new TestClient());
        rs.execute(testState, logger);
        assertEquals("lower", testState.getModifiedMessage().toString());

        FilterContext test2 = new FilterContext("LOWERCASE ALL THIS STUFF!", author, new TestClient());
        rs.execute(test2, logger);
        assertEquals("lowercase all this stuff!", test2.getModifiedMessage().toString());
    }


}
