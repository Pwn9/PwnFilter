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

import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    protected Set<String> permSet = new TreeSet<String>();

    //private
    private final Plugin plugin;
    private int taskId;
    private ConcurrentHashMap<Player,String> playerName;
    private ConcurrentHashMap<String, Player> playerForName;
    private ConcurrentHashMap<UUID, Player> playerForUUID;
    private ConcurrentHashMap<Player,String> playerWorld;
    private ConcurrentHashMap<Player,HashSet<String>> playerPermissions;
    private ArrayList<Player> queuedPlayerList = new ArrayList<Player>();
    private Set<Player> onlinePlayers = new HashSet<Player>();

    //TODO: Add a "registration" system for interesting permissions, etc.
    // so that plugins can add/remove things they want cached.
    private DataCache(Plugin plugin) {
        if (plugin == null) throw new IllegalStateException("Could not get PwnFilter instance!");
        playerName = new ConcurrentHashMap<Player,String>();
        playerForName = new ConcurrentHashMap<String, Player>();
        playerForUUID = new ConcurrentHashMap<UUID, Player>();
        playerWorld = new ConcurrentHashMap<Player,String>();
        playerPermissions = new ConcurrentHashMap<Player,HashSet<String>>();
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
        HashSet<String> perms = playerPermissions.get(p);
        return perms != null && perms.contains(s);
    }

    public boolean hasPermission(Player p, Permission perm) {
        HashSet<String> perms = playerPermissions.get(p);
        return perms != null && perms.contains(perm.getName());
    }

    public String getPlayerWorld(Player p) {
        if (p.isOnline())
            return playerWorld.get(p);
        else
            return null;
    }

    public String getPlayerName(Player p) {
        return playerName.get(p);
    }

    public Player getPlayerForName(String name) {
        return playerForName.get(name);
    }

    public Player getPlayerForUUID(UUID id) {
        return playerForUUID.get(id);
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
        for (Player p : playerName.keySet()) {
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
        l.finest("Total Names: " + playerName.size() + " Worlds: " + playerWorld.size() + " Perms: " + playerPermissions.size());
        StringBuilder sb = new StringBuilder();
        for (Player p : queuedPlayerList ){
            sb.append(p.toString());
            sb.append(" ");
        }
        l.finest(sb.toString());
        l.finest("-----PlayerCache ------");
        for (Player p : playerName.keySet()) {
            l.finest("Player ID: " + p.getUniqueId() + " Name: " + playerName.get(p) + " World: " + playerWorld.get(p));
            StringBuilder s = new StringBuilder();
            sb.append("PermissionsSet : ");
            HashSet<String> perms = playerPermissions.get(p);
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
        playerName.put(p, p.getName());
        playerForName.put(p.getName(), p);
        playerForUUID.put(p.getUniqueId(),p);
        playerWorld.put(p, p.getWorld().getName());
    }

    public synchronized void updatePlayerWorld(Player p) {
        playerWorld.put(p,p.getWorld().getName());
    }

    public synchronized void removePlayer(Player p) {
        onlinePlayers.remove(p);
        playerForName.remove(p.getName());
        playerName.remove(p);
        playerWorld.remove(p);
        playerForUUID.remove(p.getUniqueId());
        playerPermissions.remove(p);
    }

    private synchronized void updateCache() {
        /*
          Every time this method is called, it will check to see if there are players on the
          queuedPlayerList[].  If so, it will process them.  If not, it will
          grab the list of online players, and add it to the list.
         */
        if (queuedPlayerList.size() < 1) {

            // A quick "Sanity Check" that our internal list of online players matches
            // The actual list of online players...
            if (!onlinePlayers.containsAll(Arrays.asList(Bukkit.getOnlinePlayers()))) {
                LogManager.logger.warning("Cached Player List is not equal to actual online player list!");
            }

            if (onlinePlayers.size() > 0) {
                queuedPlayerList.addAll(onlinePlayers);
            }
            // Clear out stale data
            for (Player p : playerName.keySet() ) {
                if (!onlinePlayers.contains(p)) {
                    LogManager.logger.warning("Removing cached, but offline player: " + p.getName());
                    removePlayer(p);
                }
            }
        }
        // Update the cache
        for (int i= 0 ; i < playersPerRun ; i++) {
            if (queuedPlayerList.size() < 1) break;
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
        HashSet<String> playerPerms = new HashSet<String>();

        for (String perm : permSet) {
            if (p.hasPermission(perm)) {
                playerPerms.add(perm);
            }
        }

        playerPermissions.put(p, playerPerms);
    }



}
