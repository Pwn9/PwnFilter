package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.rules.action.ActionFactory;
import com.pwn9.PwnFilter.util.LimitedRegexCharSequence;
import com.pwn9.PwnFilter.util.LogManager;
import com.pwn9.PwnFilter.util.Patterns;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule object
 * <p/>
 * <P>Each Rule has a single match Pattern, an ArrayList of {@link Condition}'s and an ArrayList of {@link com.pwn9.PwnFilter.rules.action.Action}'s</P>
 * TODO: Finish docs
 */
public class Rule implements ChainEntry {
    private Pattern pattern;
    private String description;
    private String id;
//    String name; // TODO: Give rules names for logs and troubleshooting
    ArrayList<Condition> conditions = new ArrayList<Condition>();
    ArrayList<Action> actions = new ArrayList<Action>();
    ArrayList<String> includeEvents = new ArrayList<String>();
    ArrayList<String> excludeEvents = new ArrayList<String>();

        /* Constructors */

    public Rule(String matchStr) {
        this.pattern = Patterns.compilePattern(matchStr);
        this.id = "";
    }

    public Rule(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setPattern(String pattern) {
        this.pattern = Patterns.compilePattern(pattern);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Set<String> getPermissionList() {
        TreeSet<String> permList = new TreeSet<String>();

        for (Condition c : conditions) {
            if (c.type == Condition.CondType.permission) {
                Collections.addAll(permList, c.parameters.split("\\|"));
            }
        }

        return permList;
    }

    /* Methods */

    /**
     * apply this action to the current message / event.  May trigger other bukkit events.
     * @param state A FilterState object for this event.
     */
    public void apply(FilterState state) {

        // Check if action matches the current state of the message

        if (LogManager.debugMode.compareTo(LogManager.DebugModes.high) >= 0) {
            LogManager.logger.info("Testing Pattern: " + pattern.toString() + " on string: " + state.message.getPlainString());
        }

            LimitedRegexCharSequence limitedRegexCharSequence = new LimitedRegexCharSequence(state.message.getPlainString(),1000);
            final Matcher matcher = pattern.matcher(limitedRegexCharSequence);
        // If we don't match, return immediately with the original message
        try {
            if (!matcher.find()) return;
        } catch (RuntimeException ex) {
            LogManager.logger.severe("Regex match timed out! Regex: " + pattern.toString());
            LogManager.logger.severe("Failed string was: " + limitedRegexCharSequence);
        }

        state.pattern = pattern;
        state.rule = this;

        // If Match, log it and then check any conditions.
        state.addLogMessage("|" + state.listener.getShortName() +  "| MATCH " +
                (id.isEmpty()?"":"("+id+")") +
                " <" +
                state.playerName + "> " + state.message.getPlainString());
        LogManager.getInstance().debugLogLow("Match String: " + matcher.group());


        for (Condition c : conditions) {
            // This checks that EVERY condition is met (conditions are AND)
            if (!c.check(state)) {
                state.addLogMessage("CONDITION not met <"+ c.flag.toString()+
                        " " + c.type.toString()+" " + c.parameters + "> " + state.getOriginalMessage());
                return;
            }

        }

        if(PwnFilter.matchTracker != null) {
            PwnFilter.matchTracker.increment(); // Update Match Statistics
        }

        // If we get this far, execute the actions
        for (Action a : actions) {
            a.execute(state);
        }

    }

    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * When we are building a new rule chain (eg, by reloading a file), this routine parses
     * out a line (eg: "then replace") and adds it to this rule.
     * @param command the first part of the line, eg: "then" or "ignore"
     * @param parameterString the remainder of the line eg: "kick" or "user tremor77"
     * @return true on success, false on failure
     */
    public boolean addLine(String command, final String parameterString) {

        command = command.toLowerCase();

        if (command.matches("then")) {
            // This is an action.  Try to add a new action with its parameters.

            Action newAction = ActionFactory.getActionFromString(parameterString);
            if (newAction != null) {
                actions.add(newAction);
                return true;
            } else {
                return false;
            }

        }
        else if (command.matches("events")) {
            String[] parts = parameterString.split("[\\s|,]");

            if (parts[0].matches("not")) {
                for (int i = 1; i < parts.length ; i++ ) {
                    excludeEvents.add(parts[i].toUpperCase());
                }
            } else {
                for (String event : parts ) {
                    includeEvents.add(event.toUpperCase());
                }
            }

            return true;
        }

        else if ( Condition.isCondition(command) )  {
            // This is a condition.  Add a new condition to this rule.
            Condition newCondition = Condition.newCondition(command,parameterString);
            return newCondition != null && conditions.add(newCondition);
        }

        // This line isn't a condition or an action...
        return false;
    }


    public boolean isValid() {
        // Check that we have a valid pattern and at least one action
        return this.pattern != null && this.actions != null;
    }

    public String toString() {
        return pattern.toString();
    }

}