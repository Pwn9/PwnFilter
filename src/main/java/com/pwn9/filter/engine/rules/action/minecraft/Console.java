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
import com.pwn9.filter.engine.rules.action.InvalidActionException;
import com.pwn9.filter.util.tag.TagRegistry;

/**
 * Execute a console command
 *
 * @author Sage905
 * @version $Id: $Id
 */

class Console implements Action {
    private final String command;

    private Console(String s) {
        this.command = s;
    }

    /** {@inheritDoc} */
    public static Action getAction(String s) throws InvalidActionException
    {
        if (s.isEmpty()) throw new InvalidActionException("No command was provided to 'console'");
        return new Console(s);

    }

    /** {@inheritDoc} */
    public void execute(final FilterContext filterTask, FilterService filterService) {
        final String cmd = TagRegistry.replaceTags(command, filterTask);
        MessageAuthor author = filterTask.getAuthor();
        if (author instanceof BukkitPlayer) {
            filterTask.addLogMessage("Sending console command: " + cmd);
            ((BukkitPlayer) author).getMinecraftAPI().executeCommand(cmd);
        } else {
            filterTask.addLogMessage("Failed to send console command to non-Bukkit Player: " + cmd);
        }
    }
}
