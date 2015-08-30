/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.parser;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for LineNumberReader that auto-trim's lines, skips comment-only
 * lines and strips comments from rules files.
 * User: ptoal
 * Date: 13-11-14
 * Time: 9:49 PM
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class RuleStreamReader extends LineNumberReader{
    /**
     * Create a new line-numbering reader, using the default input-buffer
     * size.
     *
     * @param in A Reader object to provide the underlying stream
     */
    public RuleStreamReader(Reader in) {
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
     *
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
                result = result.substring(0,result.length()-1);
                break;
            }
            else if (line.endsWith("<<EOF")) {
                result = line.substring(0,line.length()-5);
                multiline = true;
            }
            else if (multiline) {
                result += line + '\n';
            } else return line;
        }
        return result;

    }

    /**
     * Read through the stream until we reach the end of the file, or a
     * newline.
     *
     * @return A List of NumberedLine's containing the lines for this section.
     * @throws java.io.IOException if any.
     */
    public List<NumberedLine> readSection() throws IOException {
        List<NumberedLine> result = new ArrayList<NumberedLine>();
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
