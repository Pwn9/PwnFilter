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

package com.pwn9.filter.engine.api;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * This is returned by the engine when an author for a message can not be found.
 * <p>
 * Created by Sage905 on 2016-04-25.
 */
public final class UnknownAuthor implements MessageAuthor {

    private final UUID id;

    public UnknownAuthor(UUID uuid) {
        this.id = uuid;
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void sendMessage(String message) {
        // Silently do nothing, since we don't know who this is.
    }

    @Override
    public void sendMessages(List<String> messages) {
        // Silently do nothing, since we don't know who this is.
    }

    @Override
    public void sendMessage(TextComponent message) {
        // Silently do nothing, since we don't know who this is.
    }
}
