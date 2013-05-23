package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;

/**
 * Warn the user with the string provided.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionwarn implements Action {
    // Message to apply to this warn action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"warnmsg");
    }

    public boolean execute(final FilterState state ) {
        state.player.sendMessage(messageString);
        state.addLogMessage("Warned " + state.player.getName() + ": " + messageString);
        return true;
    }
}

