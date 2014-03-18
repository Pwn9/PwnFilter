/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.api.FilterClient;
import com.pwn9.PwnFilter.rules.Rule;
import com.pwn9.PwnFilter.util.ColoredString;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Filterstate
 *
 * An object to keep track of the state of a filter event through execution
 * of the rules.
 * <p>
 *
 * In addition to the public methods, it is possible to modify state by writing directly
 * to the following attributes:
 *
 * log: If true, actions will be logged to the file/console
 * stop: If true, no further rules will be processed
 * cancel: If true, this event will be set Cancelled (if possible)
 *
 */

//TODO: Make all this stuff private and create getters/setters

@SuppressWarnings("UnusedDeclaration")
public class FilterState {
    private final ColoredString originalMessage; // Original message
    private ColoredString modifiedMessage; // Modified message string
    private ColoredString unfilteredMessage; // message string for "raw" messages.
    public final Plugin plugin; // Which plugin is this state attached to?
    private final Player player; // Player that this event is connected to.
    public final String playerName,playerWorldName;
    public final FilterClient listener;
    final int messageLen; // New message can't be longer than original.
    private List<String> logMessages = new ArrayList<String>(); // Rules can add strings to this array.  They will be output to log if log=true
    public boolean log = false;  // If true, actions will be logged
    public boolean stop = false; // If set true by a rule, will stop further processing.
    public boolean cancel = false; // If set true, will cancel this event.
    public Rule rule; // Rule we currently match
    public Pattern pattern; // Pattern that we currently matched.

    // NOTE: pattern should always match originalMessage, but may not match
    // the new message, if another rule has modified it.

    /**
     * Class Constructor with the text string to act upon.  Colour codes must
     * already be converted to the section character (u00A7), otherwise they
     * will not be correctly processed.
     *
     * @param m The original text string to run rules against.
     */
    public FilterState(Plugin pl, String m, Player p, FilterClient l) {
        originalMessage = new ColoredString(m);
        modifiedMessage = new ColoredString(m);
        messageLen = originalMessage.length();
        player = p;
        if (p != null) {
            playerName = p.getName();
        } else {
            playerName = "*CONSOLE*";
        }
        if (p != null) {
            playerWorldName = p.getWorld().getName();
        } else {
            playerWorldName = "";
        }
        plugin = pl;
        listener = l;
    }

    /**
     * A FilterState object from a Player Name, instead of Player Object.  This
     * can be used for offline players.
     * @param pl PwnFilter plugin instance
     * @param m String message to process
     * @param pName Player name String
     * @param w World object (optional. Can be null)
     * @param l Listener that is calling.
     */
    public FilterState(Plugin pl, String m, String pName, World w, FilterClient l) {
        originalMessage = new ColoredString(m);
        modifiedMessage = new ColoredString(m);
        messageLen = originalMessage.length();
        playerName = pName;
        playerWorldName = (w == null)?"":w.getName();
        plugin = pl;
        listener = l;
        player = Bukkit.getPlayerExact(pName);
    }

    /**
     * Add a string to the list of log messages that will be written to the logfile /console.
     * These messages will only be output if the rule has the "then log" action, or if debug >= low
     * in the comfig.yml
     * @param message A string containing the log message to be output.
     */
    public void addLogMessage (String message) {
        logMessages.add(message);
    }

    public List<String> getLogMessages() {
        return logMessages;
    }
    /**
     * @return true if the modified message is different than the original.
     */
    public boolean messageChanged() {
        return !originalMessage.toString().equals(modifiedMessage.toString());
    }

    public boolean playerHasPermission(String perm) {
        return player != null && DataCache.getInstance().hasPermission(player, perm);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public String getListenerName() {
        return listener.getShortName();
    }
    /**
     *
     * @return a new Instance of ColouredString with a copy of the originalMessage.
     */
    public ColoredString getOriginalMessage() {
        return new ColoredString(originalMessage);
    }

    public ColoredString getModifiedMessage() {
        return new ColoredString(modifiedMessage);
    }

    public void setModifiedMessage(ColoredString newMessage) {
        modifiedMessage = newMessage;
    }

    public ColoredString getUnfilteredMessage() {
        return unfilteredMessage;
    }

    public void setUnfilteredMessage(ColoredString newMessage) {
        unfilteredMessage = newMessage;
    }

}
