package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.RuleManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import java.util.List;

/**
* Apply the filter to commands.
*/

public class PwnFilterCommandListener extends BaseListener {

    public List<String> cmdlist;
    public List<String> cmdblist;

    public String getShortName() { return "COMMAND" ;}

    public PwnFilterCommandListener(PwnFilter p) {
	    super(p);
        setRuleChain(RuleManager.getInstance().getRuleChain("command.txt"));
    }

    public void activate(Configuration config) {
        if (isActive()) return;

        EventPriority priority = EventPriority.valueOf(config.getString("cmdpriority", "LOWEST").toUpperCase());
        if (config.getBoolean("commandfilter")) {
            PluginManager pm = Bukkit.getPluginManager();
            pm.registerEvent(PlayerCommandPreprocessEvent.class, this, priority,
                    new EventExecutor() {
                public void execute(Listener l, Event e) { eventProcessor((PlayerCommandPreprocessEvent) e); }
            },
            plugin);
            setActive();
            PwnFilter.logger.info("Activated CommandListener with Priority Setting: " + priority.toString()
                    + " Rule Count: " + getRuleChain().ruleCount() );
        }
    }


    public void eventProcessor(PlayerCommandPreprocessEvent event) {

        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        DataCache dCache = DataCache.getInstance();

        if (dCache.hasPermission(player, "pwnfilter.bypass.commands")) return;

        String message = event.getMessage();

        //Gets the actual command as a string
        String cmdmessage = message.substring(1).split(" ")[0];

        cmdlist = plugin.getConfig().getStringList("cmdlist");
        cmdblist = plugin.getConfig().getStringList("cmdblist");

        if (!cmdlist.isEmpty() && !cmdlist.contains(cmdmessage)) return;
        if (cmdblist.contains(cmdmessage)) return;

        // Global mute
        if ((PwnFilter.pwnMute) && (!(dCache.hasPermission(player, "pwnfilter.bypass.mute")))) {
            event.setCancelled(true);
            return;
        }

        // Simple Spam filter TODO: Make # of repeat messages configurable (Will help with booscooldowns)
        if (plugin.getConfig().getBoolean("commandspamfilter") && !player.hasPermission("pwnfilter.bypass.spam")) {
            // Keep a log of the last message sent by this player.  If it's the same as the current message, cancel.
            if (PwnFilter.lastMessage.containsKey(player) && PwnFilter.lastMessage.get(player).equals(message)) {
                event.setCancelled(true);
                return;
            }
            PwnFilter.lastMessage.put(player, message);

        }

        FilterState state = new FilterState(plugin, message, player, this);

        // Global decolor
        if ((PwnFilter.decolor) && !(dCache.hasPermission(player,"pwnfilter.color"))) {
            state.message.decolor();
        }


        // Take the message from the Command Event and send it through the filter.

        ruleChain.apply(state);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setMessage(state.message.getColoredString());
        }

        if (state.cancel) event.setCancelled(true);

    }

}
