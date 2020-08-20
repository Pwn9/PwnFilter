package com.pwn9.filter.engine.api;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Created for the Charlton IT Project.
 * Created by Narimm on 19/08/2020.
 */
public interface CommandSender {

    boolean hasPermission(String s);

    @NotNull
    String getName();

    UUID getId();

    void sendMessage(final String message);

    void sendMessages(final List<String> messages);

    void sendMessage(final TextComponent message);
}
