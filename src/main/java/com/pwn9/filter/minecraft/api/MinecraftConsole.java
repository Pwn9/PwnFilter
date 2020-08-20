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

package com.pwn9.filter.minecraft.api;

import com.pwn9.filter.engine.api.MessageAuthor;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * A Bukkit Console Abstraction
 * Created by Sage905 on 15-09-08.
 */
public class MinecraftConsole implements MessageAuthor {

    private final MinecraftAPI minecraftAPI;

    public MinecraftConsole(MinecraftAPI minecraftAPI) {
        this.minecraftAPI = minecraftAPI;
    }

    @Override
    public boolean hasPermission(String permString) {
        return true;
    }

    @NotNull
    @Override
    public String getName() {
        return "CONSOLE";
    }

    @NotNull
    @Override
    public UUID getId() {
        return java.util.UUID.fromString("CONSOLE");
    }

    @Override
    public void sendMessage(final String message) {
        minecraftAPI.sendConsoleMessage(message);
    }

    @Override
    public void sendMessages(final List<String> messageList) {
        minecraftAPI.sendConsoleMessages(messageList);
    }

    @Override
    public void sendMessage(TextComponent message) {
        minecraftAPI.sendConsoleMessage(message);
    }

    public void sendBroadcast(final List<String> preparedMessages) {
        minecraftAPI.sendBroadcast(preparedMessages);

    }

    public void sendBroadcast(final String message) {
        minecraftAPI.sendBroadcast(message);

    }

    public void executeCommand(final String command) {
        minecraftAPI.executeCommand(command);
    }

}
