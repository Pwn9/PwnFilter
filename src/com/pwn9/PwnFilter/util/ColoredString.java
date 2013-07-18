package com.pwn9.PwnFilter.util;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This object is used to provide a mechanism for running regex match and replacements on a string that may
 * have Color Codes and formats (eg: §5§k) embedded in it.
 * NOTE: This object works on the section character (§), not ampersand (&).
 *
 * Example String:
 * raw:
 * The quick §4brown fox §1§kj§2u§3m§4p§5e§6d over§7 the lazy §ldog.
 * plain:
 * The quick brown fox jumped over the lazy dog
 * codes:
 * {,,,,,,,,,,§4,,,,,,,,,§1§k,§2,§3,§4,§5,§6,,,,,§7,,,,,,,,,§l,,,}
 *
 * The codes array maps codes to the character following it.  In the example above, plain[10] = 'b', codes[10] = "§4"
 *
 * In any string modification action, the codes will be updated to reflect the new string.
 *
 */
public class ColoredString implements CharSequence {

    private char[] raw; // The original string
    private String[] codes; // The String array containing the color / formatting codes
    private char[] plain; // the plain text

    public ColoredString(String s) {
        set(s);
    }

    public ColoredString(ColoredString c) {
        // Create a copy of the original array.
        raw = c.raw;
        codes = c.codes;
        plain = c.plain;
    }

    /* CharSequence methods */
    public int length() {
        return plain.length;
    }
    public char charAt(int i) {
        return plain[i];
    }
    public CharSequence subSequence(int i, int j) {
        return new String(Arrays.copyOfRange(plain,i,j));
    }

    @Override
    public String toString() {
        return getColoredString();
    }

    // Update this object with a new string.
    public void set (String s) {

        raw = ChatColor.translateAlternateColorCodes('&',s).toCharArray();
        plain = new char[raw.length];
        codes = new String[raw.length];

        int textpos = 0;

        for (int i = 0; i < raw.length ; i++) {
            if (raw[i] == '\u00A7' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(raw[i+1]) > -1) {
                if (codes[textpos] == null) {
                    codes[textpos] = new String(raw,i,2);
                } else {
                    codes[textpos] += new String(raw,i,2);
                }
                i++; // Move past the code character.
            } else {
                plain[textpos] = raw[i];
                textpos++;
            }
        }
        plain = Arrays.copyOf(plain,textpos);
        codes = Arrays.copyOf(codes,textpos);

    }


    // Strip all codes out of this string.
    public void decolor() {
        codes = new String[plain.length];
    }

    // Return a string with color codes interleaved.
    public String getColoredString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0 ; i < plain.length ; i++ ) {
            if ( codes[i] != null ) sb.append(codes[i]);
            sb.append(plain[i]);
        }

        return sb.toString();
    }

    // Return a string without codes
    public String getPlainString() {
        return new String(plain);
    }

    // Return the char array with the code information.
    public String[] getCodeArray() {
        return codes;
    }

    // Replace all occurrences of Regex pattern with replacement String.  If the replacement string
    // has codes embedded, separate them and add them to the code array.
    public void replaceText(Pattern p, String rText ) {
        Matcher m = p.matcher(new String(plain));
        ColoredString replacement = new ColoredString(rText);

        char[] newText = new char[0];  // We sequentially add to these through this operation.
        String[] newCodes = new String[0];

        int pos = 0;

        while (m.find()) {
            int mStart = m.start();
            int mEnd = m.end();

            int firstLen = newText.length;
            int middleLen = mStart - pos;
            int endLen = replacement.length();

            int newLength = firstLen + middleLen + endLen;

            char[] tempText = new char[newLength];
            String[] tempCodes = new String[newLength];


            // Copy anything we've done so far
            System.arraycopy(newText,0,tempText,0,firstLen);
            System.arraycopy(newCodes,0,tempCodes,0,firstLen);

            // Copy any of the original string between the pos pointer and the start of the
            // Current match
            System.arraycopy(plain,pos,tempText,firstLen ,middleLen);
            System.arraycopy(codes,pos,tempCodes,firstLen,middleLen);

            // Add replacement text
            System.arraycopy(replacement.plain,0,tempText,firstLen+middleLen,replacement.length());
            System.arraycopy(replacement.codes,0,tempCodes,firstLen+middleLen,replacement.length());

            pos = mEnd; // Set the position in the original string to the end of the match

            newText = tempText;
            newCodes = tempCodes;

        }
        char[] tempText = new char[newText.length + plain.length - pos];
        String[] tempCodes = new String[newText.length + plain.length - pos];

        // Copy anything we've done so far
        System.arraycopy(newText,0,tempText,0,newText.length);
        System.arraycopy(newCodes,0,tempCodes,0,newText.length);

        // Now get the end of the string
        System.arraycopy(plain,pos,tempText,newText.length,plain.length - pos);
        System.arraycopy(codes,pos,tempCodes,newText.length,plain.length - pos);

        plain = tempText;
        codes = tempCodes;

    }

    public boolean patternToLower (Pattern p) {
        Matcher m = p.matcher(new String(plain));

        while (m.find()) {
            for (int i = m.start() ; i < m.end() ; i++ ) {
                plain[i] = Character.toLowerCase(plain[i]);
            }
        }
        return true;
    }

}