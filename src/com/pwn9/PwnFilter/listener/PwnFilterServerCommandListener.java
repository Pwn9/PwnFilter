package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

/**
* Apply the filter to commands.
*/

public class PwnFilterServerCommandListener implements Listener {
    private final PwnFilter plugin;

    public PwnFilterServerCommandListener(PwnFilter p) {
	    plugin = p;
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvent(ServerCommandEvent.class, this, PwnFilter.cmdPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onServerCommandEvent((ServerCommandEvent) e); }
                },
                plugin);
        PwnFilter.logger.info("Activated ServerCommandListener with Priority Setting: " + PwnFilter.consolePriority.toString());

    }

    public void onServerCommandEvent(ServerCommandEvent event) {

        String command = event.getCommand();

        //Gets the actual command as a string
        String cmdmessage = command.split(" ")[0];

        if (!plugin.cmdlist.isEmpty() && !plugin.cmdlist.contains(cmdmessage)) return;
        if (plugin.cmdblist.contains(cmdmessage)) return;

        FilterState state = new FilterState(plugin, command, null,
                PwnFilter.EventType.CONSOLE);

        // Take the message from the Command Event and send it through the filter.

        PwnFilter.ruleset.runFilter(state);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setCommand(state.message.getColoredString());
        }

        if (state.cancel) event.setCommand("");

    }
}
