package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.rules.RuleChain;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

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

    /**
     * Add a listener to the PwnFilter ListenerManager.  This allows PwnFilter
     * to notify the listener when it should try to activate or shutdown.
     * PwnFilter will call the activate / shutdown methods when reloading
     * rules configs.
     *
     * The FilterListener must register *before* attempting to use any other
     * PwnFilter resources.
     *
     * @param f FilterListener instance
     * @param p Plugin that the listener belongs to.
     * @return
     */
    public boolean registerListener(FilterListener f, Plugin p );

    /**
     * Remove a listener from the PwnFilter ListenerManager.
     * The listener will no longer be activated / deactivated when PwnFilter
     * reloads configs, rules, etc.
     * IMPORTANT: Before de-registering, the FilterListener must remove all
     * references to RuleSets.
     *
     * @param f FilterListener to remove.
     * @return true if the listener was previously registered and successfully
     * removed. False if it was not registered.
     */
    public boolean unregisterListener(FilterListener f);


}
