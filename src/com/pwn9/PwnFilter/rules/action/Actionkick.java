package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Kick the user with a customized message.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionkick implements Action {
    // Message to apply to this kick action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"kickmsg");
    }

    public boolean execute(final FilterState state ) {

        state.addLogMessage("Kicked " + state.playerName + ": " + messageString);
        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                state.player.kickPlayer(messageString);
            }
        });

        return true;
    }
}
