package com.pwn9.PwnFilter.util;

import junit.framework.TestCase;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests for the ColoredString object
 * User: ptoal
 * Date: 2013-12-02
 * Time: 9:59 AM
 */
public class ColoredStringTest extends TestCase {
    Logger logger;

    public ColoredStringTest() {
        logger = Logger.getLogger("Test");
        LogManager.logger = logger;
    }


    public void testColoredString() {
        String testString = "§7This §9is§l the §1string§3 under test.";
        String plainString = "This is the string under test.";

        String[] codeArray = {"§7",null,null,null,null,"§9",null,"§l",
                null,null,null,null,"§1",null,null,null,null,null,"§3",null,null,
                null,null,null,null,null,null,null,null,null,null};

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
        cs.replaceText(p, "derp");
        assertEquals("This is the string under derp.",cs.getPlainString());
        assertEquals("§7This §9is§l the §1string§3 under derp.",cs.getColoredString());

        // Replace with colored text
        p = Pattern.compile("string");
        cs.replaceText(p, "§3r§4a§5i§6n§7b§8o§9w");
        assertEquals("This is the rainbow under derp.", cs.getPlainString());
        assertEquals("§7This §9is§l the §1§3r§4a§5i§6n§7b§8o§9w§3 under derp.", cs.getColoredString());

    }

    public void testColoredStringWithDoubleFormatting() {
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

    /**
     * Test that a string that ends with a format code is properly represented
     */
    public void testColoredStringWithFormatAtEnd() {
        String test = "This is a string ending with format.§3";
        ColoredString cs = new ColoredString(test);
        assertEquals(cs.getColoredString(),test);
    }

    public void testReplaceWithFormatAtEnd() {
        String test = "Test string.§1";
        Pattern p = Pattern.compile("string");
        String replacement = "hello";

        ColoredString cs = new ColoredString(test);
        cs.replaceText(p,replacement);
        assertEquals("Test hello.§1",cs.getColoredString());
    }

    public void testReplaceWithFormatAtEndOfReplacement() {
        String test = "Test string.";
        Pattern p = Pattern.compile("string");
        String replacement = "hello§1";

        ColoredString cs = new ColoredString(test);
        cs.replaceText(p,replacement);
        assertEquals("Test hello§1.",cs.getColoredString());

    }

    public void testConsecutiveReplacementsWithFormatting() {
        String test = "This is a §1test§2test§3test.";
        Pattern p = Pattern.compile("test");
        String replacement = "derp§4";

        ColoredString cs = new ColoredString(test);
        cs.replaceText(p,replacement);
        assertEquals("This is a §1derp§4§2derp§4§3derp§4.",cs.getColoredString());
    }

}
