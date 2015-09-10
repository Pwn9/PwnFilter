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
import com.pwn9.PwnFilter.api.MessageAuthor;
import com.pwn9.PwnFilter.bukkit.PwnFilterPlugin;
import com.pwn9.PwnFilter.rules.Rule;
import com.pwn9.PwnFilter.util.EnhancedString;
import com.pwn9.PwnFilter.util.SimpleString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
 * @author ptoal
 * @version $Id: $Id
 */

//TODO: Make all this stuff private and create getters/setters

@SuppressWarnings("UnusedDeclaration")
public class FilterState {
    private final EnhancedString originalMessage; // Original message
    private EnhancedString modifiedMessage; // Modified message string
    private EnhancedString unfilteredMessage; // message string for "raw" messages.
    private final MessageAuthor author; // Player that this event is connected to.
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
     *  @param m The original text string to run rules against.
     * @param a  a {@link MessageAuthor} object.
     * @param l a {@link FilterClient} object.
     */
    public FilterState(EnhancedString m, MessageAuthor a, FilterClient l) {
        originalMessage = m;
        modifiedMessage = m;
        messageLen = originalMessage.length();
        author = a;
        listener = l;
    }

    /**
     * A convenience Constructor that wraps a plain String
     * @param s A String containing the original text to run rules on.
     * @param a The {@link MessageAuthor } of this message
     * @param l The {@link FilterClient } that generated this message
     */
    public FilterState(String s, MessageAuthor a, FilterClient l) {
        this(new SimpleString(s), a, l);
    }

    /**
     * A FilterState object from a UUID, instead of an Author Object.
     *  @param m String message to process
     * @param uuid Unique ID of the Author
     * @param l Listener that is calling.
     */
    public FilterState(EnhancedString m, UUID uuid, FilterClient l) {
        originalMessage = m;
        modifiedMessage = m;
        messageLen = originalMessage.length();
        listener = l;
        //TODO: Abstract this into a generic MessageAuthor lookup?
        author = PwnFilterPlugin.getBukkitAPI().getAuthor(uuid);
    }

    /**
     * Add a string to the list of log messages that will be written to the logfile /console.
     * These messages will only be output if the rule has the "then log" action, or if debug &gt;= low
     * in the comfig.yml
     *
     * @param message A string containing the log message to be output.
     */
    public void addLogMessage (String message) {
        logMessages.add(message);
    }

    /**
     * <p>Getter for the field <code>logMessages</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getLogMessages() {
        return logMessages;
    }
    /**
     * <p>messageChanged.</p>
     *
     * @return true if the modified message is different than the original.
     */
    public boolean messageChanged() {
        return !originalMessage.equals(modifiedMessage);
    }

    /**
     * <p>playerHasPermission.</p>
     *
     * @param perm a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean playerHasPermission(String perm) {
        return author != null && author.hasPermission(perm);
    }

    /**
     * <p>Getter for the field <code>author</code>.</p>
     *
     * @return a {@link org.bukkit.entity.Player} object.
     */
    public MessageAuthor getAuthor() {
        return author;
    }

    /**
     * <p>isCancelled.</p>
     *
     * @return a boolean.
     */
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * <p>setCancelled.</p>
     *
     */
    public void setCancelled() {
        this.cancel = true;
    }

    /**
     * <p>getListenerName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getListenerName() {
        return listener.getShortName();
    }
    /**
     * <p>Getter for the field <code>originalMessage</code>.</p>
     *
     * @return a new Instance of ColouredString with a copy of the originalMessage.
     */
    public EnhancedString getOriginalMessage() {
        return originalMessage;
    }

    /**
     * <p>Getter for the field <code>modifiedMessage</code>.</p>
     *
     * @return a {@link EnhancedString} object.
     */
    public EnhancedString getModifiedMessage() {
        return modifiedMessage;
    }

    /**
     * <p>Setter for the field <code>modifiedMessage</code>.</p>
     *
     * @param newMessage a {@link EnhancedString} object.
     */
    public void setModifiedMessage(EnhancedString newMessage) {
        modifiedMessage = newMessage;
    }

    /**
     * <p>Getter for the field <code>unfilteredMessage</code>.</p>
     *
     * @return a {@link EnhancedString} object.
     */
    public EnhancedString getUnfilteredMessage() {
        return unfilteredMessage;
    }

    /**
     * <p>Setter for the field <code>unfilteredMessage</code>.</p>
     *
     * @param newMessage a {@link EnhancedString} object.
     */
    public void setUnfilteredMessage(EnhancedString newMessage) {
        unfilteredMessage = newMessage;
    }

}
