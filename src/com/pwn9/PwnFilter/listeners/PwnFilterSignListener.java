package com.pwn9.PwnFilter.listeners;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;


/**
 * Created with IntelliJ IDEA.
 * User: ptoal
 * Date: 13-04-02
 * Time: 11:24 PM
 */

public class PwnFilterSignListener implements Listener {
    private final PwnFilter plugin;
    public PwnFilterSignListener(PwnFilter plugin) {
        this.plugin = plugin;
    }
    // This is the handler
    public void onSignChange(SignChangeEvent event) {
        plugin.filterSign(event);
    }
}
