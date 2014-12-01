/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules;

import com.google.common.collect.Multimap;
import com.pwn9.PwnFilter.rules.action.Action;

import java.util.List;

/**
 * Interface for a RuleChain
 * User: ptoal
 * Date: 13-11-16
 * Time: 7:17 PM
 */
public interface Chain {

    public String getConfigName();

    public boolean append(ChainEntry r);

    public boolean isValid();

    public Multimap<String, Action> getActionGroups();

    public Multimap<String, Condition> getConditionGroups();

    public void resetChain();

    public void addConditionGroup(String name, List<Condition> cGroup);

    public void addActionGroup(String name, List<Action> aGroup);

    public boolean isEmpty();
}
