package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for RuleSets
 * User: Sage905
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class RuleSetTest {

    RuleChain rs, sc;
    FilterService filterService = new FilterService(new TestStatsTracker());
    Logger logger = filterService.getLogger();
    final MessageAuthor author = new TestAuthor();
    File parentDir;


    @Before
    public void setUp() {
        // For debugging purposes
//        filterService.setLogFileHandler(new File("/tmp/pwnfilter.log"));
//        logger.setLevel(Level.FINEST);
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        File testRules = new File(getClass().getResource("/testrules.txt").getFile());
        parentDir = testRules.getParentFile();
        filterService.getConfig().setRulesDir(parentDir);
        filterService.getConfig().setTextDir(parentDir);
        try {
            rs = filterService.parseRules(testRules);
            sc = filterService.parseRules(new File(parentDir, "shortcutTest.txt"));
        } catch (InvalidChainException ex) {
            fail();
        }
    }

    @Test
    public void testApplyRules() throws IOException {
        FilterContext testState = new FilterContext("This is a test", author, new TestClient());
        rs.execute(testState, filterService);
        assertEquals("This WAS a test", testState.getModifiedMessage().toString());
    }

    @Test
    public void testDollarSignInMessage() {
        FilterContext testState = new FilterContext("notATestPerson {test] $ (test 2}",author,new TestClient());
        rs.execute(testState, filterService);
    }

    // DBO Ticket # 13
    @Test
    public void testBackslashAtEndOfLine() {
        try {
            FilterContext testState = new FilterContext("Message that ends with \\",author,new TestClient());
            rs.execute(testState, filterService);
        } catch (StringIndexOutOfBoundsException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testShortcuts() {
        FilterContext testState = new FilterContext("ShortCutPattern",author,new TestClient());
        sc.execute(testState, filterService);
        Assert.assertEquals("Replaced", testState.getModifiedMessage().toString());
    }


}
