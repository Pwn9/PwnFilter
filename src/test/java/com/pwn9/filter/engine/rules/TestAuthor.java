/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.action.targeted.BurnTarget;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Created by Sage905 on 2016-03-27.
 */
public class TestAuthor implements MessageAuthor, BurnTarget {

    private boolean burnt;
    private final UUID id;

    public TestAuthor() {
        id = UUID.randomUUID();
    }

    public TestAuthor(UUID id) {
        this.id = id;
    }

    @Override
    public boolean hasPermission(String permString) {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return "Sage905";
    }

    @Override
    public void sendMessage(String message) {
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void sendMessages(List<String> messages) {
    }

    @Override
    public boolean burn(int duration, String message) {
        return burnt = true;
    }

    boolean burnt() {
        return burnt;}
}
