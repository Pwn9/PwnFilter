package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.rules.RuleChain;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.Listener;

/**
 * Listeners that can call PwnFilter
 * User: ptoal
 * Date: 13-09-28
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FilterListener extends Listener {

    /**
     * A short name for this filter to be used in log messages and statistics.
     * eg: CHAT, COMMAND, ANVIL, etc.
     *
     * @return String containing the listeners short name.
     */
    public String getShortName();

    /**
     *
      * @return The primary rulechain for this filter
     */
    public RuleChain getRuleChain();


    /**
     * @return True if this FilterListener is currently active
     */
    public boolean isActive();

    /**
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     *
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     *
     * @param config PwnFilter Configuration object, which the plugin can read for configuration
     *               information. (eg: config.getString("ruledir")
     */
    public void activate(Configuration config);

    /**
     * Shutdown this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the activate / shutdown methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     *
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    public void shutdown();

}
