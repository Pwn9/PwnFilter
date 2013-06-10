package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Burns a player to death.
 * NOTE: This method needs to use runTask to operate on the player, as the bukkit API
 * calls are NOT thread-safe.
 * TODO: Consider hooking this into the custom death message handler.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionburn implements Action {
    // Message to apply to this burn action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"burnmsg");
    }

    public boolean execute(final FilterState state ) {
        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                state.player.setFireTicks(5000);
                state.player.sendMessage(messageString);
            }
        });

        state.addLogMessage("Burned " + state.playerName + ": " + messageString);
        return true;
    }
}
