package com.pwn9.filter.engine.rules.action;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.ActionToken;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/08/2020.
 */
public interface ActionFactory {
    Action getActionFromString(String s) throws InvalidActionException;

    Action getAction(String actionName, String actionData)
            throws InvalidActionException;

    void addActionTokens(Class<? extends ActionToken> tokenEnum);
}
