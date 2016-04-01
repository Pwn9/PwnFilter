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
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.minecraft.api.MinecraftConsole;
import com.pwn9.filter.minecraft.util.ColoredString;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

/**
 * Apply the filter to commands.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class PwnFilterServerCommandListener extends BaseListener {

    private final PwnFilterPlugin plugin;

    /**
     * <p>Constructor for PwnFilterServerCommandListener.</p>
     *
     */
    public PwnFilterServerCommandListener(PwnFilterPlugin plugin) {
	    super(plugin.getFilterService());
        this.plugin = plugin;
    }

    /** {@inheritDoc} */
    @Override
    public String getShortName() {
        return "CONSOLE";
    }

    /**
     * <p>onServerCommandEvent.</p>
     *
     * @param event a {@link org.bukkit.event.server.ServerCommandEvent} object.
     */
    public void onServerCommandEvent(ServerCommandEvent event) {

        String command = event.getCommand();

        //Gets the actual command as a string
        String cmdmessage;
        try {
            cmdmessage = command.split(" ")[0];
        } catch (IndexOutOfBoundsException ex) {
            return;
        }

        if (!BukkitConfig.getCmdlist().isEmpty() && !BukkitConfig.getCmdlist().contains(cmdmessage)) return;
        if (BukkitConfig.getCmdblist().contains(cmdmessage)) return;

        FilterContext state = new FilterContext(new ColoredString(command), MinecraftConsole.getInstance(), this);

        // Take the message from the Command Event and send it through the filter.

        ruleChain.execute(state, plugin.getLogger());

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setCommand(state.getModifiedMessage().getRaw());
        }

        if (state.isCancelled()) event.setCommand("");

    }

    /**
     * {@inheritDoc}
     *
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p/>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    @Override
    public void activate() {
        if (isActive()) return;


        try {

            if (BukkitConfig.consolefilterEnabled()) {

                ruleChain = getCompiledChain("console.txt");
                PluginManager pm = Bukkit.getPluginManager();
                EventPriority priority = BukkitConfig.getCmdpriority();

                pm.registerEvent(ServerCommandEvent.class, this, priority,
                        new EventExecutor() {
                            public void execute(Listener l, Event e) {
                                onServerCommandEvent((ServerCommandEvent) e);
                            }
                        },
                        PwnFilterPlugin.getInstance());
                plugin.getLogger().info("Activated ServerCommandListener with Priority Setting: " + priority.toString()
                        + " Rule Count: " + getRuleChain().ruleCount());

                setActive();
            }
        } catch (InvalidChainException e) {
            plugin.getLogger().severe("Unable to activate ServerCommandListener.  Error: " + e.getMessage());
            setInactive();
        }
    }

}
