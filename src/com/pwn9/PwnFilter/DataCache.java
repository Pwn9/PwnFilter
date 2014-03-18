/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Logger;

/**
 * Handles keeping a cache of data that we need during Async event handling.
 * We can't get this data in our Async method, as Bukkit API calls are not threadsafe.
 *
 * Making this a singleton, since it will be needed for lookups across multiple threads
 * and we don't want to have to pass around a reference to it.
 *
 * The DataCache runs in the main bukkit thread every second, pulling the required
 * information.
 *
 * This implementation is probably not the optimal way of doing things, but it
 * is the best I could come up with. - Sage
 */

@SuppressWarnings("UnusedDeclaration")
public class DataCache {

    public final static int runEveryTicks = 20; // Once per second
    public final static int playersPerRun = 50;

    private static DataCache _instance = null;

    // Permissions we are interested in caching
    protected Set<String> permSet = new HashSet<String>();

    //private
    private final Plugin plugin;
    private int taskId;
    private Multimap<Player, String> playerPermissions = HashMultimap.create();
    private List<Player> queuedPlayerList = new ArrayList<Player>();
    private Set<Player> onlinePlayers = new HashSet<Player>();

    private DataCache(Plugin plugin) {
        if (plugin == null) throw new IllegalStateException("Could not get PwnFilter instance!");
        this.plugin = plugin;
    }

    // This method is for other classes to call to query the cache.
    public static DataCache getInstance() throws IllegalStateException {
        if ( _instance == null ) {
            _instance = new DataCache(PwnFilter.getInstance());
        }
        return _instance;
    }

    public Player[] getOnlinePlayers() {
        return onlinePlayers.toArray(new Player[onlinePlayers.size()]);
    }

    public boolean hasPermission(Player p, String s) {
        return playerPermissions.get(p).contains(s);
    }

    public boolean hasPermission(Player p, Permission perm) {
        return playerPermissions.get(p).contains(perm.getName());
    }

    public void start() {
        // Initialize with current online players
        for (Player p : Bukkit.getOnlinePlayers()) {
            addPlayer(p);
        }

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                updateCache();
            }
        },0,DataCache.runEveryTicks);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
        for (Player p : onlinePlayers) {
            removePlayer(p);
        }
        taskId = 0;
    }

    public void finalize() throws Throwable {
        if ( taskId != 0 ) {
            stop();
        }
        super.finalize();
    }

    public void dumpCache(Logger l) {
        l.finest("PwnFilter Data Cache Contents:");
        l.finest("Task Id: " + taskId);
        l.finest("Online Players: " + Bukkit.getOnlinePlayers().length);
        l.finest("Total Names: " + onlinePlayers.size() + " Perms: " + playerPermissions.size());
        StringBuilder sb = new StringBuilder();
        for (Player p : queuedPlayerList ){
            sb.append(p.toString());
            sb.append(" ");
        }
        l.finest(sb.toString());
        l.finest("-----PlayerCache ------");
        for (Player p : onlinePlayers) {
            l.finest("Player ID: " + p.getUniqueId() + " Name: " + p.getName() + " World: " + p.getWorld().getName());
            StringBuilder s = new StringBuilder();
            sb.append("PermissionsSet : ");
            Collection<String> perms = playerPermissions.get(p);
            for (String perm : perms) {
                s.append(perm);
                s.append(" ");
            }
            l.finest(s.toString());
        }
    }


    /* NOTE: All of the following methods are synchronized.  They must only
             ever be called from the Bukkit main thread task.  ALL writes to
             the DataCache MUST happen in a thread-safe way!
    */
    public synchronized void addPlayer(Player p) {
        onlinePlayers.add(p);
    }

    public synchronized void removePlayer(Player p) {
        onlinePlayers.remove(p);
        playerPermissions.get(p).clear();
    }

    private synchronized void updateCache() {
        /*
          Every time this method is called, it will check to see if there are players on the
          queuedPlayerList[].  If so, it will process them.  If not, it will
          grab the list of online players, and add it to the list.
         */
        if (queuedPlayerList.isEmpty()) {
            // A quick "Sanity Check" that our internal list of online players matches
            // The actual list of online players...
            if (!onlinePlayers.containsAll(Arrays.asList(Bukkit.getOnlinePlayers()))) {
                LogManager.logger.warning("Cached Player List is not equal to actual online player list!");
            }

            if (onlinePlayers.size() > 0) {
                queuedPlayerList.addAll(onlinePlayers);
            }
            // Clear out stale data
            for (Iterator<Player> it = onlinePlayers.iterator(); it.hasNext(); ) {
                Player p = it.next();
                if (!p.isOnline()) {
                    LogManager.logger.warning("Removing cached, but offline player: " + p.getName());
                    playerPermissions.get(p).clear();
                    it.remove();
                }
            }
        }
        // Update the cache
        for (int i= 0 ; i < playersPerRun ; i++) {
            if (queuedPlayerList.isEmpty()) break;
            Player player = queuedPlayerList.remove(0);
            cachePlayerPermissions(player);

        }
    }

    public synchronized void addPermission(String permission) {
        permSet.add(permission);
    }

    public synchronized void addPermissions(List<Permission> permissions) {
        for (Permission p : permissions ) {
            permSet.add(p.getName());
        }
    }

    public synchronized void addPermissions(Set<String> permissions) {
        permSet.addAll(permissions);
    }

    // NOTE: This is not synchronized, but it is private, so that only the
    // synchronized methods can call it.

    private void cachePlayerPermissions(Player p) {

        for (String perm : permSet) {
            if (p.hasPermission(perm)) {
                playerPermissions.put(p, perm);
            }
        }

    }



}
