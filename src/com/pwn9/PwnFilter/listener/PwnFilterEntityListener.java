package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

/**
* Catch Death events to rewrite them with a custom message.
*/

public class PwnFilterEntityListener implements Listener {
    private final PwnFilter plugin;

	public PwnFilterEntityListener(PwnFilter p) {
        plugin = p;
        PluginManager pm = Bukkit.getServer().getPluginManager();

        /* Hook up the Listener for PlayerChat events */
        pm.registerEvent(EntityDeathEvent.class, this, PwnFilter.chatPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onEntityDeath((EntityDeathEvent) e); }
                },
                plugin);
    }

    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event instanceof PlayerDeathEvent)) return;

        PlayerDeathEvent e = (PlayerDeathEvent)event;

        final Player player = (Player)event.getEntity();

        if (plugin.killedPlayers.containsKey(player)) {
            e.setDeathMessage(plugin.killedPlayers.remove(player));
        }

    }

}