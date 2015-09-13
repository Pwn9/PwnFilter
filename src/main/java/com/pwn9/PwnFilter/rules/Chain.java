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
 *
 * @author ptoal
 * @version $Id: $Id
 */
public interface Chain {

    /**
     * <p>getConfigName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getConfigName();

    /**
     * <p>append.</p>
     *
     * @param r a {@link ChainEntry} object.
     */
    void append(ChainEntry r);

    /**
     * <p>isValid.</p>
     *
     * @return a boolean.
     */
    boolean isValid();

    /**
     * <p>getActionGroups.</p>
     *
     * @return a {@link com.google.common.collect.Multimap} object.
     */
    Multimap<String, Action> getActionGroups();

    /**
     * <p>getConditionGroups.</p>
     *
     * @return a {@link com.google.common.collect.Multimap} object.
     */
    Multimap<String, Condition> getConditionGroups();

    /**
     * <p>resetChain.</p>
     */
    void resetChain();

    /**
     * <p>addConditionGroup.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param cGroup a {@link java.util.List} object.
     */
    void addConditionGroup(String name, List<Condition> cGroup);

    /**
     * <p>addActionGroup.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param aGroup a {@link java.util.List} object.
     */
    void addActionGroup(String name, List<Action> aGroup);

    /**
     * <p>isEmpty.</p>
     *
     * @return a boolean.
     */
    boolean isEmpty();
}
