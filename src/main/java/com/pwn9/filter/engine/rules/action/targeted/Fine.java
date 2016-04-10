/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.targeted;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.action.InvalidActionException;

/**
 * Fine the user by extracting money from his economy account.
 *
 * @author Sage905
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class Fine implements Action {

    private final String messageString; // Message to apply to this action
    private final double fineAmount; // How much to fine the player.
    // Default message to apply to this burn action
    private static String defaultMessage = "";

    private Fine(String message, double fineAmount) {
        this.messageString = message;
        this.fineAmount = fineAmount;
    }

    /** {@inheritDoc} */
    public static Action getAction(String s) throws InvalidActionException
    {
        String[] parts;
        Double fineAmount;

        parts = s.split("\\s",2);
        try {
            fineAmount = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e ) {
            throw new InvalidActionException("'fine' action did not have a valid amount.");
        }

        return new Fine((parts.length > 1) ? parts[1] : defaultMessage, fineAmount);
    }
    public static void setDefaultMessage(String s) {
        defaultMessage = s;
    }

    /** {@inheritDoc} */
    public void execute(final FilterContext filterTask, FilterService filterService) {

        if (filterTask.getAuthor() instanceof FineTarget) {

            FineTarget target = (FineTarget)filterTask.getAuthor();
            if (target.fine(fineAmount, messageString)) {
                filterTask.addLogMessage(String.format("Fined %s : %f", filterTask.getAuthor().getName(), fineAmount));
            } else {
                filterTask.addLogMessage(String.format("Failed to fine %s.",
                        filterTask.getAuthor().getName()));
            }

        }
    }


}
