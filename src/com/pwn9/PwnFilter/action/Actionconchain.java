package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;

/**
 * Execute a chain of console commands
 */
public class Actionconchain implements Action {
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
            state.addLogMessage("Sending console command: " + cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        return true;
    }
}
