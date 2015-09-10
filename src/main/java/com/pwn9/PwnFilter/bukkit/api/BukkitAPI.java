/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.bukkit.api;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.bukkit.PwnFilterPlugin;
import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Handles keeping a cache of data that we need during Async event handling.
 * We can't get this data in our Async method, as Bukkit API calls are not threadsafe.
 * Also, we can't always schedule a task, because we might be running in the
 * main thread.
 * <p/>
 * This will cache data about players for 10s.
 */

@SuppressWarnings("UnusedDeclaration")
public class BukkitAPI {

    private LoadingCache<UUID, PlayerData> playerDataMap = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<UUID, PlayerData>() {
                        @Override
                        public PlayerData load(@NotNull final UUID uuid) {
                            Callable<PlayerData> cacheTask =
                                    new Callable<PlayerData>() {
                                        @Override
                                        public PlayerData call() {
                                            return dataFetch(uuid);
                                        }
                                    };
                            return safeBukkitAPICall(cacheTask);
                        }
                    }
            );

    private final Plugin plugin;

    // Permissions we are interested in caching
    protected final Set<String> permSet = new HashSet<String>();

    private BukkitAPI(PwnFilterPlugin p) {
        plugin = p;

    }

    public static BukkitAPI getInstance(PwnFilterPlugin plugin) {
        return new BukkitAPI(plugin);
    }

    public synchronized void reset() {
        permSet.clear();
        playerDataMap.invalidateAll();
    }


//    /**
//     * <p>Getter for the field <code>onlinePlayers</code>.</p>
//     *
//     * @return an array of {@link org.bukkit.entity.Player} objects.
//     */
//    public Player[] getOnlinePlayers() {
//        return onlinePlayers.toArray(new Player[onlinePlayers.size()]);
//    }


    /**
     * <p>addPermission.</p>
     *
     * @param permission a {@link java.lang.String} object.
     */
    public synchronized void addPermission(String permission) {
        permSet.add(permission);
    }


    /**
     * <p>addPermissions.</p>
     *
     * @param permissions a {@link java.util.List} object.
     */
    public synchronized void addPermissions(List<Permission> permissions) {
        for (Permission p : permissions) {
            permSet.add(p.getName());
        }
    }

    /**
     * <p>addPermissions.</p>
     *
     * @param permissions a {@link java.util.Set} object.
     */
    public synchronized void addPermissions(Set<String> permissions) {
        permSet.addAll(permissions);
    }

    // NOTE: This is not synchronized, but it is private, so that only the
    // synchronized methods can call it.

    private Set<String> cachePlayerPermissions(Player p) {

        Set<String> result = new HashSet<String>();

        for (String perm : permSet) {
            if (p.hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    @Nullable
    public <T> T safeBukkitAPICall(Callable<T> callable) {

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
            Future<T> task =
                    Bukkit.getScheduler().callSyncMethod(plugin, callable);
            try {
                // This will block the current thread for up to 3s
                return task.get(3, TimeUnit.SECONDS);
            } catch (Exception e) {
                LogManager.getInstance().debugLow("Bukkit API call timed out (>3s).");
                return null;
            }

        }
        return null;
    }

    public void safeBukkitDispatch(Runnable runnable) {
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
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }


    private PlayerData dataFetch(UUID uuid) {
        PlayerData cache = new PlayerData();
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return null; // Couldn't find the player!
        cache.setPermissionSet(cachePlayerPermissions(p));
        cache.setName(p.getDisplayName());
        cache.setWorld(p.getWorld());
        return cache;
    }

    public MessageAuthor getAuthor(UUID uuid) {
        return BukkitPlayer.getInstance(uuid,plugin);
    }

    public PlayerData getData(UUID uuid) throws ExecutionException {
        return playerDataMap.get(uuid);
    }
}
