package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Warn the user with the string provided.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionwarn implements Action {
    // Message to apply to this warn action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"warnmsg");
    }

    public boolean execute(final FilterState state ) {

        state.addLogMessage("Warned " + state.playerName + ": " + messageString);
        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                state.player.sendMessage(messageString);
            }
        });

        return true;
    }
}

