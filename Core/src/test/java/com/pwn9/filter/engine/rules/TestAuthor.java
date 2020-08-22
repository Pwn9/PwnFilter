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

import com.pwn9.filter.engine.api.CommandSender;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.targeted.BurnTarget;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TestAuthor implements MessageAuthor, BurnTarget, CommandSender {

    private final UUID id;
    private boolean burnt;
    private final Collection<String> messages = new LinkedList<>();

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
        this.messages.add(message);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void sendMessages(List<String> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public void sendMessage(TextComponent message) {
        this.messages.add(LegacyComponentSerializer.legacyAmpersand().serialize(message));
    }

    public Collection<String> getMessages(){
        return messages;
    }
    @Override
    public boolean burn(int duration, String message) {
        return burnt = true;
    }

    boolean burnt() {
        return burnt;
    }
}
