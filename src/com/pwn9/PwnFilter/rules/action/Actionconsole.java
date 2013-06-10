package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;

/**
 * Execute a console command
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionconsole implements Action {
    String command;

    public void init(String s)
    {
        command = s;
    }

    public boolean execute(final FilterState state ) {
        String cmd = Patterns.replaceCommands(command,
                state.message.getColoredString(), state.getOriginalMessage().getColoredString(),state);
        state.addLogMessage("Sending console command: " + cmd);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        return true;
    }
}
