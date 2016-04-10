/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.chain.Chain;
import com.pwn9.filter.engine.rules.chain.ChainEntry;
import com.pwn9.filter.util.LimitedRegexCharSequence;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule object
 * <p/>
 * <P>Each Rule has a single match Pattern, an ArrayList of {@link com.pwn9.filter.engine.rules.Condition}'s and an ArrayList of {@link Action}'s</P>
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class Rule implements ChainEntry {
    private Pattern pattern;
    private String description = "";
    private String id = "";
    public static int matches = 0;

    private final List<Condition> conditions = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();
    public final List<String> includeEvents = new ArrayList<>();
    public final List<String> excludeEvents = new ArrayList<>();

        /* Constructors */

    /**
     * <p>Constructor for Rule.</p>
     */
    public Rule() {}

    /**
     * <p>Constructor for Rule.</p>
     *
     * @param matchStr a {@link java.lang.String} object.
     */
    public Rule(String matchStr) {
        this.pattern = Pattern.compile(matchStr, Pattern.CASE_INSENSITIVE);
    }

    /**
     * <p>Constructor for Rule.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     */
    public Rule(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>pattern</code>.</p>
     *
     * @return a {@link java.util.regex.Pattern} object.
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>pattern</code>.</p>
     *
     * @param pattern a {@link java.lang.String} object.
     */
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public void setId(String id) {
        this.id = id;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getConditionsMatching(String matchString) {
        Set<String> retVal = new HashSet<>();
        Condition.CondType matchType;

        try {
            matchType = Condition.CondType.valueOf(matchString);
        } catch (IllegalArgumentException e) {
            return retVal;
        }

        conditions.stream().filter(c -> c.type == matchType).
                forEach(c -> Collections.addAll(retVal, c.parameters.split("\\|")));

        return retVal;
    }

    /* Methods */

    /**
     * {@inheritDoc}
     *
     * apply this action to the current message / event.  May trigger other bukkit events.
     *
     */
    public void apply(FilterContext filterContext, Chain parent, Logger logger ) {

        // If finest logging is set, then generate our logging info. (This is a
        // lambda + Supplier pattern.)
        logger.finest(() -> "Testing Pattern: '" + pattern.toString() + "' on string: '" +
                filterContext.getModifiedMessage().toString()+"'");

        // Check if action matches the current state of the message
        LimitedRegexCharSequence limitedRegexCharSequence =
                new LimitedRegexCharSequence(filterContext.getModifiedMessage().toString(),100);
        final Matcher matcher = pattern.matcher(limitedRegexCharSequence);

        // If we don't match, return immediately with the original message
        try {
            if (!matcher.find()) return;
        } catch (RuntimeException ex) {
            logger.severe("Regex match timed out! Regex: " + pattern.toString());
            logger.severe("Failed string was: " + limitedRegexCharSequence);
            return;
        }

        // If we do match, update the pattern and rule in the filter.
        filterContext.setPattern(pattern);
        filterContext.setRule(this);

        // If Match, log it and then check any conditions.
        filterContext.addLogMessage("|" + filterContext.getFilterClient().getShortName() + "| MATCH " +
                (id.isEmpty() ? "" : "(" + id + ")") +
                " <" +
                filterContext.getAuthor().getName() + "> " + filterContext.getModifiedMessage().toString());

        logger.fine(() -> "Match String: " + matcher.group());

        for (Condition c : conditions) {
            // This checks that EVERY condition is met (conditions are AND)
            if (!c.check(filterContext)) {
                filterContext.addLogMessage("CONDITION not met <" + c.flag.toString() +
                        " " + c.type.toString() + " " + c.parameters + "> " + filterContext.getOriginalMessage());
                return;
            }

        }

        // If we get this far, execute the actions
        for (Action a : actions) {
            a.execute(filterContext);
        }

    }

    /**
     * <p>isValid.</p>
     *
     * @return a boolean.
     */
    public boolean isValid() {
        // Check that we have a valid pattern and at least one action
        return this.pattern != null && this.actions != null;
    }

    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString() {
        return pattern.toString();
    }

    /**
     * <p>addCondition.</p>
     *
     * @param c a {@link com.pwn9.filter.engine.rules.Condition} object.
     * @return a boolean.
     */
    public boolean addCondition(Condition c) {
        return c != null && conditions.add(c);
    }
    /**
     * <p>addConditions.</p>
     *
     * @param conditionList a {@link java.util.Collection} object.
     * @return a boolean.
     */
    public boolean addConditions(Collection<Condition> conditionList) {
        return conditionList != null && conditions.addAll(conditionList);
    }

    /**
     * <p>Getter for the field <code>conditions</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * <p>add.</p>
     *
     * @param a a {@link Action} object.
     * @return a boolean.
     */
    public boolean addAction(Action a) {
        return a != null && actions.add(a);
    }

    /**
     * <p>addActions.</p>
     *
     * @param actionList a {@link java.util.Collection} object.
     * @return a boolean.
     */
    public boolean addActions(Collection<Action> actionList) {
        return actionList != null && actions.addAll(actionList);
    }

    /**
     * <p>Getter for the field <code>actions</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Action> getActions() {
        return actions;
    }


}
