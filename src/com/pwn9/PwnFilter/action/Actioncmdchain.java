package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.util.Patterns;

/**
 * Execute a chain of commands by the player.
 */
public class Actioncmdchain implements Action {
    String commands;

    public void init(String s)
    {
        commands = s;
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        state.cancel = true;
        String cmds = Patterns.replaceCommands(commands, state.player,
                state.message.getColoredString(), state.getOriginalMessage().getColoredString());
        String cmdchain[] = cmds.split("\\|");
        for (String cmd : cmdchain) {
            state.addLogMessage("Helped " + state.player.getName() + " execute command: " + cmd);
            state.player.chat("/" + cmd);
        }
        return true;
    }
}
