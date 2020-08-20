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
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.util.PwnFormatter;
import com.pwn9.filter.util.tag.TagRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Responds to the user with the string provided.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Respond implements Action {
    protected final List<String> messageStrings;

    protected Respond(List<String> messageStrings) {
        this.messageStrings = messageStrings;
    }

    /**
     * {@inheritDoc}
     */
    public static Action getAction(String s) {
        List<String> messageStrings = new ArrayList<>();

        for (String message : s.split("\n")) {
            messageStrings.add(PwnFormatter.legacyTextConverter(message));
        }
        return new Respond(ImmutableList.copyOf(messageStrings));
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask, FilterService filterService) {
        if (filterTask.getAuthor() == null) return;
        final ArrayList<String> preparedMessages = messageStrings.stream().map(message -> TagRegistry.replaceTags(message, filterTask)).collect(Collectors.toCollection(ArrayList::new));

        filterTask.getAuthor().sendMessages(preparedMessages);

        filterTask.addLogMessage("Responded to " + filterTask.getAuthor().getName()
                + " with: " + preparedMessages.get(0) + "...");


    }
}

