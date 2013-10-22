package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.DefaultMessages;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Responds to the user with the string provided.
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
        final String message = Patterns.replaceVars(messageString,state);

        state.addLogMessage("Responded to " + state.playerName + " with: " + message);
        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                state.getPlayer().sendMessage(message);
            }
        });

        return true;
    }
}

