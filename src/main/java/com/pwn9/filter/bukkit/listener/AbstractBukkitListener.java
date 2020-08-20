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

import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Cancellable;
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
abstract class AbstractBukkitListener implements FilterClient, Listener {
    protected final FilterService filterService;
    volatile RuleChain ruleChain;
    private boolean active;
    protected final PwnFilterPlugin plugin;

    AbstractBukkitListener(PwnFilterPlugin plugin) {
        this.filterService = plugin.getFilterService();
        this.plugin = plugin;
    }

    RuleChain getCompiledChain(File ruleFile) throws InvalidChainException {
        return filterService.parseRules(ruleFile);
    }

    @Override
    public FilterService getFilterService() {
        return filterService;
    }

    @Override
    public RuleChain getRuleChain() {
        return ruleChain;
    }

// --Commented out by Inspection START (20/08/2020 2:38 am):
//    public void loadRuleChain(File path) throws InvalidChainException {
//        ruleChain = getCompiledChain(path);
//    }
// --Commented out by Inspection STOP (20/08/2020 2:38 am)

    void loadRuleChain(String name) throws InvalidChainException {
        ruleChain = getCompiledChain(filterService.getConfig().getRuleFile(name));
    }


    @Override
    public boolean isActive() {
        return active;
    }

    void setActive() {
        active = true;
    }

    void setInactive() {
        active = false;
    }

    /**
     * Shutdown this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the activate / shutdown methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p>
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

    @SuppressWarnings("WeakerAccess")
    protected boolean checkIfSpam(MessageAuthor minecraftPlayer, String message, Cancellable event){
        if (plugin.checkRecentMessage(minecraftPlayer.getId(),message)) {
            event.setCancelled(true);
            minecraftPlayer.sendMessage(TextComponent.of("[PwnFilter] ").color(NamedTextColor.DARK_RED)
                  .append(TextComponent.of("Repeated command blocked by spam filter.").color(NamedTextColor.RED)));
            return true;
        }
        plugin.addRecentMessage(minecraftPlayer.getId(), message);
        return false;
    }
}
