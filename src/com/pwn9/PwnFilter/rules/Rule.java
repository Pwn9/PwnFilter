package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.util.Patterns;
import com.pwn9.PwnFilter.rules.action.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule object
 * <p/>
 * <P>Each Rule has a single match Pattern, an ArrayList of {@link Condition}'s and an ArrayList of {@link com.pwn9.PwnFilter.rules.action.Action}'s</P>
 * TODO: Finish docs
 */
public class Rule {
    /**
     * A Rule contains the match regex, conditions and actions for a action.
     * Conditions are checked in order.  The first condition that matches
     */
    final Pattern pattern;
//    String name; // TODO: Give rules names for logs and troubleshooting
    ArrayList<Condition> conditions = new ArrayList<Condition>();
    ArrayList<Action> actions = new ArrayList<Action>();
    boolean log = false;

    public enum CondFlag {
        NONE, ignore, require
    }

    enum CondType {
        permission, user, string
    }

        /* Constructors */

    // All rules must have a matchStr, hence no parameter-less constructor.
    public Rule(String matchStr) {
        this.pattern = Patterns.compilePattern(matchStr);
    }

    /* Methods */

    /**
     * apply this action to the current message / event.  May trigger other bukkit events.
     * @param state A FilterState object for this event.
     * @return true if action was taken, false if not.
     */
    public boolean apply(FilterState state) {

        // Check if action matches the current state of the message
        final Matcher matcher = pattern.matcher(state.message.getPlainString());

        // If we don't match, return immediately with the original message
        if (!matcher.find()) return false;
        state.pattern = pattern;

        // If Match, log it and then check any conditions.

        state.addLogMessage("MATCH <"+ state.player.getName() + "> " + state.message.getPlainString());

        for (Condition c : conditions) {
            // This checks that EVERY condition is met (conditions are AND)
            if (!c.check(state.player, state.message.getPlainString())) {
                state.addLogMessage("CONDITION not met <"+ c.flag.toString()+
                        " " + c.type.toString()+" " + c.parameters + "> " + state.getOriginalMessage());
                return false;
            }

        }

        // If we get this far, execute the actions
        for (Action a : actions) {
            a.execute(state);
        }
        return true;
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
        CondFlag flag = CondFlag.NONE;
        String subCmd;
        String lineData = "";


        if (command.matches("then")) {
            // This is an action.  Try to add a new action with its parameters.
            if (parameterString.matches("log")) {
                log = true;
                return true;
            }
            Action newAction = ActionFactory.getActionFromString(parameterString);
            if (newAction != null) {
                actions.add(newAction);
                return true;
            } else {
                return false;
            }

        }
        else if (command.matches("ignore")) flag = CondFlag.ignore; // This is an ignore condition
        else if (command.matches("require")) flag = CondFlag.require;  // This is a require condition

        if (flag != CondFlag.NONE) { // If we have a condition, process it now.

            // Now split the parameters to find the type of condition
            {
                String[] parts = parameterString.split("\\s", 2);
                subCmd = parts[0].toLowerCase();
                if (parts.length > 1) {
                    lineData = parts[1];
                }
            }
            try {
                CondType cType = CondType.valueOf(subCmd);
                Condition newCond = new Condition(flag, cType, lineData);
                conditions.add(newCond);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    public boolean isValid() {
        // Check that we have a valid pattern and at least one action
        return this.pattern != null && this.actions != null;
    }

    class Condition {
        final CondType type;
        final CondFlag flag;
        final String parameters;

        public Condition(CondFlag f, CondType t, String p) {
            flag = f;
            parameters = p;
            type = t;
        }

        /**
         * Checks a message against this condition.  This method returns true if
         * the condition is met, false otherwise.  Processing of the current rule
         * will be aborted if _any_ check returns false.
         *
         * @param player The player sending this message
         * @param message The message to be checked
         * @return true if this condition is met, false otherwise
         */
        public boolean check(Player player, String message) {
            boolean matched = false;
            switch (type) {
                case user:
                    String playerName = player.getName();
                    for (String check : parameters.split("\\s")) {
                        if (playerName.equalsIgnoreCase(check)) matched = true;
                    }
                case permission:
                    for (String check: parameters.split("\\s")) {
                        if (player.hasPermission(check)) matched = true;
                    }
                case string:
                    for (String check: parameters.split("\\|")) {
                        if (ChatColor.stripColor(message.replaceAll("&([0-9a-fk-or])", "\u00A7$1")).
                                toUpperCase().contains(check.toUpperCase())) matched = true;
                    }

            }
            switch (flag) {
                case ignore:
                    return !matched;
                case require:
                    return matched;
            }
            // Well, we shouldn't be able to get here, but in case we did, return false
            return false;
        }

    }

}
