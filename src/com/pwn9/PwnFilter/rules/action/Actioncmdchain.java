package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Execute a chain of commands by the player.
 *  * NOTE: This method needs to use runTask to operate on the player, as the bukkit API
 * calls are NOT thread-safe.
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
        String cmds = Patterns.replaceCommands(commands,
                state.message.getColoredString(), state.getOriginalMessage().getColoredString(),state);
        String cmdchain[] = cmds.split("\\|");
        for (final String cmd : cmdchain) {
            state.addLogMessage("Helped " + state.playerName + " execute command: " + cmd);

            Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
                @Override
                public void run() {
                    state.player.chat("/" + cmd);
                }
            });
        }


        return true;
    }
}
