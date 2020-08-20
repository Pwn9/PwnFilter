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

package com.pwn9.filter.bukkit;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.pwn9.filter.engine.api.CommandSender;
import com.pwn9.filter.engine.api.Player;
import com.pwn9.filter.engine.rules.action.targeted.BurnTarget;
import com.pwn9.filter.engine.rules.action.targeted.FineTarget;
import com.pwn9.filter.engine.rules.action.targeted.KickTarget;
import com.pwn9.filter.engine.rules.action.targeted.KillTarget;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Author of a text string sent to us by Bukkit.  This is typically a player.
 * These objects are transient, and only last for as long as the message does.
 * <p>
 * Created by Sage905 on 15-08-31.
 */
public class BukkitPlayer implements Player, CommandSender, FineTarget, BurnTarget, KillTarget, KickTarget {

    static final int MAX_CACHE_AGE_SECS = 60; //

    private final MinecraftAPI minecraftAPI;
    private final UUID playerId;
    private final Stopwatch stopwatch;
    private final ConcurrentHashMap<String, Boolean> playerPermCache =
            new ConcurrentHashMap<>(16, 0.9f, 1); // Optimizations for Map
    private String playerName = "";

    BukkitPlayer(UUID uuid, MinecraftAPI api) {
        this.playerId = uuid;
        this.minecraftAPI = api;
        this.stopwatch = Stopwatch.createStarted();
    }

    // For testing
    BukkitPlayer(UUID uuid, MinecraftAPI api, Ticker ticker) {
        this.playerId = uuid;
        this.minecraftAPI = api;
        this.stopwatch = Stopwatch.createStarted(ticker);
    }

    @Override
    public boolean hasPermission(String permString) {

        // We are caching permissions, so we don't have to ask the API every time,
        // as that could get expensive for complex rulechains.  Every MAX_CACHE_AGE_SECS
        // we invalidate the cache on access.  This should have us asking if a player
        // has any given perm only 1 or 2 times every MAX_CACHE_AGE_SECS

        if (stopwatch.elapsed(TimeUnit.SECONDS) > MAX_CACHE_AGE_SECS) {
            stopwatch.reset();
            stopwatch.start();
            playerPermCache.clear();
        }

        Boolean hasPerm = playerPermCache.get(permString);

        if (hasPerm == null) {
            Boolean newPerm = minecraftAPI.playerIdHasPermission(playerId, permString);
            if (newPerm != null)
                playerPermCache.putIfAbsent(permString, newPerm);
        }
        // At this point, the player should be in the cache if they are online.
        // If player is offline, or there is an API failure, returns null

        hasPerm = playerPermCache.get(permString);

        return hasPerm != null && hasPerm;
    }

    @NotNull
    @Override
    public String getName() {
        if (playerName.isEmpty()) {
            String name = minecraftAPI.getPlayerName(playerId);
            if (name != null) playerName = name;
        }
        return playerName;
    }

    @NotNull
    @Override
    public UUID getId() {
        return playerId;
    }

    public MinecraftAPI getMinecraftAPI() {
        return minecraftAPI;
    }

    // Not cached.
    public String getWorldName() {
        return minecraftAPI.getPlayerWorldName(playerId);
    }


    public boolean burn(final int duration, final String messageString) {
        return minecraftAPI.burn(playerId, duration, messageString);
    }

    @Override
    public void sendMessage(final String message) {
        minecraftAPI.sendMessage(playerId, message);
    }

    @Override
    public void sendMessages(final List<String> messages) {
        minecraftAPI.sendMessages(playerId, messages);
    }

    @Override
    public void sendMessage(TextComponent message) {
        minecraftAPI.audiences().player(playerId).sendMessage(message);
    }

    public void executeCommand(final String command) {
        minecraftAPI.executePlayerCommand(playerId, command);
    }

    public boolean fine(final Double amount, final String messageString) {
        return minecraftAPI.withdrawMoney(playerId, amount, messageString);
    }

    public void kick(final String messageString) {
        minecraftAPI.kick(playerId, messageString);
    }

    public void kill(final String messageString) {
        minecraftAPI.kill(playerId, messageString);
    }

    @Override
    public String getPlace() {
        return minecraftAPI.getPlayerWorldName(playerId);
    }
}
