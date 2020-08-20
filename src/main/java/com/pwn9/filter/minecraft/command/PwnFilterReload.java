package com.pwn9.filter.minecraft.command;

import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * @author Narimm
 * on 19/08/2020.
 */
public class PwnFilterReload implements PwnFilterCommandExecutor {
    private final FilterService filterService;
    private final PwnFilterPlugin plugin;

    public PwnFilterReload(FilterService filterService, PwnFilterPlugin plugin) {
        this.filterService = filterService;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String command, String alias,String... args) {

        filterService.getLogger().info("Disabling all listeners");
        filterService.disableClients();

        if (!plugin.configurePlugin()) return false;
        filterService.getLogger().config("Reloaded config.yml as requested by " + sender.getName());
        filterService.getLogger().config("All rules reloaded by " + sender.getName());

        // Re-register our listeners
        filterService.enableClients();
        filterService.getLogger().info("All listeners re-enabled");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command,String alias, String... args) {
        return Collections.emptyList();
    }
}
