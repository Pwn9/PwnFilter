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

package com.pwn9.filter.engine.rules.action.core;

import com.pwn9.filter.engine.FilterConfig;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.ActionToken;
import com.pwn9.filter.engine.rules.action.InvalidActionException;

/**
 * A table of Action Tokens provided by this package.
 * <p>
 * Created by Sage905 on 2016-03-18.
 */

public enum CoreAction implements ActionToken {
    ABORT {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Abort.INSTANCE;
        }
    },
    DENY {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Deny.INSTANCE;
        }
    },
    LOG {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Log.getAction(s);
        }
    },
    LOWER {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Lower.INSTANCE;
        }
    },
    POINTS {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return Points.getAction(s);
        }
    },
    RANDREP {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return RandomReplace.getAction(s);
        }
    },
    REPLACE {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Replace.getAction(s);
        }
    },
    REWRITE {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Rewrite.getAction(s);
        }
    },
    UPPER {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Upper.INSTANCE;
        }
    }

}