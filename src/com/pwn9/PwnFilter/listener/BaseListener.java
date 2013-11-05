
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
 */
public abstract class BaseListener implements FilterClient {
    protected final PwnFilter plugin;
    protected boolean active;
    protected RuleChain ruleChain;

    public BaseListener(PwnFilter p) {
        plugin = p;
    }


    protected void setRuleChain(RuleChain rc) {
        ruleChain = rc;
        rc.loadConfigFile();
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

    protected void setActive() {
        active = true;
    }

    protected void setInactive() {
        active = false;
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
            setInactive();
        }
    }
}
