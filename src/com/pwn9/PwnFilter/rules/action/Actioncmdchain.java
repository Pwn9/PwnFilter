package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;

/**
 * Execute a chain of commands by the player.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actioncmdchain implements Action {
    String commands;

    public void init(String s)
    {
        commands = s;
    }

    public boolean execute(final FilterState state ) {
        state.cancel = true;
        String cmds = Patterns.replaceCommands(commands, state.player,
                state.message.getColoredString(), state.getOriginalMessage().getColoredString(),state);
        String cmdchain[] = cmds.split("\\|");
        for (String cmd : cmdchain) {
            state.addLogMessage("Helped " + state.player.getName() + " execute command: " + cmd);
            state.player.chat("/" + cmd);
        }
        return true;
    }
}
