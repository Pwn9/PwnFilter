package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.rules.RuleSet;
import com.pwn9.PwnFilter.util.ColoredString;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ptoal
 * Date: 13-04-04
 * Time: 12:37 PM
 */

@RunWith(PowerMockRunner.class)
public class FilterTest extends TestCase {

    Logger logger;

    public FilterTest() {
        logger = Logger.getLogger("Test");
    }

    @Test
    public void testLoadRules() {

        PwnFilter p = new PwnFilter();
        p.logger = logger;

        RuleSet rs = new RuleSet(p);
        InputStreamReader ruleStream = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testrules.txt"));

        assertTrue(rs.loadRules(ruleStream));

    }

    @Test
    public void testColoredString() {
        String testString = "§7This §9is§l the §1string§3 under test.";
        String plainString = "This is the string under test.";
        char [] codeArray = new char[]{'7',0,0,0,0,'9',0,'l',0,0,0,0,'1',0,0,0,0,0,'3',0,0,0,0,0,0,0,0,0,0,0};

        ColoredString cs = new ColoredString(testString);
        // Check length() method
        assertEquals(cs.length(),plainString.length());
        // Check charAt() method
        assertEquals(cs.charAt(5),'i');
        // Check subSequence method
        assertEquals("is th",cs.subSequence(5,10));

        // Basic setup tests
        assertEquals(testString, cs.getColoredString());
        assertEquals(plainString, cs.getPlainString());
        assertEquals(testString, cs.toString());
        assertArrayEquals(codeArray, cs.getCodeArray());


        // Replace with plain text
        Pattern p = Pattern.compile("test");
        cs.replaceText(p,"derp");
        assertEquals("This is the string under derp.",cs.getPlainString());
        assertEquals("§7This §9is§l the §1string§3 under derp.",cs.getColoredString());

        // Replace with colored text
        p = Pattern.compile("string");
        cs.replaceText(p,"§3r§4a§5i§6n§7b§8o§9w");
        assertEquals("This is the rainbow under derp.", cs.getPlainString());
        assertEquals("§7This §9is§l the §3r§4a§5i§6n§7b§8o§9w§3 under derp.", cs.getColoredString());

    }
}
