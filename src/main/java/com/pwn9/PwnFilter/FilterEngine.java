/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.rules.action.RegisterActions;
import com.pwn9.PwnFilter.util.tags.RegisterTags;

import java.util.HashSet;
import java.util.Set;

/**
 * This is the main PwnFilter Engine.  It is designed to run independently of
 * a particular plugin format.  It should be able to handle Bukkit, as well as
 * Sponge and others.
 *
 * //TODO: This needs to have non-Bukkit routines moved over from PwnFilterPlugin
 *
 * Created by ptoal on 15-09-04.
 */
public class FilterEngine {

    private static FilterEngine _instance = new FilterEngine();
    private static Set<Class<? extends MessageAuthor>> authorClasses =
            new HashSet<Class<? extends MessageAuthor>>();

    private FilterEngine() {
        RegisterActions.all();
        RegisterTags.all();
    }

    public static FilterEngine getInstance() {
        return _instance;
    }

    public void registerAuthorClass(Class<? extends MessageAuthor> authorClass) {
        authorClasses.add(authorClass);
    }

    public void addPermission(String permission) {

    }
}
