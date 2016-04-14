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

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.rules.chain.RuleChain;

/**
 * Created by Sage905 on 2016-03-27.
 */
public class TestClient implements FilterClient {

    private final String name;
    private boolean active;

    public TestClient() {
        this.name = "TEST";
    }

    public TestClient(String name) {
        this.name = name;
    }

    @Override
    public String getShortName() {
        return name;
    }

    @Override
    public FilterService getFilterService() {
        return null;
    }

    @Override
    public RuleChain getRuleChain() {
        return null;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void shutdown() {
        active = false;

    }
}
