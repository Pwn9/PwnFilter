/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.bukkit;

import com.pwn9.PwnFilter.api.MessageAuthor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Author of a text string sent to us by Bukkit.  This is typically a player.
 * These objects are transient, and only last for as long as the message does.
 * <p/>
 * Created by ptoal on 15-08-31.
 */
public class BukkitPlayer implements MessageAuthor {

    private final UUID bukkitPlayerId;
    private final Plugin plugin;

    private BukkitPlayer(UUID uuid, Plugin plugin) {
        this.bukkitPlayerId = uuid;
        this.plugin = plugin;
    } // Do not allow external creation of class


    public static BukkitPlayer getInstance(Player player, Plugin plugin) {
        return new BukkitPlayer(player.getUniqueId(), plugin);
    }

    public static BukkitPlayer getInstance(UUID uuid, Plugin plugin) {
        return new BukkitPlayer(uuid, plugin);
    }
    /**
     * <p>hasPermission.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean hasPermission(String s) {
        try {
            return PwnFilterPlugin.getCache().getData(bukkitPlayerId).getPermissionSet().contains(s);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * <p>hasPermission.</p>
     *
     * @param perm a {@link org.bukkit.permissions.Permission} object.
     * @return a boolean.
     */
    public boolean hasPermission(Permission perm) {
        return hasPermission(perm.getName());
    }

    @NotNull
    public String getName() {
        try {
            return PwnFilterPlugin.getCache().getData(bukkitPlayerId).getName();
        } catch (Exception e) {
            return "";
        }

   }

    @NotNull
    public UUID getID() {
        return bukkitPlayerId;
    }

    public boolean burn(final int duration, final String messageString) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player bukkitPlayer = Bukkit.getPlayer(bukkitPlayerId);
                if (bukkitPlayer != null) {
                    bukkitPlayer.setFireTicks(duration);
                    bukkitPlayer.sendMessage(messageString);
                }
            }
        }.runTask(plugin);

        return true;
    }

    @Override
    public void sendMessage(final String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player bukkitPlayer = Bukkit.getPlayer(bukkitPlayerId);
                if (bukkitPlayer != null) {
                    bukkitPlayer.sendMessage(message);
                }
            }
        }.runTask(plugin);
    }

    public void executeCommand(final String command) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player bukkitPlayer = Bukkit.getPlayer(bukkitPlayerId);
                if (bukkitPlayer != null) {
                    bukkitPlayer.performCommand(command);
                }
            }
        }.runTask(plugin);
    }

    public boolean withdrawMoney(final Double amount, final String messageString) {

        if (PwnFilterPlugin.economy != null) {
            Boolean result = PwnFilterPlugin.getCache().safeBukkitAPICall(
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            Player bukkitPlayer = Bukkit.getPlayer(bukkitPlayerId);
                            if (bukkitPlayer != null) {
                                EconomyResponse resp = PwnFilterPlugin.economy.withdrawPlayer(
                                        Bukkit.getOfflinePlayer(bukkitPlayerId), amount);
                                bukkitPlayer.sendMessage(messageString);
                                return resp.transactionSuccess();
                            }
                            return false;
                        }
                    });
            if (result != null) return result;
        }
        return false;
    }

    public void kick(final String messageString) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player bukkitPlayer = Bukkit.getPlayer(bukkitPlayerId);
                if (bukkitPlayer != null)
                bukkitPlayer.kickPlayer(messageString);
            }
        }.runTask(plugin);
    }

    public void kill(final String messageString) {
        PwnFilterPlugin.addKilledPlayer(bukkitPlayerId, getName() + " " + messageString);
        new BukkitRunnable() {
            @Override
            public void run() {
                Player bukkitPlayer = Bukkit.getPlayer(bukkitPlayerId);
                if (bukkitPlayer != null)
                    bukkitPlayer.getPlayer().setHealth(0);
            }
        }.runTask(plugin);
    }

    public String getWorldName() {
        try {
            return PwnFilterPlugin.getCache().getData(bukkitPlayerId).getWorld().getName();
        } catch (ExecutionException e) {
            return null;
        }
    }


}
