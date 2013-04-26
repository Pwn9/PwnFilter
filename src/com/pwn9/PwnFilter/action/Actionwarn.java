package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;

/**
 * Warn the user with the string provided.
 */
public class Actionwarn implements Action {
    // Message to apply to this kick action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"warnmsg");
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            public void run() {
                state.player.sendMessage(messageString);
                state.addLogMessage("Warned " + state.player.getName() + ": " + messageString);
            }
        });
        return true;
    }
}

