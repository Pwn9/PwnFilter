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
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import com.pwn9.filter.minecraft.util.ColoredString;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;


/**
 * Apply the filter to commands.
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class PwnFilterCommandListener extends AbstractBukkitListener {
    private RuleChain chatRuleChain;

    public PwnFilterCommandListener(PwnFilterPlugin plugin) {
        super(plugin);
    }

    public String getShortName() {
        return "COMMAND";
    }

    @Override
    public void activate() {
        if (isActive()) return;

        PluginManager pm = Bukkit.getPluginManager();
        EventPriority priority = BukkitConfig.getCmdpriority();
        if (BukkitConfig.cmdfilterEnabled()) {
            try {
                ruleChain = getCompiledChain(filterService.getConfig().getRuleFile("command.txt"));
                chatRuleChain = getCompiledChain(filterService.getConfig().getRuleFile("chat.txt"));

                pm.registerEvent(PlayerCommandPreprocessEvent.class, this, priority,
                        (l, e) -> eventProcessor((PlayerCommandPreprocessEvent) e),
                        PwnFilterBukkitPlugin.getInstance());
                setActive();
                plugin.getLogger().info("Activated CommandListener with Priority Setting: " + priority.toString()
                        + " Rule Count: " + getRuleChain().ruleCount());

                StringBuilder sb = new StringBuilder("Commands to filter: ");
                for (String command : BukkitConfig.getCmdlist())
                    sb.append(command).append(" ");
                plugin.getLogger().finest(sb.toString().trim());

                sb = new StringBuilder("Commands to never filter: ");
                for (String command : BukkitConfig.getCmdblist())
                    sb.append(command).append(" ");
                plugin.getLogger().finest(sb.toString().trim());
            } catch (InvalidChainException e) {
                plugin.getLogger().severe("Unable to activate CommandListener.  Error: " + e.getMessage());
                setInactive();
            }
        }
    }


    /**
     * <p>eventProcessor.</p>
     *
     * @param event a {@link org.bukkit.event.player.PlayerCommandPreprocessEvent} object.
     */
    private void eventProcessor(PlayerCommandPreprocessEvent event) {

        if (event.isCancelled()) return;


        MessageAuthor minecraftPlayer = plugin.getFilterService().getAuthor((event.getPlayer().getUniqueId()));

        if (minecraftPlayer.hasPermission("pwnfilter.bypass.commands")) return;

        String message = event.getMessage();

        //Gets the actual command as a string
        String cmdmessage = message.substring(1).split(" ")[0];


        FilterContext filterTask = new FilterContext(new ColoredString(message), minecraftPlayer, this);

        // Check to see if we should treat this command as chat (eg: /tell)
        if (BukkitConfig.getCmdchat().contains(cmdmessage)) {
            // Global mute
            if ((BukkitConfig.globalMute()) && (!minecraftPlayer.hasPermission("pwnfilter.bypass.mute"))) {
                event.setCancelled(true);
                return;
            }

            // Simple Spam filter
            if (BukkitConfig.commandspamfilterEnabled()
                  && !minecraftPlayer.hasPermission("pwnfilter.bypass.spam")
                  && checkIfSpam(minecraftPlayer,message,event)) {
                // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
                return;
            }

            chatRuleChain.execute(filterTask, filterService);

        } else {

            if (!BukkitConfig.getCmdlist().isEmpty() && !BukkitConfig.getCmdlist().contains(cmdmessage))
                return;
            if (BukkitConfig.getCmdblist().contains(cmdmessage)) return;

            // Take the message from the Command Event and send it through the filter.

            ruleChain.execute(filterTask, filterService);

        }

        // Only update the message if it has been changed.
        if (filterTask.messageChanged()) {
            if (filterTask.getModifiedMessage().toString().isEmpty()) {
                event.setCancelled(true);
                return;
            }
            event.setMessage(filterTask.getModifiedMessage().getRaw());
        }

        if (filterTask.isCancelled()) event.setCancelled(true);

    }

}
