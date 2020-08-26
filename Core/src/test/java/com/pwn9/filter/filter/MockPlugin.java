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

package com.pwn9.filter.filter;

import com.google.common.collect.MapMaker;
import com.pwn9.filter.PwnFilterPlugin;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.FilterServiceImpl;
import com.pwn9.filter.engine.api.AuthorService;
import com.pwn9.filter.engine.api.Console;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.TestAuthor;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import com.pwn9.filter.minecraft.MinecraftConsole;


import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class MockPlugin implements PwnFilterPlugin {

    public static final ConcurrentMap<UUID, String> lastMessage = new MapMaker().concurrencyLevel(2).weakKeys().makeMap();
    private final FilterService filterServiceImpl = new FilterServiceImpl();
    private final MinecraftAPI minecraftAPI = new MockMinecraftAPI();
    private final static AuthorService authorService = new AuthorService() {
        final TestAuthor author = new TestAuthor();
        @Override
        public MessageAuthor getAuthorById(UUID uuid) {
            return author;
        }

        @Override
        public com.pwn9.filter.engine.api.CommandSender getSenderById(UUID uuid) {
            return author;
        }
    };

    @Override
    public FilterService getFilterService() {
        return filterServiceImpl;
    }

    @Override
    public Console getConsole() {
        return new MinecraftConsole(minecraftAPI);
    }

    public static AuthorService getMockAuthorService() {
        return authorService;
    }
    @Override
    public Logger getLogger() {
        return Logger.getAnonymousLogger();
    }

    @Override
    public MinecraftAPI getApi() {
        return minecraftAPI;
    }

    @Override
    public boolean configurePlugin() {
        return true;
    }

    @Override
    public boolean checkRecentMessage(UUID uuid, String string) {
        return (lastMessage.containsKey(uuid) && lastMessage.get(uuid).equals(string));

    }

    @Override
    public void addRecentMessage(UUID uuid, String string) {
        lastMessage.put(uuid,string);
    }


}
