package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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
        state.plugin.killedPlayers.put(state.player, state.playerName + " " + messageString);
        state.addLogMessage("Killed by Filter: " + state.playerName + " " + messageString);

        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                state.player.setHealth(0);
            }
        });
        return true;
    }
}
