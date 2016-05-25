/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by Sage905 on 2016-05-10.
 */
public class WorkingScheduler implements BukkitScheduler {

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return 0;
    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable bukkitRunnable, long l) {
        return 0;
    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
        return 0;
    }

    @Override
    public int scheduleSyncDelayedTask(Plugin plugin, BukkitRunnable bukkitRunnable) {
        return 0;
    }

    @Override
    public int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return 0;
    }

    @Override
    public int scheduleSyncRepeatingTask(Plugin plugin, BukkitRunnable bukkitRunnable, long l, long l1) {
        return 0;
    }

    @Override
    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
        return 0;
    }

    @Override
    public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task) {
        return 0;
    }

    @Override
    public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
        return 0;
    }

    // This scheduler never runs tasks.
    @Override
    public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
        FutureTask<T> newTask = new FutureTask<>(task);
        newTask.run();
        return newTask;
    }

    @Override
    public void cancelTask(int taskId) {

    }

    @Override
    public void cancelTasks(Plugin plugin) {

    }

    @Override
    public void cancelAllTasks() {

    }

    @Override
    public boolean isCurrentlyRunning(int taskId) {
        return false;
    }

    @Override
    public boolean isQueued(int taskId) {
        return false;
    }

    @Override
    public List<BukkitWorker> getActiveWorkers() {
        return null;
    }

    @Override
    public List<BukkitTask> getPendingTasks() {
        return null;
    }

    @Override
    public BukkitTask runTask(Plugin plugin, Runnable task) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTask(Plugin plugin, BukkitRunnable bukkitRunnable) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskAsynchronously(Plugin plugin, Runnable task) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskAsynchronously(Plugin plugin, BukkitRunnable bukkitRunnable) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskLater(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskLater(Plugin plugin, BukkitRunnable bukkitRunnable, long l) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(Plugin plugin, BukkitRunnable bukkitRunnable, long l) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskTimer(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskTimer(Plugin plugin, BukkitRunnable bukkitRunnable, long l, long l1) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {
        return null;
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, BukkitRunnable bukkitRunnable, long l, long l1) throws IllegalArgumentException {
        return null;
    }
}
