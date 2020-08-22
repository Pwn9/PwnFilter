package com.pwn9.filter.bukkit.commands;

import com.pwn9.filter.bukkit.PwnFilterBukkitPlugin;
import com.pwn9.filter.minecraft.command.PwnFilterCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/08/2020.
 */
public class PwnFilterCommands implements CommandExecutor, TabCompleter {

    private final PwnFilterBukkitPlugin bukkitPlugin;
    private final Map<String, PwnFilterCommandExecutor> commands= new HashMap<>();

    public PwnFilterCommands(PwnFilterBukkitPlugin bukkitPlugin) {
        this.bukkitPlugin = bukkitPlugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
        com.pwn9.filter.engine.api.CommandSender sender = bukkitPlugin.getApi().getSenderById(((Player) commandSender).getUniqueId());
        return commands.get(command.getName()).onCommand(sender,command.getName(),label,strings);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] strings) {
        com.pwn9.filter.engine.api.CommandSender sender = bukkitPlugin.getApi().getSenderById(((Player) commandSender).getUniqueId());
        return commands.get(command.getName()).onTabComplete(sender,command.getName(),label,strings);
    }

    public void registerCommands(String command,PwnFilterCommandExecutor executor){
        bukkitPlugin.getCommand(command).setExecutor(this);
        commands.put(command,executor);
    }
}
