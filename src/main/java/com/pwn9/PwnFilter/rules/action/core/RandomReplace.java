/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action.core;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.rules.action.Action;

import java.util.Random;

/**
 * Replace the matched text with a random selection from a | seperated list of text.
 *
 * @author ptoal
 * @version $Id: $Id
 */
@SuppressWarnings("UnusedDeclaration")
public class RandomReplace implements Action {
    private static Random random = new Random();

    // toRand is a String array of options to chose from for replacement.
    String[] toRand;

    /** {@inheritDoc} */
    public void init(String s)
    {
        toRand = s.split("\\|");
        if (toRand[0].isEmpty()) throw new IllegalArgumentException("'randrep' requires at least one replacement string.");
    }

    /** {@inheritDoc} */
    public boolean execute(final FilterState state ) {
        int randomInt = random.nextInt(toRand.length);
        state.setModifiedMessage(state.getModifiedMessage().replaceText(state.pattern,toRand[randomInt]));

        if (state.rule.modifyRaw())
            state.setUnfilteredMessage(state.getUnfilteredMessage().replaceText(state.pattern,toRand[randomInt]));

        return true;
    }
}
