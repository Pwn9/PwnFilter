
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

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.RuleManager;
import com.pwn9.PwnFilter.util.LogManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
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
     * @param p a {@link com.pwn9.PwnFilter.PwnFilter} object.
     */
    public PwnFilterBookListener(PwnFilter p) {
        super(p);
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

        if (DataCache.getInstance().hasPermission(player, "pwnfilter.bypass.book")) return;

        BookMeta bookMeta = event.getNewBookMeta();

        // Process Book Title
        if (bookMeta.hasTitle()) {
            // Run title through filter.
            message = bookMeta.getTitle();
            FilterState state = new FilterState(plugin, message, player, this);
            ruleChain.execute(state);
            if (state.cancel) event.setCancelled(true);
            if (state.messageChanged()) {
                bookMeta.setTitle(state.getModifiedMessage().getColoredString());
                event.setNewBookMeta(bookMeta);
            }
        }

        // Process Book Text
        if (bookMeta.hasPages()) {
            List<String> newPages = new ArrayList<String>();

            final String lineEdgeToken = "§0";
            boolean bookModified = false;

            for (String page : bookMeta.getPages()) {
                // Lines on a page are separated by §0\n§0, for example:
                // Line 1 text§0\n§0Line 2 text§0\n§0Line 3 text§0\n§0Line 4 text

                // Split on §0 and process the split text one line at a time
                // Store the line (either original or modified) in a string builder

                boolean pageModified = false;

                String[] pageSections = page.split(lineEdgeToken);

                StringBuilder modifiedPageText = new StringBuilder();
                int sectionNumber = 0;

                for (String section : pageSections) {
                    sectionNumber++;

                    if (StringUtils.isBlank(section)) {
                        modifiedPageText.append(section);
                    } else {
                        FilterState state = new FilterState(plugin, section, player, this);
                        ruleChain.execute(state);
                        if (state.isCancelled()) {
                            event.setCancelled(true);
                        }
                        if (state.messageChanged()) {
                            // Filter rule matched; store the filtered message
                            modifiedPageText.append(state.getModifiedMessage().getColoredString());
                            bookModified = true;
                            pageModified = true;
                        } else {
                            // No rules were matched
                            modifiedPageText.append(section);
                        }
                    }

                    if (sectionNumber < pageSections.length)
                        modifiedPageText.append(lineEdgeToken);
                }

                if (pageModified) {
                    newPages.add(modifiedPageText.toString());
                } else {
                    newPages.add(page);
                }
            }

            if (bookModified)  {
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
    public void activate(Configuration config) {
        if (isActive()) return;
        setRuleChain(RuleManager.getInstance().getRuleChain("book.txt"));

        PluginManager pm = Bukkit.getPluginManager();
        EventPriority priority = EventPriority.valueOf(config.getString("bookpriority", "LOWEST").toUpperCase());

        if (config.getBoolean("bookfilter")) {
            // Now register the listener with the appropriate priority
            pm.registerEvent(PlayerEditBookEvent.class, this, priority,
                    new EventExecutor() {
                        public void execute(Listener l, Event e) { onBookEdit((PlayerEditBookEvent)e); }
                    },
                    plugin);

            LogManager.logger.info("Activated BookListener with Priority Setting: " + priority.toString()
                    + " Rule Count: " + getRuleChain().ruleCount() );

            setActive();
        }
    }
}

