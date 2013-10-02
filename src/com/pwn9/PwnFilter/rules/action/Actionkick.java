package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.DefaultMessages;
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
        messageString = DefaultMessages.prepareMessage(s, "kickmsg");
    }

    public boolean execute(final FilterState state ) {

        if (state.getPlayer() == null ) return false;

        state.addLogMessage("Kicked " + state.playerName + ": " + messageString);
        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                state.getPlayer().kickPlayer(messageString);
            }
        });

        return true;
    }
}
