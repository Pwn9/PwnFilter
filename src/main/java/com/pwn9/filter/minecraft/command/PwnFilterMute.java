package com.pwn9.filter.minecraft.command;

import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.engine.api.CommandSender;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author  Narimm
 * on 19/08/2020.
 */
public class PwnFilterMute implements PwnFilterCommandExecutor {

    private final MinecraftAPI api;
    private final Logger logger;

    public PwnFilterMute(PwnFilterPlugin plugin) {
        this.api = plugin.getApi();
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean onCommand(CommandSender sender, String command,String alias, String... args) {
        if (api.globalMute()) {
            api.sendBroadCast(TextComponent.of("Global mute cancelled by " + sender.getName()).color(NamedTextColor.RED));
            logger.info("global mute cancelled by " + sender.getName());
            api.setMutStatus(false);
        } else {
            api.sendBroadCast(TextComponent.of("Global mute initiated by " + sender.getName()).color(NamedTextColor.GREEN));
            logger.info("global mute initiated by " + sender.getName());
            api.setMutStatus(true);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command,String alias, String... args) {
        return Collections.emptyList();
    }
}
