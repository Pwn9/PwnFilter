package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;

/**
 * Execute a command as a player.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actioncommand implements Action {
    String command;

    public void init(String s)
    {
        command = s;
    }

    public boolean execute(final FilterState state ) {
        state.cancel = true;
        String cmd;
        if (!command.isEmpty()) {
            cmd = Patterns.replaceCommands(command, state.player,
                    state.message.getColoredString(), state.getOriginalMessage().getColoredString());
            state.addLogMessage("Helped " + state.player.getName() + " execute command: " + cmd);
        } else {
            cmd = state.message.getColoredString();
        }
        state.addLogMessage("Helped " + state.player.getName() + " execute command: " + cmd);
        state.player.chat("/" + cmd);
        return true;
    }
}
