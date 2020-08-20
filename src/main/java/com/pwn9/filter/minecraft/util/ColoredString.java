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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This object is used to provide a mechanism for running regex match and replacements on a string that may
 * have Color Codes and formats (eg: &amp;5&amp;k) embedded in it.
 * NOTE: By default, this object works on the ampersand (&amp;) character, but this can be specified in the constructor.
 * Any valid format code will be removed from the string for matching purposes.
 * <p>
 * Example String:
 * raw:
 * The quick &amp;4brown fox &amp;1&amp;kj&amp;2u&amp;3m&amp;4p&amp;5e&amp;6d over&amp;7 the lazy &amp;ldog.
 * plain:
 * The quick brown fox jumped over the lazy dog
 * codes:
 * {,,,,,,,,,,&amp;4,,,,,,,,,&amp;1&amp;k,&amp;2,&amp;3,&amp;4,&amp;5,&amp;6,,,,,&amp;7,,,,,,,,,&amp;l,,,}
 * <p>
 * The codes array maps codes to the character following it.  In the example above, plain[10] = 'b', codes[10] = "&amp;4"
 * <p>
 * In any string modification action, the codes will be updated to reflect the new string.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public final class ColoredString implements EnhancedString {
    //todo recreate this class to filter {@link TextComponents}
    private final String[] codes; // The String array containing the color / formatting codes
    private final char[] plain; // the plain text
    private final String COLORCODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrx";
    private final char CR = '\r';
    private final char LF = '\n';
    private final String FORMATPREFIXES = "§&";

    /**
     * <p>Constructor for ColoredString.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public ColoredString(String s) {
        char[] raw = s.toCharArray();
        char[] tmpPlain = new char[raw.length];
        String[] tmpCodes = new String[raw.length + 1];

        int textpos = 0;

        for (int i = 0; i < raw.length; i++) {
            if (i != raw.length - 1 && FORMATPREFIXES.indexOf(raw[i]) > -1
                    && COLORCODES.indexOf(raw[i + 1]) > -1) {
                if (tmpCodes[textpos] == null) {
                    tmpCodes[textpos] = new String(raw, i, 2);
                } else {
                    tmpCodes[textpos] += new String(raw, i, 2);
                }
                i++; // Move past the code character.
            } else if (raw[i] == CR || raw[i] == LF) {

                tmpCodes[textpos] = new String(raw, i, 1);

                // Now insert a tab in its place
                tmpPlain[textpos] = ' ';
                textpos++;
            } else {
                tmpPlain[textpos] = raw[i];
                textpos++;
            }

        }
        plain = Arrays.copyOf(tmpPlain, textpos);
        // Copy one more code than the plain string
        // so we can capture any trailing format codes.
        codes = Arrays.copyOf(tmpCodes, textpos + 1);
    }

    /**
     * <p>Constructor for ColoredString.</p>
     *
     * @param plain an array of char.
     * @param codes an array of {@link java.lang.String} objects.
     */
    private ColoredString(char[] plain, String[] codes) {
        this.plain = Arrays.copyOf(plain, plain.length);
        this.codes = Arrays.copyOf(codes, plain.length + 1);
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

    /**
     * {@inheritDoc}
     */
    public char charAt(int i) {
        return plain[i];
    }

    /**
     * {@inheritDoc}
     */
    public CharSequence subSequence(int i, int j) {
        return new String(Arrays.copyOfRange(plain, i, j));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String toString() {
        return new String(plain);
    }

    // Return a string with color codes interleaved.

    /**
     * Reassemble a colord string by inserting codes found in the array before the
     * character in that spot.  If the code is a CR/LF, discard the temporary
     * space character, and replace it with the correct code.
     *
     * @return a {@link java.lang.String} object.
     */
    String getColoredString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < plain.length; i++) {
            if (codes[i] != null) {
                if (codes[i].indexOf(CR) > -1 || codes[i].indexOf(LF) > -1) {
                    sb.append(codes[i]);
                } else {
                    sb.append(codes[i]).append(plain[i]);
                }
            } else {
                sb.append(plain[i]);
            }
        }
        // Check to see if there is a code at the end of the text
        // If so, append it to the end of the string.
        if (codes[codes.length - 1] != null)
            sb.append(codes[codes.length - 1]);
        return sb.toString();
    }

    // Return the char array with the code information.

    /**
     * <p>getCodeArray.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    String[] getCodeArray() {
        return codes;
    }


    /**
     * Replace all occurrences of Regex pattern with replacement String.
     * If the replacement string has codes embedded, separate them and
     * add them to the code array.
     * <p>
     * This is a tricky bit of code.  We will copy the last code before
     * a replacement and prepend it to the next code of the current text.
     * If the current text is done, we'll prepend the last code at the end
     * of the replacement text to the last code of the final string.
     * Example:
     * Test &amp;1foo
     * replace foo with bar:
     * Test &amp;1bar
     * replace bar with baz&amp;2:
     * Test &amp;1baz&amp;2
     * replace baz with nothing:
     * Test &amp;1&amp;2
     *
     * @param p     Regex Pattern
     * @param rText Replacement Text
     * @return a {@link ColoredString} object.
     */
    @Override
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
            String[] currentCodes = new String[newLength + 1];

            // Copy all of the text up to the end of the last match.
            System.arraycopy(lastMatchText, 0, currentText, 0, lastMatchTextLength);
            // Copy any text between the end of the last match and the start
            // of this match.
            System.arraycopy(plain, currentPosition, currentText, lastMatchTextLength, middleLen);
            // Append replacement text in place of current text.
            System.arraycopy(replacement.plain, 0, currentText, lastMatchTextLength + middleLen, replacement.length());

            /*
             Now, copy the format codes.  If there are "trailing" format codes
             from the previous match, prepend them to the codes for the next character.
            */

            // First, the codes up to the end of the last match, including any trailing codes.
            System.arraycopy(lastMatchCodes, 0, currentCodes, 0, lastMatchTextLength + 1);

            // Append the first code to the trailing code.
            currentCodes[lastMatchTextLength] = mergeCodes(currentCodes[lastMatchTextLength], codes[currentPosition]);

            // Copy the codes from between the last match and this one.
            System.arraycopy(codes, currentPosition + 1, currentCodes, lastMatchTextLength + 1, middleLen);

            // Append the first replacement code to the trailing code.
            currentCodes[lastMatchTextLength + middleLen] = mergeCodes(currentCodes[lastMatchTextLength + middleLen], replacement.codes[0]);

            // Copy the codes from the replacement text.
            if (replacement.codes.length > 1)
                System.arraycopy(replacement.codes, 1, currentCodes, lastMatchTextLength + 1 + middleLen, replacement.length());


            currentPosition = mEnd; // Set the position in the original string to the end of the match

            lastMatchText = currentText;
            lastMatchCodes = currentCodes;

        }

        char[] tempText = new char[lastMatchText.length + plain.length - currentPosition];
        String[] tempCodes = new String[lastMatchText.length + plain.length - currentPosition + 1];

        // Copy the text we've processed in previous matches.
        System.arraycopy(lastMatchText, 0, tempText, 0, lastMatchText.length);
        // ... and copy the formats
        System.arraycopy(lastMatchCodes, 0, tempCodes, 0, lastMatchText.length);

        // Copy the original text from the end of the last match to the end
        // of the string.
        System.arraycopy(plain, currentPosition, tempText, lastMatchText.length, plain.length - currentPosition);

        // Merge the codes from the end of the last segment and the beginning of this one
        tempCodes[lastMatchText.length] = mergeCodes(lastMatchCodes[lastMatchText.length], codes[currentPosition]);

        // Copy the remaining codes (not the first one, it was already appended),
        // as well as the trailing code.
        System.arraycopy(codes, currentPosition + 1, tempCodes, lastMatchText.length + 1, plain.length - currentPosition);

        return new ColoredString(tempText, tempCodes);

    }

    public ColoredString patternToLower(Pattern p) {
        char[] modified = plain.clone();

        Matcher m = p.matcher(new String(plain));

        while (m.find()) {
            for (int i = m.start(); i < m.end(); i++) {
                modified[i] = Character.toLowerCase(modified[i]);
            }
        }
        return new ColoredString(modified, codes);
    }

    public ColoredString patternToUpper(Pattern p) {
        char[] modified = plain.clone();

        Matcher m = p.matcher(new String(plain));

        while (m.find()) {
            for (int i = m.start(); i < m.end(); i++) {
                modified[i] = Character.toUpperCase(modified[i]);
            }
        }
        return new ColoredString(modified, codes);
    }

    /**
     * Returns a concatenation of two strings, a + b.  If a or b are null, they
     * are converted to an empty string.  If both a and b are null, returns null.
     *
     * @param a First string to concatenate
     * @param b Second string to concatenate
     * @return Concatenation of a and b, or null if they are both null.
     */
    private String mergeCodes(String a, String b) {
        String result = (a == null) ? "" : a;
        if (b != null) {
            result += b;
        }
        return (result.isEmpty()) ? null : result;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ColoredString) {
            return ((ColoredString) obj).getColoredString().equals(getColoredString());
        } else {
            return getColoredString().equals(obj);
        }
    }

    @Override
    public String getRaw() {
        return getColoredString();
    }
}
