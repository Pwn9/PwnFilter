/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.minecraft.util;

import com.pwn9.filter.engine.api.EnhancedString;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;


/**
 * Extended tests for the ColoredString object.
 */
public class ColoredStringTest {
    private final String original = "This&0 is the string &8to test";
    private final String plain = "This is the string to test";
    private final ColoredString testCs = new ColoredString(original);

    @Test
    public void testLength() {
        assertEquals(plain.length(), testCs.length());
    }

    @Test
    public void testCharAt() {
        assertEquals(plain.charAt(5), testCs.charAt(5));
    }

    @Test
    public void testSubSequence(){
        assertEquals(plain.subSequence(5, 9), testCs.subSequence(5, 9));
    }

    @Test
    public void testToString() {
        assertEquals(plain, testCs.toString());
    }

    @Test
    public void testGetColoredString() {
        assertEquals(original, testCs.getColoredString());

    }

    @Test
    public void testGetCodeArray() {
        String[] array = testCs.getCodeArray();
        assertEquals(array[4], "&0");
        assertEquals(array[19], "&8");
        assertEquals(array.length - 1, plain.length());
    }

    @Test
    public void testColoredString() {
        String testString = "&7This &9is&l the &1string&3 under test.";
        String plainString = "This is the string under test.";

        String[] codeArray = {"&7", null, null, null, null, "&9", null, "&l",
                null, null, null, null, "&1", null, null, null, null, null, "&3", null, null,
                null, null, null, null, null, null, null, null, null, null};

        ColoredString cs = new ColoredString(testString);
        // Check length() method
        assertEquals(cs.length(), plainString.length());
        // Check charAt() method
        assertEquals(cs.charAt(5), 'i');
        // Check subSequence method
        assertEquals("is th", cs.subSequence(5, 10));

        // Basic setup tests
        assertEquals(plainString, cs.toString());
        assertArrayEquals(codeArray, cs.getCodeArray());
        assertEquals(testString, cs.getColoredString());
        assertEquals(plainString, cs.toString());


        // Replace with plain text
        Pattern p = Pattern.compile("test");
        ColoredString modified = cs.replaceText(p, "derp");
        assertEquals("This is the string under derp.", modified.toString());
        assertEquals("&7This &9is&l the &1string&3 under derp.", modified.getColoredString());

        // Replace with colored text
        p = Pattern.compile("string");
        modified = cs.replaceText(p, "&3r&4a&5i&6n&7b&8o&9w");
        assertEquals("This is the rainbow under test.", modified.toString());
        assertEquals("&7This &9is&l the &1&3r&4a&5i&6n&7b&8o&9w&3 under test.", modified.getColoredString());

    }

    @Test
    public void testColourStringRGB(){
        String test = "§x§f§f§e§f§d§5Kittens! are §x§d§d§0§0§0§0smelly";
        ColoredString coloredString = new ColoredString(test);
        assertEquals("Kittens! are smelly",coloredString.toString());
        assertEquals(test,coloredString.getColoredString());
        coloredString = coloredString.replaceText(Pattern.compile("are smelly"),"may not be funny");
        assertEquals("Kittens! may not be funny",coloredString.toString());
        assertEquals("§x§f§f§e§f§d§5Kittens! may not be funny",coloredString.getColoredString());
    }


    @Test
    public void testColoredStringWithDoubleFormatting() {
        String test = "&9&l![MEMBERS]!:&r &aPlease &c&l/VOTE&r&a to get &9&l4 DIAMONDS&r&a /VOTE!";
        String plain = "![MEMBERS]!: Please /VOTE to get 4 DIAMONDS /VOTE!";
        Pattern p = Pattern.compile("-+m+e+m+b+e+r");

        ColoredString cs1 = new ColoredString(test);

        assertEquals(test, cs1.getColoredString());
        assertEquals(plain, cs1.toString());

        EnhancedString cs2 = new ColoredString("-member");

        EnhancedString modified2 = cs2.replaceText(p, "&9&l![MEMBERS]!:&r &aPlease &c&l/VOTE&r&a to get &9&l4 DIAMONDS&r&a /VOTE!");
        assertEquals("&9&l![MEMBERS]!:&r &aPlease &c&l/VOTE&r&a to get &9&l4 DIAMONDS&r&a /VOTE!", modified2.getRaw());
    }

    /**
     * Test that a string that ends with a format code is properly represented
     */
    @Test
    public void testColoredStringWithFormatAtEnd() {
        String test = "This is a string ending with format.&3";
        ColoredString cs = new ColoredString(test);
        assertEquals(cs.getColoredString(), test);
    }

    @Test
    public void testReplaceText() {
        // Complex string test.  Make sure that characters are not lost.
        String test = "a&3test.";
        EnhancedString cs = new ColoredString(test);
        EnhancedString result = cs.replaceText(Pattern.compile("test"), "derp&4");
        assertEquals(result.getRaw(), "a&3derp&4.");
    }

    @Test
    public void testReplaceWithFormatAtEnd() {
        String test = "Test string.&1";
        Pattern p = Pattern.compile("string");
        String replacement = "hello";

        EnhancedString cs = new ColoredString(test);
        assertEquals("Test hello.&1", cs.replaceText(p, replacement).getRaw());
    }

    @Test
    public void testReplaceWithFormatAtEndOfReplacement() {
        String test = "Test string.";
        Pattern p = Pattern.compile("string");
        String replacement = "hello&1";

        EnhancedString cs = new ColoredString(test);
        assertEquals("Test hello&1.", cs.replaceText(p, replacement).getRaw());

    }

    @Test
    public void testConsecutiveReplacementsWithFormatting() {
        String test = "&This is a &1test&2testa&3test.&";
        Pattern p = Pattern.compile("test");
        String replacement = "derp&4";
        EnhancedString cs = new ColoredString(test);
        EnhancedString result = cs.replaceText(p, replacement);
        assertEquals("&This is a &1derp&4&2derp&4a&3derp&4.&", result.getRaw());
    }

    @Test
    public void testPatternToLower() {
        assertEquals(new ColoredString("this&0 is the string &8to test"),
                testCs.patternToLower(Pattern.compile("This")));
    }

    @Test
    public void testPatternToUpper(){
        assertEquals(new ColoredString("THIS&0 is the string &8to test"),
                testCs.patternToUpper(Pattern.compile("This")));
    }

    @Test
    public void testGetRaw() {
        assertEquals(testCs.getRaw(), original);
    }

    @Test
    public void sectionSymbolTreatedAsCode() {
        String testString = "§7This §9is§l the §1string§3 under test.";
        String plainString = "This is the string under test.";
        String[] codeArray = {"§7", null, null, null, null, "§9", null, "§l",
                null, null, null, null, "§1", null, null, null, null, null, "§3", null, null,
                null, null, null, null, null, null, null, null, null, null};

        ColoredString cs = new ColoredString(testString);

        assertEquals(plainString, cs.toString());
        assertArrayEquals(codeArray, cs.getCodeArray());
        assertEquals(testString, cs.getColoredString());
        assertEquals(plainString, cs.toString());

    }

    @Test
    public void testCRLFInStringIsStrippedFromMatch() {
        String test = "Test string.\rNext line\r\nAnother line";
        Pattern p = Pattern.compile("\\bline\\b");
        String replacement = "word";

        EnhancedString cs = new ColoredString(test);
        assertEquals("Test string.\rNext word\r\nAnother word", cs.replaceText(p, replacement).getRaw());

    }


}

