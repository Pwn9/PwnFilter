package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.DefaultMessages;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Warn the user with the string provided.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionrespond implements Action {
    // Message to apply to this warn action
    String messageString;

    public void init(String s)
    {
        messageString = DefaultMessages.prepareMessage(s, "warnmsg");
    }

    public boolean execute(final FilterState state ) {
        if ( state.getPlayer() == null ) return false;

        state.addLogMessage("Warned " + state.playerName + ": " + messageString);
        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                state.getPlayer().sendMessage(messageString);
            }
        });

        return true;
    }
}

