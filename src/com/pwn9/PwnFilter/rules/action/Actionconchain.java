package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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
        String cmds = Patterns.replaceCommands(commands, state);
        String cmdchain[] = cmds.split("\\|");
        for (final String cmd : cmdchain) {
            state.addLogMessage("Sending console command: " + cmd);
            Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            });
        }
        return true;
    }
}
