package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;

/**
 * Kill a player with a customized Death Message
 */
public class Actionkill implements Action {
    // Message to apply to this kick action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"killmsg");
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            public void run() {
                plugin.killedPlayers.put(state.player, state.player.getDisplayName() + " " + messageString);
                state.player.setHealth(0);
                state.addLogMessage("Killed by Filter: " + state.player.getName() + " " + messageString);
            }
        });
        return true;
    }
}
