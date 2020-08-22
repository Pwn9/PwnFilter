/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.engine.api;

import com.pwn9.filter.engine.rules.Rule;
import com.pwn9.filter.util.SimpleString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author Sage905
 *
 * A FilterContext is passed to the FilterService with the message, author, and
 * a reference to the calling client.
 *
 * Rules will be applied to the message, and actions may be taken on the author.
 *
 * At the end of processing, the client is responsible for taking actions such
 * as cancelling the original event, forwarding the modified message, etc.
 *
 */
public class FilterContext {
    private final EnhancedString originalMessage; // Original message
    private final MessageAuthor author; // Author that this event is connected to.
    private final FilterClient filterClient;
    private final List<String> logMessages = new ArrayList<>(); // Rules can add strings to this array.  They will be output to log if log=true
    private final List<Rule> matchedRules = new ArrayList<>(); // An array containing all the rules we matched.
    private final ConcurrentHashMap<String, String> notifyMessages = new ConcurrentHashMap<>(8, 0.9f, 1);
    private EnhancedString modifiedMessage; // Modified message string
    private boolean logging = false;  // If true, actions will be logged
    private boolean aborted = false; // If set true by a rule, will stop further processing.
    private boolean cancelled = false; // If set true, will cancel this event.
    private Rule rule; // Rule we currently match
    private Pattern pattern; // Pattern that we currently matched.

    /**
     * FilterContext Constructor
     *
     * @param originalMessage An {@link EnhancedString} object to run rules against.
     * @param author The {@link MessageAuthor} to perform rule actions on.
     * @param filterClient The {@link FilterClient} source of this message.
     */
    public FilterContext(EnhancedString originalMessage, MessageAuthor author,
                         FilterClient filterClient) {
        this.originalMessage = originalMessage;
        modifiedMessage = originalMessage;
        this.author = author;
        this.filterClient = filterClient;
    }

    /**
     * A convenience Constructor that wraps a plain String into an
     * {@link EnhancedString} object.
     * @param originalString A plain {@link String} to apply rules to.
     * @param author The {@link MessageAuthor} to perform rule actions on.
     * @param filterClient The {@link FilterClient} source of this message.
     */
    public FilterContext(String originalString, MessageAuthor author,
                         FilterClient filterClient) {
        this(new SimpleString(originalString), author, filterClient);
    }

    /**
     * Add a string to the list of log messages that will be written to the
     * logfile / console.
     *
     * These messages will only be output if the rule has the "then log" action,
     * or if debug &gt;= low in the comfig.yml
     *
     * @param message Log message to add
     */
    public void addLogMessage(String message) {
        logMessages.add(message);
    }

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

    public MessageAuthor getAuthor() {
        return author;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled() {
        this.cancelled = true;
    }

    public EnhancedString getOriginalMessage() {
        return originalMessage;
    }

    public EnhancedString getModifiedMessage() {
        return modifiedMessage;
    }

    public void setModifiedMessage(EnhancedString newMessage) {
        modifiedMessage = newMessage;
    }

    public boolean loggingOn() {
        return logging;
    }

    public void setLogging() {
        this.logging = true;
    }

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted() {
        this.aborted = true;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
        matchedRules.add(rule);
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<Rule> getMatchedRules() {
        return matchedRules;
    }

    public FilterClient getFilterClient() {
        return filterClient;
    }

    public Map<String, String> getNotifyMessages() {
        return Collections.unmodifiableMap(notifyMessages);
    }

    public void setNotifyMessage(String perm, String message) {
        notifyMessages.put(perm, message);
    }

}
