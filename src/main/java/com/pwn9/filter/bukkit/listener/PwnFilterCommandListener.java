
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

import com.pwn9.filter.engine.api.FilterTask;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.minecraft.api.MinecraftPlayer;
import com.pwn9.filter.minecraft.util.ColoredString;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.rules.RuleChain;
import com.pwn9.filter.engine.rules.RuleManager;
import com.pwn9.filter.util.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

/**
 * Apply the filter to commands.
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class PwnFilterCommandListener extends BaseListener {

    private RuleChain chatRuleChain;

    /**
     * <p>getShortName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getShortName() { return "COMMAND" ;}

    /**
     * <p>Constructor for PwnFilterCommandListener.</p>
     *
     */
    public PwnFilterCommandListener() {
	    super();
    }

    /** {@inheritDoc} */
    public void activate() {
        if (isActive()) return;

        setRuleChain(RuleManager.getInstance().getRuleChain("command.txt"));
        chatRuleChain = RuleManager.getInstance().getRuleChain("chat.txt");


        EventPriority priority = BukkitConfig.getCmdpriority();
        if (BukkitConfig.cmdfilterEnabled()) {
            PluginManager pm = Bukkit.getPluginManager();
            pm.registerEvent(PlayerCommandPreprocessEvent.class, this, priority,
                    new EventExecutor() {
                public void execute(Listener l, Event e) { eventProcessor((PlayerCommandPreprocessEvent) e); }
            },
            PwnFilterPlugin.getInstance());
            setActive();
            LogManager.logger.info("Activated CommandListener with Priority Setting: " + priority.toString()
                    + " Rule Count: " + getRuleChain().ruleCount() );

            StringBuilder sb = new StringBuilder("Commands to filter: ");
            for (String command : BukkitConfig.getCmdlist()) sb.append(command).append(" ");
            LogManager.getInstance().debugLow(sb.toString().trim());

            sb = new StringBuilder("Commands to never filter: ");
            for (String command : BukkitConfig.getCmdblist()) sb.append(command).append(" ");
            LogManager.getInstance().debugLow(sb.toString().trim());
        }
    }


    /**
     * <p>eventProcessor.</p>
     *
     * @param event a {@link org.bukkit.event.player.PlayerCommandPreprocessEvent} object.
     */
    public void eventProcessor(PlayerCommandPreprocessEvent event) {

        if (event.isCancelled()) return;


        MinecraftPlayer minecraftPlayer = MinecraftPlayer.getInstance(event.getPlayer().getUniqueId());

        if (minecraftPlayer.hasPermission("pwnfilter.bypass.commands")) return;

        String message = event.getMessage();

        //Gets the actual command as a string
        String cmdmessage = message.substring(1).split(" ")[0];


        FilterTask filterTask = new FilterTask(new ColoredString(message), minecraftPlayer, this);

        // Check to see if we should treat this command as chat (eg: /tell)
        if (BukkitConfig.getCmdchat().contains(cmdmessage)) {
            // Global mute
            if ((BukkitConfig.isGlobalMute()) && (!minecraftPlayer.hasPermission("pwnfilter.bypass.mute"))) {
                event.setCancelled(true);
                return;
            }

            // Simple Spam filter
            if (BukkitConfig.commandspamfilterEnabled() && !minecraftPlayer.hasPermission("pwnfilter.bypass.spam")) {
                // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
                if (PwnFilterPlugin.lastMessage.containsKey(minecraftPlayer.getID()) && PwnFilterPlugin.lastMessage.get(minecraftPlayer.getID()).equals(message)) {
                    event.setCancelled(true);
                    minecraftPlayer.sendMessage(ChatColor.DARK_RED + "[PwnFilter]" + ChatColor.RED + " Repeated command blocked by spam filter.");
                    return;
                }
                PwnFilterPlugin.lastMessage.put(minecraftPlayer.getID(), message);
            }

            chatRuleChain.execute(filterTask);

        } else {

            if (!BukkitConfig.getCmdlist().isEmpty() && !BukkitConfig.getCmdlist().contains(cmdmessage)) return;
            if (BukkitConfig.getCmdblist().contains(cmdmessage)) return;

            // Take the message from the Command Event and send it through the filter.

            ruleChain.execute(filterTask);

        }

        // Only update the message if it has been changed.
        if (filterTask.messageChanged()){
            if (filterTask.getModifiedMessage().toString().isEmpty()) {
                event.setCancelled(true);
                return;
            }
            event.setMessage(filterTask.getModifiedMessage().getRaw());
        }

        if (filterTask.isCancelled()) event.setCancelled(true);

    }

}
