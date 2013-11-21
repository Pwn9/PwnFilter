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

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.rules.parser.FileParser;
import com.pwn9.PwnFilter.util.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;


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
 *
 * TODO: More documentation
 *
 * User: ptoal
 * Date: 13-04-05
 * Time: 12:38 PM
 */

public class RuleChain implements Chain,ChainEntry {
    enum ChainState {
        INIT,  // Chain was reset and needs to be reloaded before use.
        PARTIAL, // Chain is in the process of loading
        READY // Chain is fully loaded and ready to use.
    }

    private ChainState chainState;
    private ArrayList<ChainEntry> chain = new ArrayList<ChainEntry>();
    private HashMap<String, ArrayList<Action>> actionGroups = new HashMap<String, ArrayList<Action>>();
    private HashMap<String, ArrayList<Condition>> conditionGroups = new HashMap<String, ArrayList<Condition>>();

    private final String configName;


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
            DataCache.getInstance().addPermissions(getPermissionList());
            return true;
        } else {
            return false;
        }
    }

    public String getConfigName() { return configName;}

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
     * Iterate over the chain in order, checking the Rule pattern against the
     * current message.  If the text pattern matches, test the rule conditions, to
     * ensure they are all met.  If all of the conditions are met, execute the Rule's
     * actions in sequential order.  If the Rule sets the stop=true of the FilterState,
     * stop processing rules.  If not, continue along the rule chain, checking the
     * (possibly modified) message against subsequent rules.
     *
     * @param state A FilterState object which is used to get information about
     *              this event, and update its status (eg: set cancelled)
     *
     */

    public void apply(FilterState state) throws IllegalStateException {

        if (chain == null) {
            throw new IllegalStateException("Chain is empty: " + configName);
        }

        for (ChainEntry entry : chain) {
            entry.apply(state);
            if (state.stop) {
                break;
            }
        }
    }

    public void execute(FilterState state ) {

        LogManager logManager = LogManager.getInstance();

        apply(state);

        if (state.pattern != null) {
            logManager.debugHigh("Debug last match: " + state.pattern.pattern());
            logManager.debugHigh("Debug original: " + state.getOriginalMessage().getColoredString());
            logManager.debugHigh("Debug current: " + state.message.getColoredString());
            logManager.debugHigh("Debug log: " + (state.log ? "yes" : "no"));
            logManager.debugHigh("Debug deny: " + (state.cancel ? "yes" : "no"));
        } else {
            logManager.debugHigh("[PwnFilter] Debug no match: " + state.getOriginalMessage().getColoredString());
        }

        if (state.cancel){
            state.addLogMessage("<"+state.playerName + "> Original message cancelled.");
        } else if (state.pattern != null) {
            state.addLogMessage("|" + state.listener.getShortName() + "| SENT <" +
                    state.playerName + "> " + state.message.getPlainString());
        }

        for (String s : state.getLogMessages()) {
            if (state.log) {
                LogManager.logger.info(s);
            } else {
                LogManager.logger.log(LogManager.getRuleLogLevel(),s);
            }
        }
    }

    public boolean append(ChainEntry r) {
        if (r.isValid()) {
            chain.add(r); // Add the Rule to this chain
            return true;
        } else return false;
    }

    public ArrayList<ChainEntry> getChain() {
        return chain;
    }

    public boolean isEmpty() {
        return chain.isEmpty();
    }

    public boolean isValid() {
        return chainState == ChainState.READY;
    }

    /**
     * The DataCache object needs to know what permissions to cache.  Whenever this
     * rulechain is updated, the datacache should also be updated with the list of
     * permissions which are interesting.
     *
     * @return a Set of all permissions that this rule is interested in.
     */
    @Override
    public Set<String> getPermissionList() {
        TreeSet<String> permList = new TreeSet<String>();

        for (ChainEntry r : chain) {
            permList.addAll(r.getPermissionList());
        }
        return permList;
    }

    public HashMap<String,ArrayList<Action>> getActionGroups() {
        return actionGroups;
    }

    public HashMap<String,ArrayList<Condition>> getConditionGroups() {
        return conditionGroups;
    }
    /**
     * Delete all rules in the chain, and reset its state to INIT
     */
    public void resetChain() {
        chain = new ArrayList<ChainEntry>();
        conditionGroups = new HashMap<String, ArrayList<Condition>>();
        actionGroups = new HashMap<String, ArrayList<Action>>();
        chainState = ChainState.INIT;
    }

    public void addConditionGroup(String name, ArrayList<Condition> cGroup) {
        if (name != null && cGroup != null)
            if(!conditionGroups.containsKey(name)) {
                conditionGroups.put(name,cGroup);
            } else {
                LogManager.getInstance().debugLow("Condition Group named '"+name+"' already exists in chain: " + getConfigName());
            }
    }

    public void addActionGroup(String name, ArrayList<Action> aGroup) {
        if (name != null && aGroup != null)
            if(!actionGroups.containsKey(name)) {
                actionGroups.put(name,aGroup);
            } else {
                LogManager.getInstance().debugLow("Action Group named '"+name+"' already exists in chain: " + getConfigName());
            }
    }

}
