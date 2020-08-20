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

import com.pwn9.filter.bukkit.PwnFilterBukkitPlugin;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.minecraft.util.ColoredString;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.PluginManager;

/**
 * Apply the filter to commands.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class PwnFilterServerCommandListener extends AbstractBukkitListener {

    public PwnFilterServerCommandListener(PwnFilterPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getShortName() {
        return "CONSOLE";
    }

    private void onServerCommandEvent(ServerCommandEvent event) {

        String command = event.getCommand();

        //Gets the actual command as a string
        String cmdmessage;
        try {
            cmdmessage = command.split(" ")[0];
        } catch (IndexOutOfBoundsException ex) {
            return;
        }

        if (!BukkitConfig.getCmdlist().isEmpty() && !BukkitConfig.getCmdlist().contains(cmdmessage))
            return;
        if (BukkitConfig.getCmdblist().contains(cmdmessage)) return;

        FilterContext state = new FilterContext(new ColoredString(command), plugin.getConsole(), this);

        // Take the message from the Command Event and send it through the filter.

        ruleChain.execute(state, filterService);

        // Only update the message if it has been changed.
        if (state.messageChanged()) {
            event.setCommand(state.getModifiedMessage().getRaw());
        }

        if (state.isCancelled()) event.setCommand("");

    }

    /**
     * {@inheritDoc}
     * <p>
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     */
    @Override
    public void activate() {
        if (isActive()) return;


        try {

            if (BukkitConfig.consolefilterEnabled()) {

                ruleChain = getCompiledChain(filterService.getConfig().getRuleFile("console.txt"));
                PluginManager pm = Bukkit.getPluginManager();
                EventPriority priority = BukkitConfig.getCmdpriority();

                pm.registerEvent(ServerCommandEvent.class, this, priority,
                        (l, e) -> onServerCommandEvent((ServerCommandEvent) e),
                        PwnFilterBukkitPlugin.getInstance());
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
