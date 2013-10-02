package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.RuleManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

/**
* Listen for Chat events and apply the filter.
*/

public class PwnFilterPlayerListener extends BaseListener {

    public String getShortName() {
        return "CHAT";
    }

	public PwnFilterPlayerListener(PwnFilter p) {
        super(p);
        setRuleChain(RuleManager.getInstance().getRuleChain("chat.txt"));
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup player messages on quit
        if (event.getPlayer() != null && PwnFilter.lastMessage.containsKey(event.getPlayer())) {
            PwnFilter.lastMessage.remove(event.getPlayer());
        }
    }

    public void onPlayerChat(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        DataCache dCache = DataCache.getInstance();

        // Permissions Check, if player has bypass permissions, then skip everything.
        if (dCache.hasPermission(player,"pwnfilter.bypass.chat")) return;

        String message = event.getMessage();

        // Global mute
        if ((PwnFilter.pwnMute) && (!(dCache.hasPermission(player, "pwnfilter.bypass.mute")))) {
            event.setCancelled(true);
            return; // No point in continuing.
        }

        if (plugin.getConfig().getBoolean("spamfilter") && !dCache.hasPermission(player,"pwnfilter.bypass.spam")) {
            // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
            if (PwnFilter.lastMessage.containsKey(player) && PwnFilter.lastMessage.get(player).equals(message)) {
                event.setCancelled(true);
                return;
            }
            PwnFilter.lastMessage.put(player, message);

        }

        FilterState state = new FilterState(plugin, message, event.getPlayer(), this);

        // Global decolor
        if ((PwnFilter.decolor) && !(dCache.hasPermission(player, "pwnfilter.color"))) {
            // We are changing the state of the message.  Let's do that before any rules processing.
            state.message.decolor();
        }

        // Take the message from the ChatEvent and send it through the filter.
        ruleChain.apply(state);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setMessage(state.message.getColoredString());
        }
        if (state.cancel) event.setCancelled(true);
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

        if (isActive()) return;

        PluginManager pm = Bukkit.getServer().getPluginManager();
        EventPriority priority = EventPriority.valueOf(config.getString("chatpriority", "LOWEST").toUpperCase());

        /* Hook up the Listener for PlayerChat events */
        pm.registerEvent(AsyncPlayerChatEvent.class, this, priority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerChat((AsyncPlayerChatEvent)e); }
                },
                plugin);

        pm.registerEvent(PlayerQuitEvent.class, this, priority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onPlayerQuit((PlayerQuitEvent)e); }
                },
                plugin);

        PwnFilter.logger.info("Activated PlayerListener with Priority Setting: " + priority.toString()
                + " Rule Count: " + getRuleChain().ruleCount() );

        setActive();

    }

}


