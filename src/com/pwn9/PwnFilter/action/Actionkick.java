package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;

/**
 * Kick the user with a customized message.
 */
public class Actionkick implements Action {
    // Message to apply to this kick action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"kickmsg");
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            public void run() {
                state.player.kickPlayer(messageString);
                state.addLogMessage("Kicked " + state.player.getName() + ": " + messageString);
            }
        });
        return true;
    }
}
