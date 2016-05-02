/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.targeted;

import com.google.common.collect.ImmutableList;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.tag.TagRegistry;
import org.bukkit.ChatColor;

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
@SuppressWarnings("UnusedDeclaration")
public class RespondFile implements Action {
    private final List<String> messageStrings;

    private RespondFile(List<String> messageStrings) {
        this.messageStrings = messageStrings;
    }

    public static Action getAction(String s, File sourceDir ) throws InvalidActionException {
        ArrayList<String> messageStrings = new ArrayList<>();
        Path filePath = sourceDir.toPath().resolve(s);

        try (Stream<String> sourceLines = Files.lines(filePath)) {
            sourceLines.forEach((String message) ->
                    messageStrings.add(ChatColor.translateAlternateColorCodes('&', message)));
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

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask, FilterService filterService) {
        final ArrayList<String> preparedMessages = new ArrayList<String>();

        for (String message : messageStrings) {
            preparedMessages.add(TagRegistry.replaceTags(message, filterTask));
        }

        filterTask.getAuthor().sendMessages(preparedMessages);

        filterTask.addLogMessage("Responded to " + filterTask.getAuthor().getName() + " with: " + preparedMessages.get(0) + "...");

    }
}

