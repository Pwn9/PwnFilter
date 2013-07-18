package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.rules.RuleSet;
import com.pwn9.PwnFilter.util.ColoredString;
import junit.framework.TestCase;
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
        PwnFilter.logger = logger;
    }

    public void testLoadRules() {

        PwnFilter p = new PwnFilter();


        RuleSet rs = new RuleSet(p);
        InputStreamReader ruleStream = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testrules.txt"));

        assertTrue(rs.loadRules(ruleStream));

    }

    public void testColoredString() {
        String testString = "§7This §9is§l the §1string§3 under test.";
        String plainString = "This is the string under test.";

        String[] codeArray = {"§7",null,null,null,null,"§9",null,"§l",
                null,null,null,null,"§1",null,null,null,null,null,"§3",null,null,
                null,null,null,null,null,null,null,null,null};

        ColoredString cs = new ColoredString(testString);
        // Check length() method
        assertEquals(cs.length(),plainString.length());
        // Check charAt() method
        assertEquals(cs.charAt(5),'i');
        // Check subSequence method
        assertEquals("is th",cs.subSequence(5,10));

        // Basic setup tests
        assertEquals(plainString, cs.getPlainString());
        assertArrayEquals(codeArray, cs.getCodeArray());
        assertEquals(testString, cs.getColoredString());
        assertEquals(testString, cs.toString());


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

    public void testColoredStringWithFormatting() {
        String test = "§9§l![MEMBERS]!:§r §aPlease §c§l/VOTE§r§a to get §9§l4 DIAMONDS§r§a /VOTE!";
        String plain = "![MEMBERS]!: Please /VOTE to get 4 DIAMONDS /VOTE!";
        Pattern p = Pattern.compile("-+m+e+m+b+e+r");

        ColoredString cs1 = new ColoredString(test);

        assertEquals(test,cs1.getColoredString());
        assertEquals(plain,cs1.getPlainString());

        ColoredString cs2 = new ColoredString("-member");

        cs2.replaceText(p,"§9§l![MEMBERS]!:§r §aPlease §c§l/VOTE§r§a to get §9§l4 DIAMONDS§r§a /VOTE!");
        assertEquals("§9§l![MEMBERS]!:§r §aPlease §c§l/VOTE§r§a to get §9§l4 DIAMONDS§r§a /VOTE!",cs2.getColoredString());
    }
}
