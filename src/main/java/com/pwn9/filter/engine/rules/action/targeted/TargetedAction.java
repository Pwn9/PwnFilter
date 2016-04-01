/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.targeted;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.ActionToken;
import com.pwn9.filter.engine.config.FilterConfig;
import com.pwn9.filter.engine.rules.action.InvalidActionException;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Sage905 on 2016-03-26.
 */

public enum TargetedAction implements ActionToken {
    BURN("burnmsg") {
        @Override
        public Action getAction(String s, FilterConfig filterConfig) { return Burn.getAction(s); }
        public void setDefMsg(String s) {Burn.setDefaultMessage(s);}
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

    public String getDefaultMsgConfigName() {
        return defaultMsgConfig;
    }
    public void setDefMsg(String s) {
        throw new RuntimeException("Can not set Default message on action" + this.toString());};

    public static Stream<TargetedAction> getActionsWithDefaults() {
        return Arrays.stream(TargetedAction.values()).filter(e -> !e.getDefaultMsgConfigName().isEmpty());
    }

}
