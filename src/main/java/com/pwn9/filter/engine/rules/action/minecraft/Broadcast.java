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

import com.pwn9.filter.bukkit.BukkitPlayer;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.util.PwnFormatter;
import com.pwn9.filter.util.tag.TagRegistry;

import java.util.ArrayList;

/**
 * Responds to the user with the string provided.
 * <p>
 * TODO: Extract Broadcast actions from Minecraft to make them universal.
 */

class Broadcast implements Action {
    private final String[] messageStrings;

    private Broadcast(String[] strings) {
        this.messageStrings = strings;
    }

    /**
     * {@inheritDoc}
     */
    static Action getAction(String s) {
        return new Broadcast(PwnFormatter.legacyTextConverter(s).split("\n"));
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask, FilterService filterService) {

        /*
        TODO: Abstract out to a BroadcastDestination object, so that broadcasts
        can be sent to more than just the minecraft server (eg: to IRC)
         */

        final ArrayList<String> preparedMessages = new ArrayList<>();

        for (String message : messageStrings) {
            preparedMessages.add(TagRegistry.replaceTags(message, filterTask));
        }

        MessageAuthor author = filterTask.getAuthor();
        if (author instanceof BukkitPlayer) {
            filterTask.addLogMessage("Broadcasted: " + preparedMessages.get(0) + (preparedMessages.size() > 1 ? "..." : ""));
            ((BukkitPlayer) author).getMinecraftAPI().sendBroadcast(preparedMessages);
        }
    }
}

