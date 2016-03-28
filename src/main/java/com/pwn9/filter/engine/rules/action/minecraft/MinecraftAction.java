/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.minecraft;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.ActionToken;
import com.pwn9.filter.engine.config.FilterConfig;
import com.pwn9.filter.engine.rules.action.InvalidActionException;

/**
 * Created by Sage905 on 2016-03-26.
 */
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
    };

}
