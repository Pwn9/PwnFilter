/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.util.DefaultMessages;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Fine the user by extracting money from his economy account.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Actionfine implements Action {

    String messageString; // Message to apply to this action
    double fineAmount; // How much to fine the player.

    /** {@inheritDoc} */
    public void init(String s)
    {
        if (PwnFilter.economy == null) {
            throw new IllegalArgumentException("Parsed rule requiring an Economy, but one was not detected. " +
                    "Check Vault configuration, or remove 'then fine' rules.");
        }

        String[] parts;

        parts = s.split("\\s",2);
        try {
            fineAmount = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e ) {
            throw new IllegalArgumentException("'fine' action did not have a valid amount.");
        }

        messageString = DefaultMessages.prepareMessage((parts.length > 1)?parts[1]:"", "finemsg");
    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {

        if (state.getPlayer() == null) return false;

        if (PwnFilter.economy != null ) {
            EconomyResponse resp = PwnFilter.economy.withdrawPlayer(state.getPlayer(),fineAmount);
            if (resp.transactionSuccess()) {
                state.addLogMessage(String.format("Fined %s : %f",state.playerName,resp.amount));
            } else {
                state.addLogMessage(String.format("Failed to fine %s : %f. Error: %s",
                        state.playerName,resp.amount,resp.errorMessage));
                return false;
            }
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    state.getPlayer().sendMessage(messageString);
                }
            };
            task.runTask(state.plugin);
            return true;

        } else return false;
    }
}
