package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.RuleChain;
import com.pwn9.PwnFilter.rules.RuleManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
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

public class PwnFilterInvListener implements FilterListener {

    private final PwnFilter plugin;
    private RuleChain ruleChain;
    private boolean active;

    public PwnFilterInvListener(PwnFilter p) {
        plugin = p;
        ruleChain = RuleManager.getInstance().getRuleChain("item.txt");

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

            FilterState state = new FilterState(plugin, message, player, this);

            ruleChain.apply(state);
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

    /**
     * A short name for this filter to be used in log messages and statistics.
     * eg: CHAT, COMMAND, ANVIL, etc.
     *
     * @return String containing the listeners short name.
     */
    @Override
    public String getShortName() {
        return "ITEM";
    }

    /**
     * @return The primary rulechain for this filter
     */
    @Override
    public RuleChain getRuleChain() {
        return ruleChain;
    }

    /**
     * @return True if this FilterListener is currently active
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p/>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     *
     * @param config PwnFilter Configuration object, which the plugin can read for configuration
     *               information. (eg: config.getString("ruledir")
     */
    @Override
    public void activate(Configuration config) {
        PluginManager pm = Bukkit.getPluginManager();
        EventPriority priority = EventPriority.valueOf(config.getString("itempriority", "LOWEST").toUpperCase());

        if (!active && config.getBoolean("itemfilter")) {
            // Now register the listener with the appropriate priority
            pm.registerEvent(InventoryClickEvent.class, this, priority,
                    new EventExecutor() {
                        public void execute(Listener l, Event e) { onInventoryEvent((InventoryClickEvent) e); }
                    },
                    plugin);
            active = true;
            PwnFilter.logger.info("Activated ItemListener with Priority Setting: " + priority.toString());
        }
    }

    /**
     * Shutdown this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the activate / shutdown methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p/>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    @Override
    public void shutdown() {
        if (active) {
            HandlerList.unregisterAll(this);
            active = false;
        }
    }
}
