package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;

/**
 * Burns a player to death.
 * TODO: Consider hooking this into the custom death message handler.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionburn implements Action {
    // Message to apply to this burn action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"burnmsg");
    }

    public boolean execute(final FilterState state ) {
        state.player.setFireTicks(5000);
        state.player.sendMessage(messageString);
        state.addLogMessage("Burned " + state.player.getName() + ": " + messageString);
        return true;
    }
}
