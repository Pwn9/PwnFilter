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

import com.pwn9.filter.engine.FilterConfig;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.ActionToken;
import com.pwn9.filter.engine.rules.action.InvalidActionException;

import java.util.Arrays;
import java.util.stream.Stream;

public enum TargetedAction implements ActionToken {
    BURN("burnmsg") {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Burn.getAction(s);
        }

        public void setDefMsg(String s) {
            Burn.setDefaultMessage(s);
        }
    },
    FINE("finemsg") {
        @Override
        public void setDefMsg(String s) {
            Fine.setDefaultMessage(s);
        }

        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return Fine.getAction(s);
        }
    },
    KICK("kickmsg") {
        @Override
        public void setDefMsg(String s) {
            Kick.setDefaultMessage(s);
        }

        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Kick.getAction(s);
        }
    },
    KILL("killmsg") {
        @Override
        public void setDefMsg(String s) {
            Kill.setDefaultMessage(s);
        }

        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Kill.getAction(s);
        }
    },
    RESPOND("") {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Respond.getAction(s);
        }
    },
    RESPONDFILE("") {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) throws InvalidActionException {
            return RespondFile.getAction(s, filterConfig.getTextDir());
        }
    },
    WARN("") {
        @Override
        public void setDefMsg(String s) {
            Warn.setDefaultMessage(s);
        }

        @Override
        public Action getAction(String s, FilterConfig filterConfig) {
            return Warn.getAction(s);
        }
    };

    private final String defaultMsgConfig;

    TargetedAction(String defaultMsgConfig) {
        this.defaultMsgConfig = defaultMsgConfig;
    }

    public static Stream<TargetedAction> getActionsWithDefaults() {
        return Arrays.stream(TargetedAction.values()).filter(e -> !e.getDefaultMsgConfigName().isEmpty());
    }

    public String getDefaultMsgConfigName() {
        return defaultMsgConfig;
    }

    public void setDefMsg(String s) {
        throw new RuntimeException("Can not set Default message on action" + this.toString());
    }

}
