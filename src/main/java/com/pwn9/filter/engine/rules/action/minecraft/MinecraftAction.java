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

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.ActionToken;
import com.pwn9.filter.engine.FilterConfig;
import com.pwn9.filter.engine.rules.action.InvalidActionException;

public enum MinecraftAction implements ActionToken {
    BROADCAST {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Broadcast.getAction(s);
        }
    },
    BROADCASTFILE {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return BroadcastFile.getAction(s,filterConfig.getTextDir());
        }
    },
    COMMAND {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return Command.getAction(s);
        }
    },
    CMDCHAIN {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return CommandChain.getAction(s);
        }
    },
    CONSOLE {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return Console.getAction(s);
        }
    },
    CONCHAIN {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return ConsoleChain.getAction(s);
        }
    },
    NOTIFY {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return Notify.getAction(s);
        }
    }

}
