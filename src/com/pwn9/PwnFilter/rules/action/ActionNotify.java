package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Notify all users with the permission specified in notifyperm:
 */
@SuppressWarnings("UnusedDeclaration")
public class ActionNotify implements Action {
    // Message to apply to this warn action
    String permissionString;
    String messageString;

    public void init(String s)
    {
        permissionString = Bukkit.getPluginManager().getPlugin("PwnFilter").getConfig().getString("notifyperm");
    }

    public boolean execute(final FilterState state ) {

        // Create the message to send
        String sendString = Patterns.replaceVars(messageString,state);

        // Get all logged in players who have the required permission and send them the message
        Bukkit.getScheduler().runTask(state.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission(permissionString)) {
                        p.sendMessage(messageString);
                    }
                }
            }
        });

        return true;
    }
}

