
/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.engine.api.FilterTask;
import com.pwn9.filter.minecraft.api.MinecraftPlayer;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.minecraft.api.MinecraftServer;
import com.pwn9.filter.minecraft.util.ColoredString;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.rules.RuleManager;
import com.pwn9.filter.util.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Listen for Book Change events and apply the filter to the text.
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class PwnFilterBookListener extends BaseListener {

    /**
     * <p>Constructor for PwnFilterBookListener.</p>
     *
     */
    public PwnFilterBookListener() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public String getShortName() {
        return "BOOK";
    }

    // This is the handler
    /**
     * <p>onBookEdit.</p>
     *
     * @param event a {@link org.bukkit.event.player.PlayerEditBookEvent} object.
     */
    public void onBookEdit(PlayerEditBookEvent event) {
        Player player;
        String message;
        // Don't process already cancelled events.
        if (event.isCancelled()) return;

        player = event.getPlayer();

        if (MinecraftServer.getAPI().getAuthor(player.getUniqueId()).hasPermission("pwnfilter.bypass.book")) return;

        BookMeta bookMeta = event.getNewBookMeta();

        // Process Book Title
        if (bookMeta.hasTitle()) {
            // Run title through filter.
            message = bookMeta.getTitle();
            FilterTask filterTask = new FilterTask(new ColoredString(message),
                    MinecraftPlayer.getInstance(player), this);
            ruleChain.execute(filterTask);
            if (filterTask.isCancelled()) event.setCancelled(true);
            if (filterTask.messageChanged()) {
                bookMeta.setTitle(filterTask.getModifiedMessage().getRaw());
                event.setNewBookMeta(bookMeta);
            }
        }

        // Process Book Text
        if (bookMeta.hasPages()) {
            List<String> newPages = new ArrayList<String>();
            boolean modified = false;
            for (String page : bookMeta.getPages()) {
                FilterTask state = new FilterTask(new ColoredString(page),
                        MinecraftPlayer.getInstance(player.getUniqueId()), this);
                ruleChain.execute(state);
                if (state.isCancelled()) {
                    event.setCancelled(true);
                }
                if (state.messageChanged()) {
                    page = state.getModifiedMessage().getRaw();
                    modified = true;
                }
                newPages.add(page);
            }
            if (modified)  {
                bookMeta.setPages(newPages);
                event.setNewBookMeta(bookMeta);
            }
        }

    }


    /**
     * {@inheritDoc}
     *
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p/>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    @Override
    public void activate() {
        if (isActive()) return;

        setRuleChain(RuleManager.getInstance().getRuleChain("book.txt"));


        PluginManager pm = Bukkit.getPluginManager();
        EventPriority priority = BukkitConfig.getBookpriority();

        if (!active && BukkitConfig.bookfilterEnabled()  ) {
            // Now register the listener with the appropriate priority
            pm.registerEvent(PlayerEditBookEvent.class, this, priority,
                    new EventExecutor() {
                        public void execute(Listener l, Event e) { onBookEdit((PlayerEditBookEvent) e); }
                    },
                    PwnFilterPlugin.getInstance());
            setActive();
            LogManager.logger.info("Activated BookListener with Priority Setting: " + priority.toString()
                    + " Rule Count: " + getRuleChain().ruleCount() );

        }
    }
}

