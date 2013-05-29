package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;


/**
 * Listen for Sign Change events and apply the filter to the text.
 */

public class PwnFilterInvListener implements Listener {
    private final PwnFilter plugin;
    public PwnFilterInvListener(PwnFilter p) {
        plugin = p;
        PluginManager pm = Bukkit.getPluginManager();

        // Now register the listener with the appropriate priority
        pm.registerEvent(InventoryClickEvent.class, this, PwnFilter.invPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onInventoryEvent((InventoryClickEvent) e); }
                },
                plugin);

        PwnFilter.logger.info("Activated ItemListener with Priority Setting: " + PwnFilter.invPriority.toString());

    }
    // This is the handler
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

        if (itemMeta.hasDisplayName()) {
            message = itemMeta.getDisplayName();

            FilterState state = new FilterState(plugin, message, player, PwnFilter.EventType.ITEM);

            PwnFilter.ruleset.runFilter(state);
            if (state.cancel) event.setCancelled(true);

            // Only update the message if it has been changed.
            if (state.messageChanged()){
                ItemStack newItem = new ItemStack(item);
                ItemMeta newItemMeta = newItem.getItemMeta();
                newItemMeta.setDisplayName(state.message.getColoredString());
                newItem.setItemMeta(newItemMeta);
                event.setCurrentItem(newItem);
            }



        }

    }

}
