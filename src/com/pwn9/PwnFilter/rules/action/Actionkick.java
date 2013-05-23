package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;

/**
 * Kick the user with a customized message.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionkick implements Action {
    // Message to apply to this kick action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"kickmsg");
    }

    public boolean execute(final FilterState state ) {
        state.player.kickPlayer(messageString);
        state.addLogMessage("Kicked " + state.player.getName() + ": " + messageString);
        return true;
    }
}
