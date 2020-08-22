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

import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * A generic Minecraft API interface containing all the calls we require into
 * the server.
 * <p>
 * Created by Sage905 on 15-09-11.
 */
public interface MinecraftAPI {

    void reset();

    boolean burn(UUID uuid, final int duration, final String messageString);

    void sendMessage(UUID uuid, final String message);

    void sendMessages(UUID uuid, final List<String> messages);

    void executePlayerCommand(UUID uuid, final String command);

    boolean withdrawMoney(UUID uuid, final Double amount, final String messageString);

    void kick(UUID uuid, final String messageString);

    void kill(UUID uuid, final String messageString);

    String getPlayerWorldName(UUID uuid);

    String getPlayerName(UUID uuid);

    // Null return is a failure of the API to get the answer.
    @Nullable
    Boolean playerIdHasPermission(UUID u, String s);

    // Console APIs

    void sendConsoleMessage(String message);

    void sendConsoleMessage(TextComponent message);

    void sendConsoleMessages(List<String> messageList);

    void sendBroadcast(String message);

    void sendBroadCast(TextComponent component);

    void sendBroadcast(List<String> messageList);

    void executeCommand(String command);

    boolean globalMute();

    void setMutStatus(boolean status);

    AudienceProvider audiences();
}
