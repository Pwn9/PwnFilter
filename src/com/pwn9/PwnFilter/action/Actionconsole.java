package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;

/**
 * Execute a console command
 */
public class Actionconsole implements Action {
    String command;

    public void init(String s)
    {
        command = s;
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        state.cancel = true;
        String cmd = Patterns.replaceCommands(command, state.player,
                state.message.getColoredString(), state.getOriginalMessage().getColoredString());
        state.addLogMessage("Sending console command: " + cmd);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        return true;
    }
}
