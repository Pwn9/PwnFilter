package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;

/**
 * Execute a chain of console commands
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionconchain implements Action {
    String commands;

    public void init(String s)
    {
        commands = s;
    }

    public boolean execute(final FilterState state ) {
        String cmds = Patterns.replaceCommands(commands,
                state.message.getColoredString(), state.getOriginalMessage().getColoredString(),state);
        String cmdchain[] = cmds.split("\\|");
        for (String cmd : cmdchain) {
            state.addLogMessage("Sending console command: " + cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        return true;
    }
}
