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

package com.pwn9.filter.engine.rules.parser;

/**
 * The <code>TokenString</code> class takes a <code>String</code> object
 * and provides the popToken() method for removing the first word of the
 * string and returning it.
 * <p>
 * User: Sage905
 * Date: 13-11-20
 * Time: 8:20 AM
 *
 * @author Sage905
 * @version $Id: $Id
 */
class TokenString {

    final private String originalString;
    private String string;

    /**
     * <p>Constructor for TokenString.</p>
     *
     * @param originalString a {@link java.lang.String} object.
     */
    TokenString(String originalString) {

        string = (originalString == null) ? "" : originalString;
        this.originalString = originalString;
    }

    /**
     * This method finds the first token in the string, removes it from the
     * original string, and returns it. Successive calls to popToken() will
     * remove a word at a time.  When there are no words left in the string
     * the token will be an empty string.
     *
     * @return A string containing the token.
     */
    String popToken() {

        String[] parts;
        parts = string.split("\\s", 2);

        string = (parts.length == 2) ? parts[1] : "";

        return parts[0];
    }

    /**
     * <p>Getter for the field <code>string</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getString() {
        return string;
    }

    /**
     * <p>Getter for the field <code>originalString</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getOriginalString() {
        return originalString;
    }

    /**
     * Reset to the original string.
     */
    public void reset() {
        string = originalString;
    }
}
