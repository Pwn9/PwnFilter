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

import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.targeted.BurnTarget;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class TestAuthor implements MessageAuthor, BurnTarget {

    private boolean burnt;
    private final UUID id;

    public TestAuthor() {
        id = UUID.randomUUID();
    }

    public TestAuthor(UUID id) {
        this.id = id;
    }

    @Override
    public boolean hasPermission(String permString) {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return "Sage905";
    }

    @Override
    public void sendMessage(String message) {
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void sendMessages(List<String> messages) {
    }

    @Override
    public boolean burn(int duration, String message) {
        return burnt = true;
    }

    boolean burnt() {
        return burnt;}
}
