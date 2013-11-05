
/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

/**
* Catch Death events to rewrite them with a custom message.
*/

public class PwnFilterEntityListener implements Listener {
    PwnFilter plugin;

    public PwnFilterEntityListener(PwnFilter p) {
        plugin = p;
        PluginManager pm = Bukkit.getServer().getPluginManager();
        EventPriority priority = EventPriority.valueOf(p.getConfig().getString("chatpriority", "LOWEST").toUpperCase());

        /* Hook up the Listener for EntityDeath events */
        pm.registerEvent(EntityDeathEvent.class, this, priority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onEntityDeath((EntityDeathEvent) e); }
                },
                plugin);
    }

    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event instanceof PlayerDeathEvent)) return;

        PlayerDeathEvent e = (PlayerDeathEvent)event;

        final Player player = (Player)event.getEntity();

        if (PwnFilter.killedPlayers.containsKey(player)) {
            e.setDeathMessage(PwnFilter.killedPlayers.remove(player));
        }

    }

}