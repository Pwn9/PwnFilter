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

package com.pwn9.filter.engine.rules.action.targeted;

import com.google.common.collect.ImmutableList;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.PwnFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Responds to the user with the string provided.
 *
 * @author Sage905
 * @version $Id: $Id
 */

class RespondFile extends Respond {

    private RespondFile(List<String> messageStrings) {
        super(messageStrings);
    }

    public static Action getAction(String s, File sourceDir) throws InvalidActionException {
        ArrayList<String> messageStrings = new ArrayList<>();
        Path filePath = sourceDir.toPath().resolve(s);

        try (Stream<String> sourceLines = Files.lines(filePath)) {
            sourceLines.forEach((String message) ->
                    messageStrings.add(PwnFormatter.legacyTextConverter(message)));
        } catch (FileNotFoundException ex) {
            throw new InvalidActionException("File not found while trying to add Action: " + ex.getMessage());
        } catch (IOException ex) {
            throw new InvalidActionException("Error reading file: " + s);
        } catch (UncheckedIOException ex) {
            if (ex.getCause() instanceof MalformedInputException) {
                throw new InvalidActionException("Error reading file: " + s + " File was not utf-8 encoded.", ex.getCause());
            }
        }

        return new RespondFile(ImmutableList.copyOf(messageStrings));
    }
}

