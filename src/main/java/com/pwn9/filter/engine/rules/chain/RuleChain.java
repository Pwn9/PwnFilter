/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.chain;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.Condition;
import com.pwn9.filter.engine.rules.Rule;

import java.io.InvalidObjectException;
import java.util.*;
import java.util.logging.Logger;


/**
 * The RuleChain contains a compiled version of all the rules in the text file.
 * A RuleChain is a chain of Rules.  The high-level process is:
 * <p/>
 * Load a chain of rules from a text file, parse them into a chain
 * The Event Handler calls the RuleChain.apply() method with the FilterContext
 * The apply() method iterates over the rules one at a time; matching, checking
 * conditions, and executing actions based on the message and the rules.
 * <p/>
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class RuleChain implements Chain, ChainEntry {

    private final ImmutableList<ChainEntry> chain;
    private final ImmutableMultimap<String, Action> actionGroups;
    private final ImmutableMultimap<String, Condition> conditionGroups;
    private final int ruleCount;
    private final String configName;


    public RuleChain(List<ChainEntry> chain, String configName,
                     Multimap<String, Action> actionGroups,
                     Multimap<String, Condition> conditionGroups)
    {
        this.chain = ImmutableList.copyOf(chain);
        this.configName = configName;
        this.actionGroups = ImmutableMultimap.copyOf(actionGroups);
        this.conditionGroups = ImmutableMultimap.copyOf(conditionGroups);

        // Count & Cache the number of rules.
        Integer count = 0;
        for (ChainEntry c : chain) {
            if (c instanceof RuleChain) {
                count += ((RuleChain) c).ruleCount();
            } else count++;
        }
        ruleCount = count;

    }

    /**
     * <p>Getter for the field <code>configName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * <p>ruleCount.</p>
     *
     * @return a int.
     */
    public int ruleCount() {
        return ruleCount;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Iterate over the chain in order, checking the Rule pattern against the
     * current message.  If the text pattern matches, test the rule conditions, to
     * ensure they are all met.  If all of the conditions are met, execute the Rule's
     * actions in sequential order.  If the Rule sets the stop=true of the FilterTask,
     * stop processing rules.  If not, continue along the rule chain, checking the
     * (possibly modified) message against subsequent rules.
     */
    public void apply(FilterContext context, Chain parent, Logger logger) throws IllegalStateException {

        for (ChainEntry entry : chain) {
            if (context.isAborted()) break;
            entry.apply(context, parent, logger);
        }
    }

    /**
     * <p>execute.</p>
     *
     * @param context a {@link FilterContext} object.
     */
    public void execute(FilterContext context, Logger logger) {
        apply(context, EmptyChain.INSTANCE, logger);

        if (!context.getMatchedRules().isEmpty()) {
            //TODO: Add counter for matches, here.
            logger.finer(() ->
                    {
                        StringBuilder sb = new StringBuilder();
                        for (Rule r : context.getMatchedRules()) {
                            sb.append(" ");
                            sb.append(r.getId().isEmpty() ? "pattern: " + r.getPattern() : "Rule ID: " + r.getId());
                        }

                        return "\nDebug matched" + sb.toString() +
                                "\nDebug original: " + context.getOriginalMessage().getRaw() +
                                "\nDebug current: " + context.getModifiedMessage().getRaw() +
                                "\nDebug log: " + (context.loggingOn() ? "yes" : "no") +
                                "\nDebug deny: " + (context.isCancelled() ? "yes" : "no");
                    }
            );

        } else {
            logger.finer(() -> "Debug no match: " + context.getOriginalMessage().getRaw());
        }

        if (context.isCancelled()) {
            context.addLogMessage("<" + context.getAuthor().getName() + "> Original message cancelled.");
        } else if (context.getPattern() != null) {
            context.addLogMessage("|" + context.getFilterClient().getShortName() + "| SENT <" +
                    context.getAuthor().getName() + "> " + context.getModifiedMessage().toString());
        }

        context.getLogMessages().stream().filter(s -> context.loggingOn()).forEach(logger::info);
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
     * {@inheritDoc}
     * <p/>
     * Get the Set of all permissions used in this chain.
     */
    @Override
    public Set<String> getConditionsMatching(String matchString) {
        TreeSet<String> retVal = new TreeSet<>();

        for (ChainEntry r : chain) {
            retVal.addAll(r.getConditionsMatching(matchString));
        }
        return retVal;
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
     * Builds an immutable RuleChain object.
     * <p/>
     * Created by Sage905 on 15-10-03.
     */
    public static final class Builder {

        private final List<ChainEntry> chain;
        private String configName;
        private final Multimap<String, Action> actionGroups;
        private final Multimap<String, Condition> conditionGroups;

        public Builder() {
            actionGroups = ArrayListMultimap.create();
            conditionGroups = ArrayListMultimap.create();
            chain = new ArrayList<>();
        }

        /**
         * {@inheritDoc}
         */
        public void append(ChainEntry r) {
             chain.add(r); // Add the Rule to this chain
        }

        public void appendAll(List<ChainEntry> rules) {
            chain.addAll(rules);
        }

        public void setConfigName(String s) {
            this.configName = s;
        }

        public String getConfigName() {
            return configName;
        }
        /**
         * {@inheritDoc}
         */
        public void addConditionGroup(String name, List<Condition> cGroup) throws InvalidObjectException {
            if (name != null && cGroup != null)
                if (conditionGroups.get(name).isEmpty()) {
                    conditionGroups.get(name).addAll(cGroup);
                } else {
                    throw new InvalidObjectException("Condition Group named '" + name + "' already exists in chain: "
                            + configName);
                }
        }

        /**
         * {@inheritDoc}
         */
        public void addActionGroup(String name, List<Action> aGroup) throws InvalidObjectException {
            if (name != null && aGroup != null)
                if (!actionGroups.containsKey(name)) {
                    actionGroups.get(name).addAll(aGroup);
                } else {
                    throw new InvalidObjectException("Action Group named '" + name + "' already exists in chain: "
                            + configName);
                }
        }

        public Multimap<String, Action> getActionGroups() {
            return actionGroups;
        }

        public Multimap<String, Condition> getConditionGroups() {
            return conditionGroups;
        }

        public RuleChain build() {
            return new RuleChain(chain, configName, actionGroups, conditionGroups);
        }
    }


}
