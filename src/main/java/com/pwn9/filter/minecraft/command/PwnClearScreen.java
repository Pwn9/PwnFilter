package com.pwn9.filter.minecraft.command;

import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.engine.api.CommandSender;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;
import java.util.List;

/**
 * Created for the Charlton IT Project.
 * Created by Narimm on 19/08/2020.
 */
public class PwnClearScreen implements PwnFilterCommandExecutor {

    private final MinecraftAPI api;
    public PwnClearScreen(PwnFilterPlugin plugin) {
        this.api = plugin.getApi();
    }
    @Override
    public boolean onCommand(CommandSender sender, String command,String alias, String... args) {
        sender.sendMessage(TextComponent.of("Clearing chat screen").color(NamedTextColor.DARK_RED));
        int i = 0;
        TextComponent.Builder messageBuilder = TextComponent.builder();
        while (i <= 120) {
            messageBuilder.append(TextComponent.of(" - ").append(TextComponent.newline()));
            i++;
        }
        api.sendBroadCast(messageBuilder.build());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command,String alias, String... args) {
        return Collections.emptyList();
    }
}
