package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.DefaultMessages;
import com.pwn9.PwnFilter.util.PointManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Add the configured number of points to the players account.
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionpoints implements Action {

    String messageString;
    double pointsAmount; // How much to fine the player.

    public void init(String s)
    {
        String[] parts = s.split("\\s",2);

        try {
            pointsAmount = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e ) {
            pointsAmount = 1.00;
        }

        String message = (parts.length > 1)?parts[1]:"";
        messageString = DefaultMessages.prepareMessage(parts[1], "pointsmsg");
    }

    public boolean execute(final FilterState state ) {
        Player p = state.getPlayer();
        if (p == null) return false;

        PointManager pm = PointManager.getInstance();

        // TODO: Add more comprehensive messaging, as well as details about thresholds.

        pm.addPlayerPoints(state.getPlayer(), pointsAmount);

        state.addLogMessage(String.format("Points Accumulated %s : %f. Total: %f",state.playerName,pointsAmount, pm.getPlayerPoints(p)));

        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                state.getPlayer().sendMessage(messageString);
            }
        });

        return true;

    }
}
