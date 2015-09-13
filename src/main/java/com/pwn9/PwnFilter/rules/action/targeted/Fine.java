/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action.targeted;

import com.pwn9.PwnFilter.FilterTask;
import com.pwn9.PwnFilter.minecraft.api.MinecraftPlayer;
import com.pwn9.PwnFilter.minecraft.util.DefaultMessages;
import com.pwn9.PwnFilter.rules.action.Action;

/**
 * Fine the user by extracting money from his economy account.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Fine implements Action {

    String messageString; // Message to apply to this action
    double fineAmount; // How much to fine the player.

    /** {@inheritDoc} */
    public void init(String s)
    {
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
    public void execute(final FilterTask filterTask) {

        if (filterTask.getAuthor() instanceof MinecraftPlayer) {

            MinecraftPlayer p = (MinecraftPlayer)filterTask.getAuthor();
            if (p.withdrawMoney(fineAmount, messageString)) {
                filterTask.addLogMessage(String.format("Fined %s : %f", filterTask.getAuthor().getName(), fineAmount));
            } else {
                filterTask.addLogMessage(String.format("Failed to fine %s.",
                        filterTask.getAuthor().getName()));
            }

        }
    }


}
