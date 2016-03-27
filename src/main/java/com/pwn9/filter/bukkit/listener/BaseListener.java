
/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.rules.chain.Chain;
import com.pwn9.filter.engine.rules.chain.InvalidChain;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.InvalidObjectException;

/**
 * User: Sage905
 * Date: 13-10-02
 * Time: 2:04 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */
public abstract class BaseListener implements FilterClient, Listener {
    boolean active;
    protected final PwnFilterPlugin plugin;
    protected volatile RuleChain ruleChain;


    /**
     * <p>Constructor for BaseListener.</p>
     *
     */
    BaseListener(PwnFilterPlugin plugin) {
        this.plugin = plugin;
    }

    RuleChain getCompiledChain(String path) throws InvalidConfigurationException, InvalidObjectException {
        Chain newChain = plugin.getFilterService().parseRules(new File(path));

        if (newChain instanceof RuleChain) {
            return (RuleChain) newChain;
        } else if (newChain instanceof InvalidChain) {
            throw new InvalidConfigurationException(
                    ((InvalidChain) newChain).getErrorMessage());
        } else
            throw new InvalidObjectException("Unknown Chain returned from Parser");
    }

    @Override
    public FilterService getFilterService() {
        return plugin.getFilterService();
    }

    @Override
    public RuleChain getRuleChain() {
        return ruleChain;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * <p>Setter for the field <code>active</code>.</p>
     */
    void setActive() {
        active = true;
    }

    /**
     * <p>setInactive.</p>
     */
    protected void setInactive() {
        active = false;
    }

    /**
     * {@inheritDoc}
     *
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
            setInactive();
        }
    }

}
