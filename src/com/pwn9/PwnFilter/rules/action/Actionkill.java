package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;

/**
 * Kill a player with a customized Death Message
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionkill implements Action {
    // Message to apply to this kick action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"killmsg");
    }

    public boolean execute(final FilterState state ) {
        Bukkit.getScheduler().runTask(state.plugin, new Runnable() {
            public void run() {
                state.plugin.killedPlayers.put(state.player, state.player.getDisplayName() + " " + messageString);
                state.player.setHealth(0);
                state.addLogMessage("Killed by Filter: " + state.player.getName() + " " + messageString);
            }
        });
        return true;
    }
}
