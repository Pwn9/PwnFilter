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

package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.rules.chain.ChainEntry;
import com.pwn9.filter.util.LimitedRegexCharSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule object
 * <p>
 * Each Rule has a single match Pattern, an ArrayList of {@link Condition}'s and an ArrayList of {@link Action}'s
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class Rule implements ChainEntry {
    public static int matches = 0;
    private final List<Condition> conditions = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();
    private Pattern pattern;
    private String description = "";
    private String id = "";

        /* Constructors */

    /**
     * <p>Constructor for Rule.</p>
     */
    public Rule() {
    }

    /**
     * <p>Constructor for Rule.</p>
     *
     * @param matchStr a {@link String} object.
     */
    public Rule(String matchStr) {
        this.pattern = Pattern.compile(matchStr, Pattern.CASE_INSENSITIVE);
    }

    /**
     * <p>Constructor for Rule.</p>
     *
     * @param id          a {@link String} object.
     * @param description a {@link String} object.
     */
    public Rule(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>pattern</code>.</p>
     *
     * @return a {@link Pattern} object.
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * <p>Setter for the field <code>pattern</code>.</p>
     *
     * @param pattern a {@link String} object.
     */
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link String} object.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
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
     * <p>
     * apply this action to the current message / event.  May trigger other bukkit events.
     */
    public void apply(FilterContext filterContext, FilterService filterService) {

        Logger logger = filterService.getLogger();

        // If finest logging is set, then generate our logging info. (This is a
        // lambda + Supplier pattern.)
        logger.finest(() -> "Testing Pattern: '" + pattern.toString() + "' on string: '" +
                filterContext.getModifiedMessage().toString() + "'");

        // Check if action matches the current state of the message
        CharSequence limitedRegexCharSequence =
                new LimitedRegexCharSequence(filterContext.getModifiedMessage().toString(), 1000);
        final Matcher matcher = pattern.matcher(limitedRegexCharSequence);

        // If we don't match, return immediately with the original message
        try {
            if (!matcher.find()) return;
        } catch (LimitedRegexCharSequence.RegexTimeoutException ex) {
            logger.severe("Regex match timed out! Regex: " + pattern.toString());
            logger.severe("Failed string was: " + limitedRegexCharSequence);
            return;
        } catch (RuntimeException ex) {
            // Note: Due to this:
            // https://stackoverflow.com/questions/16008974/strange-java-unicode-regular-expression-stringindexoutofboundsexception
            // Supplementary UTF characters will cause index-out-of-bounds.  Not sure what to do about this, right now.
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
            a.execute(filterContext, filterService);
        }

    }

    /**
     * <p>isValid.</p>
     *
     * @return a boolean.
     */
    public boolean isValid() {
        // Check that we have a valid pattern and at least one action
        return this.pattern != null;
    }

    /**
     * <p>toString.</p>
     *
     * @return a {@link String} object.
     */
    public String toString() {
        return pattern.toString();
    }

    /**
     * <p>addCondition.</p>
     *
     * @param c a {@link Condition} object.
     * @return a boolean.
     */
    public boolean addCondition(Condition c) {
        if (c == null) {
            return false;
        }
        conditions.add(c);
        return true;
    }

    /**
     * <p>addConditions.</p>
     *
     * @param conditionList a {@link Collection} object.
     * @return a boolean.
     */
    public boolean addConditions(Collection<Condition> conditionList) {
        return conditionList != null && conditions.addAll(conditionList);
    }

    /**
     * <p>Getter for the field <code>conditions</code>.</p>
     *
     * @return a {@link List} object.
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
        if (a == null) return false;
        actions.add(a);
        return true;
    }

    /**
     * <p>addActions.</p>
     *
     * @param actionList a {@link Collection} object.
     * @return a boolean.
     */
    public boolean addActions(Collection<Action> actionList) {
        return actionList != null && actions.addAll(actionList);
    }

    /**
     * <p>Getter for the field <code>actions</code>.</p>
     *
     * @return a {@link List} object.
     */
    public List<Action> getActions() {
        return actions;
    }


}
