
/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.rules.chain.Chain;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;

/**
 * User: Sage905
 * Date: 13-10-02
 * Time: 2:04 PM
 *
 * @author Sage905
 * @version $Id: $Id
 */
abstract class BaseListener implements FilterClient, Listener {
    private boolean active;
    protected final FilterService filterService;
    volatile RuleChain ruleChain;

    BaseListener(FilterService filterService) {
        this.filterService = filterService;
    }

    RuleChain getCompiledChain(File ruleFile) throws InvalidChainException {
        Chain newChain = filterService.parseRules(ruleFile);
        return (RuleChain) newChain;
    }

    @Override
    public FilterService getFilterService() {
        return filterService;
    }

    @Override
    public RuleChain getRuleChain() {
        return ruleChain;
    }

    public void loadRuleChain(File path) throws InvalidChainException {
        ruleChain = getCompiledChain(path);
    }

    void loadRuleChain(String name) throws InvalidChainException {
        ruleChain = getCompiledChain(filterService.getConfig().getRuleFile(name));
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
    void setInactive() {
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
