/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.bukkit.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This object is used to provide a mechanism for running regex match and replacements on a string that may
 * have Color Codes and formats (eg: &5&k) embedded in it.
 * NOTE: By default, this object works on the ampersand (&) character, but this can be specified in the constructor.
 * Any valid format code will be removed from the string for matching purposes.
 *
 * Example String:
 * raw:
 * The quick &4brown fox &1&kj&2u&3m&4p&5e&6d over&7 the lazy &ldog.
 * plain:
 * The quick brown fox jumped over the lazy dog
 * codes:
 * {,,,,,,,,,,&4,,,,,,,,,&1&k,&2,&3,&4,&5,&6,,,,,&7,,,,,,,,,&l,,,}
 *
 * The codes array maps codes to the character following it.  In the example above, plain[10] = 'b', codes[10] = "&4"
 *
 * In any string modification action, the codes will be updated to reflect the new string.
 *
 * @author ptoal
 * @version $Id: $Id
 */
public final class ColoredString implements CharSequence {

    private final String[] codes; // The String array containing the color / formatting codes
    private final char[] plain; // the plain text
    private final char formatPrefix;

    /**
     * <p>Constructor for ColoredString.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public ColoredString(String s) {
        this(s, '&');
    }

    /**
     * <p>Constructor for ColoredString.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @param prefix a char.
     */
    public ColoredString(String s, char prefix) {
        formatPrefix = prefix;
        char[] raw = s.toCharArray();
        char[] tmpPlain = new char[raw.length];
        String[] tmpCodes = new String[raw.length+1];

        int textpos = 0;

        for (int i = 0; i < raw.length ; i++) {
            if (i != raw.length-1 && raw[i] == formatPrefix && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(raw[i+1]) > -1) {
                if (tmpCodes[textpos] == null) {
                    tmpCodes[textpos] = new String(raw,i,2);
                } else {
                    tmpCodes[textpos] += new String(raw,i,2);
                }
                i++; // Move past the code character.
            } else {
                tmpPlain[textpos] = raw[i];
                textpos++;
            }
        }
        plain = Arrays.copyOf(tmpPlain,textpos);
        // Copy one more code than the plain string
        // so we can capture any trailing format codes.
        codes = Arrays.copyOf(tmpCodes,textpos+1);
    }

    /**
     * <p>Constructor for ColoredString.</p>
     *
     * @param c a {@link ColoredString} object.
     */
    public ColoredString(ColoredString c) {
        // Create a copy of the original array.
        codes = c.codes;
        plain = c.plain;
        formatPrefix = c.formatPrefix;
    }

    /**
     * <p>Constructor for ColoredString.</p>
     *
     * @param plain an array of char.
     * @param codes an array of {@link java.lang.String} objects.
     * @param prefix a char.
     */
    public ColoredString(char[] plain, String[] codes, char prefix) {
        this.plain = plain;
        this.codes = Arrays.copyOf(codes,plain.length+1);
        formatPrefix = prefix;
    }

    /* CharSequence methods */
    /**
     * <p>length.</p>
     *
     * @return a int.
     */
    public int length() {
        return plain.length;
    }
    /** {@inheritDoc} */
    public char charAt(int i) {
        return plain[i];
    }

    /** {@inheritDoc} */
    public CharSequence subSequence(int i, int j) {
        return new String(Arrays.copyOfRange(plain,i,j));
    }

    /** {@inheritDoc} */
    @Override
    @NotNull
    public String toString() {
        return getPlainString();
    }


    // Strip all codes out of this string.
    /**
     * <p>decolor.</p>
     *
     * @return a {@link ColoredString} object.
     */
    public ColoredString decolor() {
        return new ColoredString(new String(plain));
    }

