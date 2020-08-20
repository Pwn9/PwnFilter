/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.bukkit.PwnFilterBukkitPlugin;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.minecraft.util.ColoredString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Listen for Book Change events and apply the filter to the text.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class PwnFilterBookListener extends AbstractBukkitListener {


    public PwnFilterBookListener(PwnFilterPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getShortName() {
        return "BOOK";
    }

    // This is the handler
    void onBookEdit(PlayerEditBookEvent event) {
        Player player;
        String message;
        // Don't process already cancelled events.
        if (event.isCancelled()) return;

        player = event.getPlayer();

        if (plugin.getFilterService().getAuthor(player.getUniqueId()).hasPermission("pwnfilter.bypass.book"))
            return;

        BookMeta bookMeta = event.getNewBookMeta();

        // Process Book Title
        if (bookMeta.hasTitle()) {
            // Run title through filter.
            message = bookMeta.getTitle();
            FilterContext filterTask = new FilterContext(new ColoredString(message),
                    filterService.getAuthor(player.getUniqueId()), this);
            ruleChain.execute(filterTask, filterService);
            if (filterTask.isCancelled()) event.setCancelled(true);
            if (filterTask.messageChanged()) {
                bookMeta.setTitle(filterTask.getModifiedMessage().getRaw());
                event.setNewBookMeta(bookMeta);
            }
        }

        // Process Book Text
        if (bookMeta.hasPages()) {
            List<String> newPages = new ArrayList<>();
            boolean modified = false;
            for (String page : bookMeta.getPages()) {
                FilterContext state = new FilterContext(new ColoredString(page),
                        filterService.getAuthor(player.getUniqueId()), this);
                ruleChain.execute(state, filterService);
                if (state.isCancelled()) {
                    event.setCancelled(true);
                }
                if (state.messageChanged()) {
                    page = state.getModifiedMessage().getRaw();
                    modified = true;
                }
                newPages.add(page);
            }
            if (modified) {
                bookMeta.setPages(newPages);
                event.setNewBookMeta(bookMeta);
            }
        }

    }


    /**
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    @Override

    public void activate() {
        if (isActive()) return;

        PluginManager pm = Bukkit.getPluginManager();
        EventPriority priority = BukkitConfig.getBookpriority();

        if (BukkitConfig.bookfilterEnabled()) {
            try {
                ruleChain = getCompiledChain(filterService.getConfig().getRuleFile("book.txt"));
                pm.registerEvent(PlayerEditBookEvent.class, this, priority,
                        (l, e) -> onBookEdit((PlayerEditBookEvent) e),
                        PwnFilterBukkitPlugin.getInstance());
                setActive();
                plugin.getLogger().info("Activated BookListener with Priority Setting: " + priority.toString()
                        + " Rule Count: " + getRuleChain().ruleCount());
            } catch (InvalidChainException e) {
                plugin.getLogger().severe("Unable to activate BookListener.  Error: " + e.getMessage());
                setInactive();
            }
        }
    }

}

