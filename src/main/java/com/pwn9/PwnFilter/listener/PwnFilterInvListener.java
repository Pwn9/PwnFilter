
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

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.RuleManager;
import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;


/**
 * Listen for Sign Change events and apply the filter to the text.
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class PwnFilterInvListener extends BaseListener {

    /**
     * <p>Constructor for PwnFilterInvListener.</p>
     *
     * @param p a {@link com.pwn9.PwnFilter.PwnFilter} object.
     */
    public PwnFilterInvListener(PwnFilter p) {
        super(p);
    }

    /** {@inheritDoc} */
    @Override
    public String getShortName() {
        return "ITEM";
    }

    // This is the handler
    /**
     * <p>onInventoryEvent.</p>
     *
     * @param event a {@link org.bukkit.event.inventory.InventoryClickEvent} object.
     */
    public void onInventoryEvent(InventoryClickEvent event) {
        Player player;
        String message;
        // Don't process already cancelled events.
        if (event.isCancelled()) return;

        // Only interested in checking when the Player is getting an item from the
        // Anvil result slot.
        if (event.getSlotType() != InventoryType.SlotType.RESULT ||
                event.getInventory().getType() != InventoryType.ANVIL) return;

        // Make sure that a real player is the one who clicked this.
        if (event.getWhoClicked().getType() == EntityType.PLAYER) {
            player = (Player)event.getWhoClicked();
        } else {
            return;
        }

        ItemStack item = event.getCurrentItem();
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta != null && itemMeta.hasDisplayName()) {
            message = itemMeta.getDisplayName();

            FilterState state = new FilterState(plugin, message, player, this);

            ruleChain.execute(state);
            if (state.cancel) event.setCancelled(true);

            // Only update the message if it has been changed.
            if (state.messageChanged()){
                ItemStack newItem = new ItemStack(item);
                ItemMeta newItemMeta = newItem.getItemMeta();
                newItemMeta.setDisplayName(state.getModifiedMessage().getColoredString());
                newItem.setItemMeta(newItemMeta);
                event.setCurrentItem(newItem);
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

        setRuleChain(RuleManager.getInstance().getRuleChain("item.txt"));

        PluginManager pm = Bukkit.getPluginManager();
        EventPriority priority = EventPriority.valueOf(config.getString("itempriority", "LOWEST").toUpperCase());

        if (!active && config.getBoolean("itemfilter")) {
            // Now register the listener with the appropriate priority
            pm.registerEvent(InventoryClickEvent.class, this, priority,
                    new EventExecutor() {
                        public void execute(Listener l, Event e) { onInventoryEvent((InventoryClickEvent) e); }
                    },
                    plugin);
            setActive();
            LogManager.logger.info("Activated ItemListener with Priority Setting: " + priority.toString()
                    + " Rule Count: " + getRuleChain().ruleCount() );

        }
    }
}

