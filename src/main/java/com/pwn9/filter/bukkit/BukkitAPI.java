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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.api.AuthorService;
import com.pwn9.filter.engine.api.CommandSender;
import com.pwn9.filter.engine.api.NotifyTarget;
import com.pwn9.filter.minecraft.DeathMessages;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import com.pwn9.filter.minecraft.api.MinecraftConsole;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Handles keeping a cache of data that we need during Async event handling.
 * We can't get this data in our Async method, as Bukkit API calls are not threadsafe.
 * Also, we can't always schedule a task, because we might be running in the
 * main thread.
 * <p/>
 * This will cache data about players for 10s.
 */

public class BukkitAPI implements MinecraftAPI, AuthorService, NotifyTarget {

    private static BukkitAudiences audiences;
    private final PwnFilterPlugin plugin;
    private final Cache<UUID, BukkitPlayer> playerCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build();

    /*
    Note: The "load" call can cause blocking of the main Bukkit thread, if it is called
    from the main thread while there is an outstanding request.
     */

    @SuppressWarnings("WeakerAccess")
    protected BukkitAPI(PwnFilterPlugin p) {
        this.plugin = p;
        setup();
    }

    protected void setup(){
        if(plugin instanceof PwnFilterBukkitPlugin){ //avoid testing
            audiences = BukkitAudiences.create((Plugin) this.plugin);
        } else  {
            audiences = null;
        }
    }


    public BukkitPlayer getAuthorById(final UUID u) {
        /* Not sure if this is the best way of doing this, but we need to make
        certain that we do not block the main server thread while waiting for a
        FutureTask in the same thread when the Cache loads a missing value.
         */
        BukkitPlayer bPlayer;
        bPlayer = playerCache.getIfPresent(u);
        if (bPlayer == null) {
            Player onlinePlayer = safeBukkitAPICall(() -> Bukkit.getPlayer(u));
            if (onlinePlayer != null) {
                playerCache.asMap().putIfAbsent(u, new BukkitPlayer(u, this));
            }
        }
        // At this point, the player should be in the cache if they are online.
        // If player is offline, returns null
        return playerCache.getIfPresent(u);

    }

    @Override
    public CommandSender getSenderById(UUID uuid) {
        return getAuthorById(uuid);
    }

    public MinecraftConsole getConsole() {
        return plugin.getConsole();
    }

    @Override
    public synchronized void reset() {
        playerCache.invalidateAll();
    }


