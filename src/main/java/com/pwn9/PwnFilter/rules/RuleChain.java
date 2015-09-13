/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.pwn9.PwnFilter.FilterTask;
import com.pwn9.PwnFilter.api.Action;
import com.pwn9.PwnFilter.rules.parser.FileParser;
import com.pwn9.PwnFilter.util.LogManager;

import java.util.*;


/**
 * The RuleSet contains a compiled version of all the rules in the text file.
 * A RuleSet has a chain of Rules.  The ruleset apply method is unique to each
 * type of event, but the basic mechanism is:
 * <p>
 * Load a chain of rules from a text file, parse them into a chain
 * The Event Handler calls the RuleSet.appply() method with the signature of the event
 * The apply() method iterates over the rules, one at a time, matching, checking conditions,
 * and executing actions based on the message and the rules.
 *
 * User: ptoal
 * Date: 13-04-05
 * Time: 12:38 PM
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class RuleChain implements Chain,ChainEntry {
    enum ChainState {
        INIT,  // Chain was reset and needs to be reloaded before use.
        PARTIAL, // Chain is in the process of loading
        READY // Chain is fully loaded and ready to use.
    }

    private ChainState chainState;
    private final List<ChainEntry> chain = new ArrayList<ChainEntry>();
    private final Multimap<String, Action> actionGroups = ArrayListMultimap.create();
    private final Multimap<String, Condition> conditionGroups = ArrayListMultimap.create();
    private final Set<RuleChainListener> listeners = new HashSet<RuleChainListener>();

    private final String configName;


    /**
     * <p>Constructor for RuleChain.</p>
     *
     * @param configName a {@link java.lang.String} object.
     */
    public RuleChain(String configName) {
        this.configName = configName;
        chainState = ChainState.INIT;
    }

    /**
     * (Re)load this rulechain's config from its file.
     *
     * @return Success or failure
     */
    public boolean loadConfigFile() {

        resetChain();

        // While loading, we are in a PARTIAL state.
        chainState = ChainState.PARTIAL;

        FileParser parser = new FileParser(configName);

        if (parser.parseRules(this)) {
            chainState = ChainState.READY;
            notifyUpdate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>Getter for the field <code>configName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getConfigName() { return configName;}

    /**
     * <p>ruleCount.</p>
     *
     * @return a int.
     */
    public int ruleCount() {
        Integer count = 0;
        for (ChainEntry c : chain) {
            if (c instanceof RuleChain) {
                count += ((RuleChain) c).ruleCount();
            } else count++;
        }
        return count;
    }

    /**
     * {@inheritDoc}
     *
     * Iterate over the chain in order, checking the Rule pattern against the
     * current message.  If the text pattern matches, test the rule conditions, to
     * ensure they are all met.  If all of the conditions are met, execute the Rule's
     * actions in sequential order.  If the Rule sets the stop=true of the FilterTask,
     * stop processing rules.  If not, continue along the rule chain, checking the
     * (possibly modified) message against subsequent rules.
     */
    public void apply(FilterTask filterTask) throws IllegalStateException {

        if (chain == null) {
            throw new IllegalStateException("Chain is empty: " + configName);
        }

        for (ChainEntry entry : chain) {
            if (filterTask.isAborted()) break;
            entry.apply(filterTask);
        }
    }

    /**
     * <p>execute.</p>
     *
     * @param state a {@link FilterTask} object.
     */
    public void execute(FilterTask state ) {

        LogManager logManager = LogManager.getInstance();

        apply(state);

        if (state.getPattern() != null) {
            logManager.debugHigh("Debug last match: " + state.getPattern().pattern());
            logManager.debugHigh("Debug original: " + state.getOriginalMessage().getRaw());
            logManager.debugHigh("Debug current: " + state.getModifiedMessage().getRaw());
            logManager.debugHigh("Debug log: " + (state.loggingOn() ? "yes" : "no"));
            logManager.debugHigh("Debug deny: " + (state.isCancelled() ? "yes" : "no"));
        } else {
            logManager.debugHigh("[PwnFilter] Debug no match: " + state.getOriginalMessage().getRaw());
        }

        if (state.isCancelled()){
            state.addLogMessage("<"+state.getAuthor().getName() + "> Original message cancelled.");
        } else if (state.getPattern() != null) {
            state.addLogMessage("|" + state.getListenerName() + "| SENT <" +
                    state.getAuthor().getName() + "> " + state.getModifiedMessage().toString());
        }

        for (String s : state.getLogMessages()) {
            if (state.loggingOn()) {
                LogManager.logger.info(s);
            } else {
                LogManager.logger.log(LogManager.getRuleLogLevel(),s);
            }
        }
    }

    /** {@inheritDoc} */
    public void append(ChainEntry r) {
        if (r.isValid()) {
            chain.add(r); // Add the Rule to this chain
        }
    }

    /**
     * <p>Getter for the field <code>chain</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<ChainEntry> getChain() {
        return chain;
    }

    /**
     * <p>isEmpty.</p>
     *
     * @return a boolean.
     */
    public boolean isEmpty() {
        return chain.isEmpty();
    }

    /**
     * <p>isValid.</p>
     *
     * @return a boolean.
     */
    public boolean isValid() {
        return chainState == ChainState.READY;
    }

    /**
     * {@inheritDoc}
     *
     * Get the Set of all permissions used in this chain.
     */
    @Override
    public Set<String> getPermissionList() {
        TreeSet<String> permList = new TreeSet<String>();

        for (ChainEntry r : chain) {
            permList.addAll(r.getPermissionList());
        }
        return permList;
    }

    /**
     * <p>Getter for the field <code>actionGroups</code>.</p>
     *
     * @return a {@link com.google.common.collect.Multimap} object.
     */
    public Multimap<String, Action> getActionGroups() {
        return actionGroups;
    }

    /**
     * <p>Getter for the field <code>conditionGroups</code>.</p>
     *
     * @return a {@link com.google.common.collect.Multimap} object.
     */
    public Multimap<String, Condition> getConditionGroups() {
        return conditionGroups;
    }
    /**
     * Delete all rules in the chain, and reset its state to INIT
     */
    public void resetChain() {
        chain.clear();
        conditionGroups.clear();
        actionGroups.clear();
        chainState = ChainState.INIT;
    }

    /** {@inheritDoc} */
    public void addConditionGroup(String name, List<Condition> cGroup) {
        if (name != null && cGroup != null)
            if(conditionGroups.get(name).isEmpty()) {
                conditionGroups.get(name).addAll(cGroup);
            } else {
                LogManager.getInstance().debugLow("Condition Group named '"+name+"' already exists in chain: " + getConfigName());
            }
    }

    /** {@inheritDoc} */
    public void addActionGroup(String name, List<Action> aGroup) {
        if (name != null && aGroup != null)
            if(!actionGroups.containsKey(name)) {
                actionGroups.get(name).addAll(aGroup);
            } else {
                LogManager.getInstance().debugLow("Action Group named '"+name+"' already exists in chain: " + getConfigName());
            }
    }

    public void addListener(RuleChainListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RuleChainListener listener) {
        listeners.remove(listener);
    }

    private void notifyUpdate() {
        //TODO: Update to notify state change (INIT/READY, etc)
        for (RuleChainListener l : listeners) {
            l.ruleChainUpdated(this);
        }
    }
}
