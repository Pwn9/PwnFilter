/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.rules.action.ActionFactory;
import com.pwn9.filter.engine.rules.action.core.Abort;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Sage905 on 2016-03-19.
 */
public class ActionFactoryTest {

    @Test
    public void testActionFactoryReturnsAction() {
        ActionFactory actionFactory = new ActionFactory();
        Action result = actionFactory.getActionFromString("abort");
        assertEquals(result, Abort.INSTANCE);
    }
}
