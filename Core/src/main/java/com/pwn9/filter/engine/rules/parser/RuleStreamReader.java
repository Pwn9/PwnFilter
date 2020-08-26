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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for LineNumberReader that auto-trim's lines, skips comment-only
 * lines and strips comments from rules files.
 * User: Sage905
 * Date: 13-11-14
 * Time: 9:49 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
class RuleStreamReader extends LineNumberReader {
    /**
     * Create a new line-numbering reader, using the default input-buffer
     * size.
     *
     * @param in A Reader object to provide the underlying stream
     */
    RuleStreamReader(Reader in) {
        super(in);
        setLineNumber(1);  // So that line #'s start at 1! :)
    }

    /**
     * Create a new line-numbering reader, reading characters into a buffer of
     * the given size.
     *
     * @param in A Reader object to provide the underlying stream
     * @param sz An int specifying the size of the buffer
     */
    public RuleStreamReader(Reader in, int sz) {
        super(in, sz);
        setLineNumber(1);  // So that line #'s start at 1! :)
    }

    /**
     * {@inheritDoc}
     * <p>
     * Scan the file for the next statement (a non-blank line that doesn't
     * start with a #).  If the statement ends with EOF, read and append
     * lines until an EOF on a line of its own is reached.
     */
    @Override
    public String readLine() throws IOException {
        String line;

        String result = null;

        boolean multiline = false;

        while ((line = super.readLine()) != null) {

            line = line.trim(); // Remove leading/trailing whitespace.

            // SKIP this line if it is a comment
            if (line.matches("^#.*")) continue;

            if (multiline && line.equals("EOF")) {
                result = result.substring(0, result.length() - 1);
                break;
            } else if (line.endsWith("<<EOF")) {
                result = line.substring(0, line.length() - 5);
                multiline = true;
            } else if (multiline) {
                result += line + '\n';
            } else return line;
        }
        return result;

    }

    /**
     * Read through the stream until we reach the end of the file, or a
     * blank line.
     *
     * @return A List of NumberedLine's containing the lines for this section.
     * @throws java.io.IOException if any.
     */
    List<NumberedLine> readSection() throws IOException {
        List<NumberedLine> result = new ArrayList<>();
        String line;

        while ((line = readLine()) != null) {
            if (line.isEmpty()) {
                break;
            } else {
                result.add(new NumberedLine(getLineNumber(), line));
            }
        }
        return result;
    }

}
