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

package com.pwn9.filter.engine.rules.action.minecraft;

import com.google.common.collect.ImmutableList;
import com.pwn9.filter.bukkit.BukkitPlayer;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.PwnFormatter;
import com.pwn9.filter.util.tag.TagRegistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Broadcasts the contents of the named file to all users.
 */

class BroadcastFile implements Action {
    private final ImmutableList<String> messageStrings;

    private BroadcastFile(ArrayList<String> s) {
        messageStrings = ImmutableList.copyOf(s);
    }

    /**
     * {@inheritDoc}
     */
    static Action getAction(String s, File sourceDir) throws InvalidActionException {
        ArrayList<String> messages = new ArrayList<>();

        Path filePath = sourceDir.toPath().resolve(s);
        try (Stream<String> sourceLines = Files.lines(filePath)) {
            sourceLines.forEach((String message) ->
                    messages.add(PwnFormatter.legacyTextConverter(message)));
        } catch (FileNotFoundException ex) {
            throw new InvalidActionException("File not found while trying to add Action: " + ex.getMessage());
        } catch (IOException ex) {
            throw new InvalidActionException("Error reading file: " + s);
        }

        return new BroadcastFile(messages);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask, FilterService filterService) {
        final ArrayList<String> preparedMessages = messageStrings.
                stream().
                map(message -> TagRegistry.replaceTags(message, filterTask)).
                collect(Collectors.toCollection(ArrayList::new));

        filterTask.addLogMessage("Broadcasted: " + preparedMessages.get(0) +
                (preparedMessages.size() > 1 ? "..." : ""));

        MessageAuthor author = filterTask.getAuthor();
        if (author instanceof BukkitPlayer) {
            filterTask.addLogMessage("Broadcasted: " + preparedMessages.get(0) + (preparedMessages.size() > 1 ? "..." : ""));
            ((BukkitPlayer) author).getMinecraftAPI().sendBroadcast(preparedMessages);
        }
    }
}

