package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;


/**
 * The RuleSet contains a compiled version of all the rules in the text file.
 * A RuleSet has a chain of Rules.  The ruleset apply method is unique to each
 * type of event, but the basic mechanism is:
 * <p>
 * Load a chain of rules from a text file, parse them into a ruleChain
 * The Event Handler calls the RuleSet.appply() method with the signature of the event
 * The apply() method iterates over the rules, one at a time, matching, checking conditions,
 * and executing actions based on the message and the rules.
 *
 *
 * TODO: More documentation
 * TODO: Implement ObuShutTheHellUp style functionality.
 *
 * User: ptoal
 * Date: 13-04-05
 * Time: 12:38 PM
 */

public class RuleSet {
    public final PwnFilter plugin;
    private ArrayList<Rule> ruleChain = new ArrayList<Rule>();
    public ArrayList<String> permList = new ArrayList<String>();

    // EnumMap that contains a ruleChain (also ArrayList) for each type of event.
    private EnumMap<PwnFilter.EventType,ArrayList<Rule>> eventChain;

    public RuleSet(final PwnFilter p) {
        plugin = p;

        eventChain = new EnumMap<PwnFilter.EventType,ArrayList<Rule>>(PwnFilter.EventType.class);

        for (PwnFilter.EventType e : PwnFilter.EventType.values()) {
            eventChain.put(e, new ArrayList<Rule>());
        }
    }

    public boolean init(final File f) {

        try {
            return loadRules(new FileReader(f));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public int ruleCount() {
        return ruleChain.size();
    }

    public int ruleCount(PwnFilter.EventType r) {
        return eventChain.get(r).size();
    }


    /**
     * Iterate over the ruleChain in order, checking the Rule pattern against the
     * current message.  If the text pattern matches, test the rule conditions, to
     * ensure they are all met.  If all of the conditions are met, execute the Rule's
     * actions in sequential order.  If the Rule sets the stop=true of the FilterState,
     * stop processing rules.  If not, continue along the rule chain, checking the
     * (possibly modified) message against subsequent rules.
     */

    public void runFilter(FilterState state) {

        PwnFilter.EventType eventType = state.eventType;

        ArrayList<Rule> chain = eventChain.get(eventType);

        if (chain == null) {
            PwnFilter.logger.severe("ruleChain not found.  Please report this as a bug.");
            return;
        }

        if (PwnFilter.debugMode.compareTo(PwnFilter.DebugModes.medium) >= 0) {
              PwnFilter.logger.finer("Event: " + state.eventType.toString() + " message: " + state.getOriginalMessage());
        }

        for (Rule rule : chain) {
            rule.apply(state);
            if (state.stop) {
                break;
            }
        }

        if (PwnFilter.debugMode == PwnFilter.DebugModes.high) {
            if (state.pattern != null) {
                PwnFilter.logger.finer("Debug match: " + state.pattern.pattern());
                PwnFilter.logger.finer("Debug original: " + state.getOriginalMessage().getColoredString());
                PwnFilter.logger.finer("Debug current: " + state.message.getColoredString());
                PwnFilter.logger.finer("Debug log: " + (state.log?"yes":"no"));
                PwnFilter.logger.finer("Debug deny: " + (state.cancel?"yes":"no"));
            } else {
                PwnFilter.logger.finer("[PwnFilter] Debug no match: " + state.getOriginalMessage().getColoredString());
            }
        }

        if (state.cancel){
            state.addLogMessage("<"+state.playerName + "> Original message cancelled.");
        } else if (state.pattern != null ||
                PwnFilter.debugMode.compareTo(PwnFilter.DebugModes.low) >= 0) {
            state.addLogMessage("|" + state.eventType.toString() + "| SENT <" +
                    state.playerName + "> " + state.message.getPlainString());
        }

        for (String s : state.getLogMessages()) {
            if (state.log) {
                PwnFilter.logger.info(s);
            } else PwnFilter.logger.log(plugin.ruleLogLevel,s);
        }

    }

    public boolean append(Rule r) {
        if (r.isValid()) {
            ruleChain.add(r); // Add the Rule to the master chain
            for (PwnFilter.EventType e : r.events ) {
                if (PwnFilter.enabledEvents.contains(e)) {
                    eventChain.get(e).add(r);
                } else if (PwnFilter.debugMode.compareTo(PwnFilter.DebugModes.low) >= 0) {
                    PwnFilter.logger.fine("Unable to add rule: " + r.toString() + " to the: " + e.toString()
                            + " chain, as that filter is not enabled in the config.yml");
                }
            }
            return true;
        } else return false;
    }


    /**
     * Load rules from a file
     */
    public boolean loadRules(java.io.Reader rulesStream) {

        // Now read in the rules.txt file
        try {
            BufferedReader input = new BufferedReader(rulesStream);
            String line;
            Rule currentRule = null;
            Integer count = 0;

            while ((line = input.readLine()) != null) {

                line = line.trim();
                String command;
                String lineData = "";

                // SKIP this line if it is empty, or a comment
                if (line.isEmpty() || line.matches("^#.*")) continue;

                // SPLIT the line into the token and remainder.
                {
                    String[] parts = line.split("\\s", 2);
                    command = parts[0];
                    if (parts.length > 1 ) {
                        lineData = parts[1];
                    }
                }

                // MATCH statement.  Start a new action
                if (command.matches("match|catch|replace|rewrite")) {
                    count++;
                    // This is the start of a new action.
                    // If we currently have a valid action, add it to the set.
                    if (currentRule != null && currentRule.isValid()) {
                        append(currentRule);
                    }

                    // Now start on a new action.  If the match string is invalid, we'll still get the new action,
                    // and we'll still collect statements until the next match, but we'll throw it all away,
                    // because it won't be valid.
                    currentRule = new Rule(lineData);
                } else {
                    // Not a match statement, so much be part of a rule.
                    if (currentRule != null) {
                        if (!currentRule.addLine(command, lineData)) {
                            PwnFilter.logger.warning("Unable to add action/condition to rule: " + command + " " + lineData);
                        }
                    }
                }
            }

            // Make sure we add the last action
            if (currentRule != null && currentRule.isValid()) append(currentRule);

            input.close();

            PwnFilter.logger.config("Read " + count.toString() + " rules from file.  Installed " + ruleChain.size() + " valid rules.");
            StringBuilder sb = new StringBuilder();
            for (PwnFilter.EventType e : PwnFilter.enabledEvents) {
                sb.append(e.toString()).append(" Rules:").append(eventChain.get(e).size()).append(" ");
            }
            PwnFilter.logger.config(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add all permissions to the list of those we are interested in for the DataCache
        for (Rule r : ruleChain ) {
            for (Condition c : r.conditions) {
                if (c.type == Condition.CondType.permission) {
                    Collections.addAll(permList, c.parameters.split("\\|"));
                }
            }
        }

        return !ruleChain.isEmpty();
    }

}