
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

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.RuleChain;
import com.pwn9.PwnFilter.rules.RuleManager;
import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import java.util.List;

/**
* Apply the filter to commands.
*/

public class PwnFilterCommandListener extends BaseListener {

    public List<String> cmdlist;
    public List<String> cmdblist;
    public List<String> cmdchat;
    private RuleChain chatRuleChain;

    public String getShortName() { return "COMMAND" ;}

    public PwnFilterCommandListener(PwnFilter p) {
	    super(p);
    }

    public void activate(Configuration config) {
        if (isActive()) return;

        cmdlist = plugin.getConfig().getStringList("cmdlist");
        cmdblist = plugin.getConfig().getStringList("cmdblist");
        cmdchat = plugin.getConfig().getStringList("cmdchat");

        setRuleChain(RuleManager.getInstance().getRuleChain("command.txt"));
        chatRuleChain = RuleManager.getInstance().getRuleChain("chat.txt");

        EventPriority priority = EventPriority.valueOf(config.getString("cmdpriority", "LOWEST").toUpperCase());
        if (config.getBoolean("commandfilter")) {
            PluginManager pm = Bukkit.getPluginManager();
            pm.registerEvent(PlayerCommandPreprocessEvent.class, this, priority,
                    new EventExecutor() {
                public void execute(Listener l, Event e) { eventProcessor((PlayerCommandPreprocessEvent) e); }
            },
            plugin);
            setActive();
            LogManager.logger.info("Activated CommandListener with Priority Setting: " + priority.toString()
                    + " Rule Count: " + getRuleChain().ruleCount() );

            StringBuilder sb = new StringBuilder("Commands to filter: ");
            for (String command : cmdlist) sb.append(command).append(" ");
            LogManager.getInstance().debugLow(sb.toString().trim());

            sb = new StringBuilder("Commands to never filter: ");
            for (String command : cmdblist) sb.append(command).append(" ");
            LogManager.getInstance().debugLow(sb.toString().trim());
        }
    }


    public void eventProcessor(PlayerCommandPreprocessEvent event) {

        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        DataCache dCache = DataCache.getInstance();

        if (dCache.hasPermission(player, "pwnfilter.bypass.commands")) return;

        String message = event.getMessage();

        //Gets the actual command as a string
        String cmdmessage = message.substring(1).split(" ")[0];

        FilterState state = new FilterState(plugin, message, player, this);

        // Check to see if we should treat this command as chat (eg: /tell)
        if (cmdchat.contains(cmdmessage)) {
            // Global mute
            if ((PwnFilter.pwnMute) && (!(dCache.hasPermission(player, "pwnfilter.bypass.mute")))) {
                event.setCancelled(true);
                return;
            }

            // Simple Spam filter TODO: Make # of repeat messages configurable (Will help with booscooldowns)
            if (plugin.getConfig().getBoolean("commandspamfilter") && !player.hasPermission("pwnfilter.bypass.spam")) {
                // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
                if (PwnFilter.lastMessage.containsKey(player) && PwnFilter.lastMessage.get(player).equals(message)) {
                    event.setCancelled(true);
                    return;
                }
                PwnFilter.lastMessage.put(player, message);
            }

            // Global decolor
            if ((PwnFilter.decolor) && !(dCache.hasPermission(player,"pwnfilter.color"))) {
                state.message.decolor();
            }

            chatRuleChain.execute(state);

        } else {

            if (!cmdlist.isEmpty() && !cmdlist.contains(cmdmessage)) return;
            if (cmdblist.contains(cmdmessage)) return;

            // Take the message from the Command Event and send it through the filter.

            ruleChain.execute(state);

        }

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            if (state.message.getPlainString().isEmpty()) {
                event.setCancelled(true);
                return;
            }
            event.setMessage(state.message.getColoredString());
        }

        if (state.cancel) event.setCancelled(true);

    }

}
