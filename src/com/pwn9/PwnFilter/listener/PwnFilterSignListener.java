package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;


/**
 * Listen for Sign Change events and apply the filter to the text.
 */

public class PwnFilterSignListener implements Listener {
    private final PwnFilter plugin;
    public PwnFilterSignListener(PwnFilter p) {
        plugin = p;
        PluginManager pm = Bukkit.getPluginManager();

        // Now register the listener with the appropriate priority
        pm.registerEvent(SignChangeEvent.class, this, PwnFilter.signPriority,
                new EventExecutor() {
                    public void execute(Listener l, Event e) { onSignChange((SignChangeEvent)e); }
                },
                plugin);

        PwnFilter.logger.info("Activated SignListener with Priority Setting: " + PwnFilter.signPriority.toString());

    }
    /**
     * The sign filter has extra work to do that the chat doesn't:
     * 1. Take lines of sign and aggregate them into one string for processing
     * 2. Feed them into the filter.
     * 3. Re-split the lines so they can be placed on the sign.
     * @param event The SignChangeEvent to be processed.
     */
    public void onSignChange(SignChangeEvent event) {
        if (event.isCancelled()) return;

        // Permissions Check, if player has bypass permissions, then skip everything.
        if (event.getPlayer().hasPermission("pwnfilter.bypass.signs")) {
            return;
        }
        // Take the message from the CommandPreprocessEvent and send it through the filter.
        StringBuilder builder = new StringBuilder();

        for (String l :event.getLines()) {
            builder.append(l).append(" ");
        }
        String signLines = builder.toString();

        FilterState state = new FilterState(plugin, signLines, event.getPlayer(), PwnFilter.EventType.SIGN);

        PwnFilter.ruleset.runFilter(state);

        if (state.messageChanged()){
            // TODO: Can colors be placed on signs?  Wasn't working. Find out why.
            // Break the changed string into words
            String[] words = state.message.getPlainString().split("\\b");
            String[] lines = new String[4];

            // Iterate over the 4 sign lines, applying one word at a time, until the line is full.
            // If all 4 lines are full, the rest of the words are just discarded.
            // This may negatively affect plugins that use signs and require text to appear on a certain
            // line, but we only do this when we've matched a rule.
            int wordIndex = 0;
            for (int i = 0 ; i < 4 ; i++) {
                lines[i] = "";
                while (wordIndex < words.length) {
                    if (lines[i].length() + words[wordIndex].length() < 15) {
                        lines[i] = lines[i] + words[wordIndex] + " ";
                        wordIndex++;
                    } else {
                        break;
                    }
                }
            }

            for (int i = 0 ; i < 4 ; i++ ) {
                if (lines[i] != null) {
                    event.setLine(i,lines[i]);
                }
            }
        }

        if (state.cancel) {
            event.setCancelled(true);
            state.player.sendMessage("Your sign broke, there must be something wrong with it.");
            state.addLogMessage("SIGN " + state.playerName + " sign text: "
                    + state.getOriginalMessage().getColoredString());
        }

    }
}
