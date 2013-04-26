package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.util.Patterns;

/**
 * Execute a command as a player.
 */
public class Actioncommand implements Action {
    String command;

    public void init(String s)
    {
        command = s;
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        state.cancel = true;
        String cmd = Patterns.replaceCommands(command, state.player,
                state.message.getColoredString(), state.getOriginalMessage().getColoredString());
        state.addLogMessage("Helped " + state.player.getName() + " execute command: " + cmd);
        state.player.chat("/" + cmd);
        return true;
    }
}
