/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.FilterServiceImpl;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.rules.chain.RuleChainImpl;

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
    public FilterServiceImpl getFilterService() {
        return null;
    }

    @Override
    public RuleChainImpl getRuleChain() {
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
