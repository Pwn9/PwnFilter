
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

import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.rules.RuleChain;
import org.bukkit.event.HandlerList;

/**
 * User: ptoal
 * Date: 13-10-02
 * Time: 2:04 PM
 *
 * @author ptoal
 * @version $Id: $Id
 */
public abstract class BaseListener implements FilterClient {
    protected final PwnFilter plugin;
    protected boolean active;
    protected RuleChain ruleChain;

    /**
     * <p>Constructor for BaseListener.</p>
     *
     * @param p a {@link com.pwn9.PwnFilter.PwnFilter} object.
     */
    public BaseListener(PwnFilter p) {
        plugin = p;
    }


    /**
     * <p>Setter for the field <code>ruleChain</code>.</p>
     *
     * @param rc a {@link com.pwn9.PwnFilter.rules.RuleChain} object.
     */
    public void setRuleChain(RuleChain rc) {
        ruleChain = rc;
        rc.loadConfigFile();
    }

    /** {@inheritDoc} */
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
    protected void setActive() {
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
