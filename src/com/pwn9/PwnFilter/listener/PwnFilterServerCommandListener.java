package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.RuleManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import java.util.List;

/**
* Apply the filter to commands.
*/

public class PwnFilterServerCommandListener extends BaseListener {

    public List<String> cmdlist;
    public List<String> cmdblist;

    public PwnFilterServerCommandListener(PwnFilter p) {
	    super(p);
        setRuleChain(RuleManager.getInstance().getRuleChain("console.txt"));
    }

    @Override
    public String getShortName() {
        return "CONSOLE";
    }

    public void onServerCommandEvent(ServerCommandEvent event) {

        String command = event.getCommand();

        //Gets the actual command as a string
        String cmdmessage = command.split(" ")[0];

        cmdlist = plugin.getConfig().getStringList("cmdlist");
        cmdblist = plugin.getConfig().getStringList("cmdblist");

        if (!cmdlist.isEmpty() && !cmdlist.contains(cmdmessage)) return;
        if (cmdblist.contains(cmdmessage)) return;

        FilterState state = new FilterState(plugin, command, null, this);

        // Take the message from the Command Event and send it through the filter.

        ruleChain.apply(state);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setCommand(state.message.getColoredString());
        }

        if (state.cancel) event.setCommand("");

    }

    /**
     * Activate this listener.  This method can be called either by the owning plugin
     * or by PwnFilter.  PwnFilter will call the shutdown / activate methods when PwnFilter
     * is enabled / disabled and whenever it is reloading its config / rules.
     * <p/>
     * These methods could either register / deregister the listener with Bukkit, or
     * they could just enable / disable the use of the filter.
     *
     * @param config PwnFilter Configuration object, which the plugin can read for configuration
     *               information. (eg: config.getString("ruledir")
     */
    @Override
    public void activate(Configuration config) {
        if (isActive()) return;

        if (config.getBoolean("commandfilter")) {

            PluginManager pm = Bukkit.getPluginManager();
            EventPriority priority = EventPriority.valueOf(config.getString("cmdpriority", "LOWEST").toUpperCase());

            pm.registerEvent(ServerCommandEvent.class, this, priority,
                    new EventExecutor() {
                        public void execute(Listener l, Event e) { onServerCommandEvent((ServerCommandEvent) e); }
                    },
                    plugin);
            PwnFilter.logger.info("Activated ServerCommandListener with Priority Setting: " + priority.toString()
                    + " Rule Count: " + getRuleChain().ruleCount() );

            setActive();
        }
    }

}