    // Return a string with color codes interleaved.
    /**
     * <p>getColoredString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getColoredString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0 ; i < plain.length ; i++ ) {
            if ( codes[i] != null ) sb.append(codes[i]);
            sb.append(plain[i]);
        }
        // Check to see if there is a code at the end of the text
        // If so, append it to the end of the string.
        if (codes[codes.length-1] != null)
            sb.append(codes[codes.length-1]);
        return sb.toString();
    }

    // Return a string without codes
    /**
     * <p>getPlainString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPlainString() {
        return new String(plain);
    }

    // Return the char array with the code information.
    /**
     * <p>getCodeArray.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getCodeArray() {
        return codes;
    }


    /**
     * Replace all occurrences of Regex pattern with replacement String.
     * If the replacement string has codes embedded, separate them and
     * add them to the code array.
     *
     * This is a tricky bit of code.  We will copy the last code before
     * a replacement and prepend it to the next code of the current text.
     * If the current text is done, we'll prepend the last code at the end
     * of the replacement text to the last code of the final string.
     * Example:
     * Test &1foo
     * replace foo with bar:
     * Test &1bar
     * replace bar with baz&2:
     * Test &1baz&2
     * replace baz with nothing:
     * Test &1&2
     *
     * @param p Regex Pattern
     * @param rText Replacement Text
     * @return a {@link ColoredString} object.
     */
    public ColoredString replaceText(Pattern p, String rText) {
        Matcher m = p.matcher(new String(plain));
        ColoredString replacement = new ColoredString(rText);

        // Start with an empty set of arrays.  These will be incrementally added
        // to, as we replace each match.
        char[] lastMatchText = new char[0];
        String[] lastMatchCodes = new String[1];

        int currentPosition = 0;

        while (m.find()) {
            int mStart = m.start();
            int mEnd = m.end();

            int lastMatchTextLength = lastMatchText.length;
            int middleLen = mStart - currentPosition;

            int newLength = lastMatchTextLength + middleLen + replacement.length();

            char[] currentText = new char[newLength];
            String[] currentCodes = new String[newLength+1];

            // Copy all of the text up to the end of the last match.
            System.arraycopy(lastMatchText,0,currentText,0,lastMatchTextLength);
            // Copy any text between the end of the last match and the start
            // of this match.
            System.arraycopy(plain,currentPosition,currentText,lastMatchTextLength ,middleLen);
            // Append replacement text in place of current text.
            System.arraycopy(replacement.plain,0,currentText,lastMatchTextLength+middleLen,replacement.length());

            /*
             Now, copy the format codes.  If there are "trailing" format codes
             from the previous match, prepend them to the codes for the next character.
            */

            // First, the codes up to the end of the last match, including any trailing codes.
            System.arraycopy(lastMatchCodes,0,currentCodes,0,lastMatchTextLength+1);

            // Append the first code to the trailing code.
            currentCodes[lastMatchTextLength] = mergeCodes(currentCodes[lastMatchTextLength],codes[currentPosition]);

            // Copy the codes from between the last match and this one.
            System.arraycopy(codes,currentPosition+1,currentCodes,lastMatchTextLength+1,middleLen);

            // Append the first replacement code to the trailing code.
            currentCodes[lastMatchTextLength+middleLen] = mergeCodes(currentCodes[lastMatchTextLength+middleLen],replacement.codes[0]);

            // Copy the codes from the replacement text.
            if (replacement.codes.length > 1)
                System.arraycopy(replacement.codes,1,currentCodes,lastMatchTextLength+1+middleLen,replacement.length());


            currentPosition = mEnd; // Set the position in the original string to the end of the match

            lastMatchText = currentText;
            lastMatchCodes = currentCodes;

        }

        char[] tempText = new char[lastMatchText.length + plain.length - currentPosition];
        String[] tempCodes = new String[lastMatchText.length + plain.length - currentPosition + 1 ];

        // Copy the text we've processed in previous matches.
        System.arraycopy(lastMatchText,0,tempText,0,lastMatchText.length);
        // ... and copy the formats
        System.arraycopy(lastMatchCodes,0,tempCodes,0,lastMatchText.length);

        // Copy the original text from the end of the last match to the end
        // of the string.
        System.arraycopy(plain,currentPosition,tempText,lastMatchText.length,plain.length - currentPosition);

        // Merge the codes from the end of the last segment and the beginning of this one
        tempCodes[lastMatchText.length] = mergeCodes(lastMatchCodes[lastMatchText.length],codes[currentPosition]);

        // Copy the remaining codes (not the first one, it was already appended),
        // as well as the trailing code.
        System.arraycopy(codes,currentPosition+1,tempCodes,lastMatchText.length+1,plain.length - currentPosition);

        return new ColoredString(tempText, tempCodes, formatPrefix);

    }

    /**
     * <p>patternToLower.</p>
     *
     * @param p a {@link java.util.regex.Pattern} object.
     * @return a {@link ColoredString} object.
     */
    public ColoredString patternToLower (Pattern p) {
        Matcher m = p.matcher(new String(plain));

        while (m.find()) {
            for (int i = m.start() ; i < m.end() ; i++ ) {
                plain[i] = Character.toLowerCase(plain[i]);
            }
        }
        return new ColoredString(this);
    }
    
    /**
     * <p>patternToUpper.</p>
     *
     * @param p a {@link java.util.regex.Pattern} object.
     * @return a {@link ColoredString} object.
     */
    public ColoredString patternToUpper (Pattern p) {
        Matcher m = p.matcher(new String(plain));

        while (m.find()) {
            for (int i = m.start() ; i < m.end() ; i++ ) {
                plain[i] = Character.toUpperCase(plain[i]);
            }
        }
        return new ColoredString(this);
    }    

    /**
     * Returns a concatenation of two strings, a + b.  If a or b are null, they
     * are converted to an empty string.  If both a and b are null, returns null.
     * @param a First string to concatenate
     * @param b Second string to concatenate
     * @return Concatenation of a and b, or null if they are both null.
     */
    private String mergeCodes(String a, String b) {
        String result = (a == null)?"":a;
        if (b != null) {
            result += b;
        }
        return (result.isEmpty())?null:result;

    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ColoredString ) {
            return ((ColoredString) obj).getColoredString().equals(getColoredString());
        } else {
            return getColoredString().equals(obj);
        }
    }
}
