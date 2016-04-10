/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.action.core;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.rules.action.InvalidActionException;

import java.util.Random;

/**
 * Replace the matched text with a random selection from a | seperated
 * list of text.
 *
 * @author Sage905
 * @version $Id: $Id
 */

public class RandomReplace implements Action {
    private static final Random random = new Random();

    // a String array of options to chose from for replacement.
    private final String[] replacementArray;

    private RandomReplace(String[] replaceArray) {
        replacementArray = replaceArray;
    }

    static Action getAction(String s) throws InvalidActionException
    {
        String[] toRand = s.split("\\|");
        if (toRand[0].isEmpty())
            throw new InvalidActionException(
                    "'randrep' requires at least one replacement string."
            );
        return new RandomReplace(toRand);
    }

    @Override
    public void execute(final FilterContext filterTask, FilterService filterService) {
        int randomInt = random.nextInt(replacementArray.length);
        filterTask.setModifiedMessage(filterTask.getModifiedMessage().
                replaceText(filterTask.getPattern(),
                        replacementArray[randomInt]));
    }
}
