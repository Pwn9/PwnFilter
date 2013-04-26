package com.pwn9.PwnFilter.util;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This object is used to provide a mechanism for running regex match and replacements on a string that may
 * have Color Codes (eg: §5) embedded in it.
 * NOTE: This object works on the section character (§), not ampersand (&).
 *
 * Example String:
 * raw:
 * The quick §4brown fox §1j§2u§3m§4p§5e§6d over§7 the lazy §ldog.
 * codedText[0]:
 * The quick brown fox jumped over the lazy dog
 * codedText[1]:
 * {,,,,,,,,,,4,,,,,,,,,1,2,3,4,5,6,,,,,7,,,,,,,,,l,,,}
 *
 * The codeArray maps codes to the character following it.  In the example above, plain[10] = b, codeArray[10] = 4
 *
 * In any string modification action, the codeArray will be updated to reflect the new string.
 *
 */
public class ColoredString implements CharSequence {
    private char[][] codedText = new char[2][];

    public ColoredString(String s) {
        set(s);
    }
    public ColoredString(ColoredString c) {
        // Create a copy of the original array.
        codedText = c.codedText.clone();
    }

    /* CharSequence methods */
    public int length() {
        return codedText[0].length;
    }
    public char charAt(int i) {
        return codedText[0][i];
    }
    public CharSequence subSequence(int i, int j) {
        return new String(codedText[0]).substring(i,j);
    }

    /* Override toString */
    @Override
    public String toString() {
        return getColoredString();
    }

    // Update this object with a new string.
    public void set (String s) {
        codedText = splitCodes(s);
    }

    // Strip all codes out of this string.
    public void decolor() {
        Arrays.fill(codedText[1],'\u0000');
    }

    // Return a string with color codes interleaved.
    public String getColoredString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0 ; i < codedText[0].length ; i++ ) {
            if ( codedText[1][i] != 0 ) sb.append('\u00A7').append(codedText[1][i]);
            sb.append(codedText[0][i]);
        }

        return sb.toString();
    }

    // Return a string without codes
    public String getPlainString() {
        return new String(codedText[0]);
    }

    // Return the char array with the code information.
    public char[] getCodeArray() {
        return codedText[1];
    }

    // Replace all occurrences of Regex pattern with replacement String.  If the replacement string
    // has codes embedded, separate them and add them to the code array.
    public void replaceText(Pattern p, String rText ) {
        Matcher m = p.matcher(new String(codedText[0]));
        char[][] replacement = splitCodes(rText);
        char[][] result = new char[2][];
        StringBuilder newText = new StringBuilder();
        StringBuilder newCodes = new StringBuilder();
        int pos = 0;

        while (m.find()) {
            int startMatch = m.start();
            int endMatch = m.end();

            // Copy the array from pos to start - 1
            newText.append(Arrays.copyOfRange(codedText[0],pos,startMatch));
            newCodes.append(Arrays.copyOfRange(codedText[1], pos, startMatch));

            // Add replacement text
            newText.append(Arrays.copyOfRange(replacement[0],0,replacement[0].length));
            newCodes.append(Arrays.copyOfRange(replacement[1], 0, replacement[1].length));

            pos = endMatch;

        }
        newText.append(Arrays.copyOfRange(codedText[0],pos,codedText[0].length));
        newCodes.append(Arrays.copyOfRange(codedText[1], pos, codedText[1].length));

        result[0] = newText.toString().toCharArray();
        result[1] = newCodes.toString().toCharArray();

        codedText = result;

    }

    public boolean patternToLower (Pattern p) {
        Matcher m = p.matcher(new String(codedText[0]));

        while (m.find()) {
            for (int i = m.start() ; i < m.end() ; i++ ) {
                codedText[0][i] = Character.toLowerCase(codedText[0][i]);
            }
        }
        return true;
    }

    /**
     * Split a String into char[] arrays, one containing the text, the other containing raw character codes
     *
     * @param s String to split color codes from
     * @return A 2-d character array in ColorString format.
     */
    public static char[][] splitCodes(String s) {
        s = ChatColor.translateAlternateColorCodes('&',s);
        char[] raw = s.toCharArray();
        char[] codes = new char[raw.length]; // We assume the code below will never increase the size of
        char[] plain = new char[raw.length]; // these two strings.
        char[][] result = new char[2][];

        int textpos = 0;

        for (int i = 0; i < raw.length ; i++) {
            if (raw[i] == '\u00A7' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(raw[i+1]) > -1) {
                codes[textpos] = raw[i+1];
                i++;
            } else {
                plain[textpos] = raw[i];
                textpos++;
            }
        }
        result[0] = Arrays.copyOf(plain,textpos);
        result[1] = Arrays.copyOf(codes,textpos);
        return result;
    }
}
