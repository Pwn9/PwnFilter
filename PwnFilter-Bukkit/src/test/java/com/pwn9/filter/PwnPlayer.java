package com.pwn9.filter;

import com.pwn9.filter.engine.api.CommandSender;
import com.pwn9.filter.engine.api.Player;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 20/08/2020.
 */
public class PwnPlayer implements Player, CommandSender {

    private final UUID randomID = UUID.randomUUID();
    protected final Queue<String> messages = new LinkedList<>();

    @Override
    public String getPlace() {
        return null;
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public @NotNull String getName() {
        return "Pwn9";
    }

    @Override
    public UUID getId() {
        return randomID;
    }

    @Override
    public void sendMessage(String s) {
        messages.add(s);
    }

    @Override
    public void sendMessages(List<String> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public void sendMessage(TextComponent message) {
        messages.add(LegacyComponentSerializer.legacyAmpersand().serialize(message));
    }


    public String getMessage(){
        return messages.poll();
    }

    public void clearMessages(){
        messages.clear();
    }

    public Queue<String> getAllMessages(){
        return messages;
    }
}
