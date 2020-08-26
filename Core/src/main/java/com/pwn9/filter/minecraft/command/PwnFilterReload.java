package com.pwn9.filter.minecraft.command;

import com.pwn9.filter.PwnFilterPlugin;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.FilterServiceImpl;
import com.pwn9.filter.engine.api.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * @author Narimm
 * on 19/08/2020.
 */
public class PwnFilterReload implements PwnFilterCommandExecutor {
    private final FilterService filterServiceImpl;
    private final PwnFilterPlugin plugin;

    public PwnFilterReload(FilterService filterServiceImpl, PwnFilterPlugin plugin) {
        this.filterServiceImpl = filterServiceImpl;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String command, String alias,String... args) {

        filterServiceImpl.getLogger().info("Disabling all listeners");
        filterServiceImpl.disableClients();

        if (!plugin.configurePlugin()) return false;
        filterServiceImpl.getLogger().config("Reloaded config.yml as requested by " + sender.getName());
        filterServiceImpl.getLogger().config("All rules reloaded by " + sender.getName());

        // Re-register our listeners
        filterServiceImpl.enableClients();
        filterServiceImpl.getLogger().info("All listeners re-enabled");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command,String alias, String... args) {
        return Collections.emptyList();
    }
}
