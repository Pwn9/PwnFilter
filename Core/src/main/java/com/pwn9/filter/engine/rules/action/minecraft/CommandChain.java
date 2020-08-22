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

import com.pwn9.filter.engine.api.Player;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.tag.TagRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Execute a chain of commands by the player.
 * * NOTE: This method needs to use runTask to operate on the player, as the bukkit API
 * calls are NOT thread-safe.
 *
 * @author Sage905
 * @version $Id: $Id
 */

class CommandChain implements Action {
    private final List<String> commands;

    private CommandChain(List<String> commands) {
        this.commands = commands;
    }

    /**
     * {@inheritDoc}
     */
    public static Action getAction(String s) throws InvalidActionException {
        if (s.isEmpty())
            throw new InvalidActionException("No commands were provided to 'cmdchain'");
        return new CommandChain(Arrays.asList(s.split("\\|")));
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final FilterContext filterTask, FilterService filterService) {
        filterTask.setCancelled();
        final ArrayList<String> parsedCommands = new ArrayList<>();

        for (String cmd : commands)
            parsedCommands.add(TagRegistry.replaceTags(cmd, filterTask));

        MessageAuthor author = filterTask.getAuthor();
        if (author instanceof Player) {
            Player player = (Player) author;
            for (String cmd : parsedCommands) {
                player.executeCommand(cmd);
                filterTask.addLogMessage("Helped " + author.getName() + " execute command: " + cmd);
            }
        } else {
            filterTask.addLogMessage("Could not execute cmdchain on non-player.");
            filterTask.setCancelled();
        }
    }
}
