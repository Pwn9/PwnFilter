package com.pwn9.PwnFilter.listener;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;


/**
 * Listen for Sign Change events and apply the filter to the text.
 */

public class PwnFilterSignListener implements Listener {
    private final PwnFilter plugin;
    public PwnFilterSignListener(PwnFilter p) {
        plugin = p;
        PluginManager pm = Bukkit.getPluginManager();

        // Now register the listener with the appropriate priority
        pm.registerEvent(SignChangeEvent.class, this, p.signPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onSignChange((SignChangeEvent)e); }
                },
                plugin);

    }
    // This is the handler
    public void onSignChange(SignChangeEvent event) {
        if (event.isCancelled()) return;

        // Permissions Check, if player has bypass permissions, then skip everything.
        if (event.getPlayer().hasPermission("pwnfilter.bypass.signs")) {
            return;
        }

        plugin.filterSign(event);
    }
}
