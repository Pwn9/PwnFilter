

/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine;

import com.google.common.collect.Sets;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.StatsTracker;
import com.pwn9.filter.engine.rules.action.RegisterActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Handle Startup / Shutdown / Configuration of our PwnFilter Clients
 * User: ptoal
 * Date: 13-09-29
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class FilterEngine {

    private final StatsTracker statsTracker;
    private final Set<FilterClient> registeredClients = Sets.newConcurrentHashSet();

    public FilterEngine(StatsTracker statsTracker) {
        this.statsTracker = statsTracker;
        RegisterActions.all();
    }

    /**
     * <p>getActiveClients.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<FilterClient> getActiveClients() {
        List<FilterClient> result = new ArrayList<FilterClient>();
        for (FilterClient f : registeredClients) {
            if (f.isActive()) result.add(f);
        }
        return result;
    }

    /**
     * <p>Getter for the field <code>registeredClients</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Set<FilterClient> getRegisteredClients() {
        return registeredClients;
    }

    /**
     * <p>enableClients.</p>
     */
    public void enableClients() {

        for (FilterClient f : registeredClients) {
            f.activate();
        }

    }

    /**
     * <p>disableClients.</p>
     */
    public void disableClients() {
        for (FilterClient f : getActiveClients()) {
            f.shutdown();
        }
    }

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
     */
    public void registerClient(FilterClient f) {
        if (registeredClients.contains(f)) {
            return;
        }
        registeredClients.add(f);
        statsTracker.updateClients(getActiveClients());
    }

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
    public boolean unregisterClient(FilterClient f) {
        if (registeredClients.contains(f)) {
            registeredClients.remove(f);
            statsTracker.updateClients(getActiveClients());
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>unregisterAllClients.</p>
     */
    public void unregisterAllClients() {
        for (FilterClient f : registeredClients ) {
            f.shutdown();
            registeredClients.remove(f);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        disableClients();
    }

}