    @Nullable
    private <T> T safeBukkitAPICall(Callable<T> callable) {

        if (Bukkit.isPrimaryThread()) {
            // We are in the main thread, just execute API calls directly.
            try {
                return callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Red Alert, Shields Up.  We are an Async task.  Ask the main
            // thread to execute these calls, and return the Player data to
            // cache.
            if (plugin instanceof Plugin) {
                Future<T> task =
                        Bukkit.getScheduler().callSyncMethod((Plugin) plugin, callable);
                try {
                    // This will block the current thread for up to 3s
                    return task.get(3, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    plugin.getLogger().warning("Bukkit API call timed out (Waited >3s). Is the server busy?");
                    return null;
                } catch (InterruptedException e) {
                    plugin.getLogger().warning("Bukkit API call Interrupted.");
                } catch (ExecutionException e) {
                    plugin.getLogger().warning("Bukkit API call threw exception: " + e.getMessage());
                }
            } else throw new IllegalPluginAccessException();
        }
        return null;
    }

    private void safeBukkitDispatch(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            // We are in the main thread, just execute API calls directly.
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Red Alert, Shields Up.  We are an Async task.  Ask the main
            // thread to execute these calls, and return the Player data to
            // cache.
            if (plugin instanceof Plugin) {
                Bukkit.getScheduler().runTask((Plugin) plugin, runnable);
            } else throw new IllegalPluginAccessException();
        }
    }
    
    /*
      **********
      Player API
      **********
    */

    public Player getPlayerFromID(UUID id) {
        return safeBukkitAPICall(() -> Bukkit.getPlayer(UUID.randomUUID()));

    }

    // Get the player's Name (Works even if they are offline)
    @Nullable
    @Override
    public String getPlayerName(final UUID u) {
        return safeBukkitAPICall(() -> {
            OfflinePlayer p = Bukkit.getOfflinePlayer(u);
            if (p != null) return p.getName();
            return null;
        });
    }

    // Get the player's current world
    @Nullable
    @Override
    public String getPlayerWorldName(final UUID uuid) {
        return safeBukkitAPICall(() -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) return p.getWorld().getName();
            return null;
        });
    }

    // Check if a player has a perm. (not cached)
    @Override
    public Boolean playerIdHasPermission(final UUID u, final String s) {
        return safeBukkitAPICall(() -> {
            Player p = Bukkit.getPlayer(u);
            return p != null && p.hasPermission(s);
        });
    }

    @Override
    public boolean burn(final UUID uuid, final int duration, final String messageString) {
        safeBukkitDispatch(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player bukkitPlayer = Bukkit.getPlayer(uuid);
                        if (bukkitPlayer != null) {
                            bukkitPlayer.setFireTicks(duration);
                            audiences.audience(bukkitPlayer).sendMessage(LegacyComponentSerializer.legacySection().deserialize(messageString));
                        }
                    }
                });

        return true;
    }

    @Override
    public void sendMessage(final UUID uuid, final String message) {
        audiences.player(uuid).sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
    }

    @Override
    public void sendMessages(final UUID uuid, final List<String> messages) {
        messages.forEach(message ->
                audiences.player(uuid).sendMessage(LegacyComponentSerializer.legacySection().deserialize(message)));
    }

    @Override
    public void executePlayerCommand(final UUID uuid, final String command) {
        safeBukkitDispatch(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player bukkitPlayer = Bukkit.getPlayer(uuid);
                        if (bukkitPlayer != null) {
                            bukkitPlayer.performCommand(command);
                        }
                    }
                });
    }

    @Override
    public boolean withdrawMoney(final UUID uuid, final Double amount, final String messageString) {
        if (PwnFilterBukkitPlugin.economy != null) {
            Boolean result = safeBukkitAPICall(
                    () -> {
                        Player bukkitPlayer = Bukkit.getPlayer(uuid);
                        if (bukkitPlayer != null) {
                            EconomyResponse resp = PwnFilterBukkitPlugin.economy.withdrawPlayer(
                                    Bukkit.getOfflinePlayer(uuid), amount);
                            bukkitPlayer.sendMessage(messageString);
                            return resp.transactionSuccess();
                        }
                        return false;
                    });
            if (result != null) return result;
        }
        return false;
    }

    @Override
    public void kick(final UUID uuid, final String messageString) {
        safeBukkitDispatch(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player bukkitPlayer = Bukkit.getPlayer(uuid);
                        if (bukkitPlayer != null)
                            bukkitPlayer.kickPlayer(messageString);
                    }
                });

    }

    @Override
    public void kill(final UUID uuid, final String messageString) {
        safeBukkitDispatch(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player bukkitPlayer = Bukkit.getPlayer(uuid);
                        if (bukkitPlayer != null)
                            bukkitPlayer.getPlayer().setHealth(0);
                        DeathMessages.addKilledPlayer(uuid, bukkitPlayer + " " + messageString);
                    }
                });
    }

    /*
      ***********
      Console API
      ***********
     */

    @Override
    public void sendConsoleMessage(final String message) {
        audiences.audience(Bukkit.getConsoleSender()).sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
    }

    @Override
    public void sendConsoleMessage(TextComponent message) {
        audiences.console().sendMessage(message);
    }

    @Override
    public void sendConsoleMessages(final List<String> messageList) {
        messageList.forEach(message->
              audiences.audience(Bukkit.getConsoleSender())
                        .sendMessage(LegacyComponentSerializer.legacySection().deserialize(message)));
    }

    @Override
    public void sendBroadcast(final String message) {
        audiences.all().sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
    }

    @Override
    public void sendBroadCast(TextComponent component) {
        audiences.all().sendMessage(component);
    }

    @Override
    public void sendBroadcast(final List<String> messageList) {
        messageList.forEach(message->
                audiences.all().sendMessage(LegacyComponentSerializer.legacySection().deserialize(message)));
    }

    @Override
    public void executeCommand(final String command) {
        if (plugin instanceof Plugin) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }.runTask((Plugin) plugin);
        } else throw new IllegalPluginAccessException();
    }

    @Override
    public boolean globalMute() {
        return BukkitConfig.globalMute();
    }

    @Override
    public void setMutStatus(boolean status) {
        BukkitConfig.setGlobalMute(status);
    }

    @Override
    public void notifyWithPerm(final String permissionString, final String sendString) {
        safeBukkitDispatch(() -> {
            if (permissionString.equalsIgnoreCase("console")) {
                Bukkit.getConsoleSender().sendMessage(sendString);
            } else {
                Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(p -> p.hasPermission(permissionString))
                        .forEach(p -> p.sendMessage(sendString));
            }
        });

    }
    @SuppressWarnings("WeakerAccess")
    public static BukkitAudiences bukkitAudiences(){
        return audiences;
    }

    public AudienceProvider audiences() {
        return bukkitAudiences();
    }

    public static class PlayerNotFound extends Exception {
    }

}
