package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;


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
 *
 * User: ptoal
 * Date: 13-04-05
 * Time: 12:38 PM
 */

public class RuleSet implements ChainEntry {
    private ArrayList<ChainEntry> ruleChain = new ArrayList<ChainEntry>();
    private final String configFile;
    private PwnFilter plugin;

    public RuleSet(PwnFilter plugin, String configFile) {
        this.plugin = plugin;
        this.configFile = configFile;
    }

    public boolean loadConfigFile() {
        final File f = plugin.getRulesFile(configFile);
        ruleChain = new ArrayList<ChainEntry>();

        try {
            return loadRules(new FileReader(f));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public String getConfigName() { return configFile ;}

    public int ruleCount() {
        return ruleChain.size();
    }

    /**
     * Iterate over the ruleChain in order, checking the Rule pattern against the
     * current message.  If the text pattern matches, test the rule conditions, to
     * ensure they are all met.  If all of the conditions are met, execute the Rule's
     * actions in sequential order.  If the Rule sets the stop=true of the FilterState,
     * stop processing rules.  If not, continue along the rule chain, checking the
     * (possibly modified) message against subsequent rules.
     */

    public boolean apply(FilterState state) {

        PwnFilter.EventType eventType = state.eventType;

        if (ruleChain == null) {
            PwnFilter.logger.severe("ruleChain not found.  Please report this as a bug.");
            return false;
        }

        if (PwnFilter.debugMode.compareTo(PwnFilter.DebugModes.medium) >= 0) {
              PwnFilter.logger.finer("Event: " + state.eventType.toString() + " message: " + state.getOriginalMessage());
        }

        for (ChainEntry entry : ruleChain) {
            entry.apply(state);
            if (state.stop) {
                break;
            }
        }

        if (PwnFilter.debugMode.compareTo(PwnFilter.DebugModes.high) >=0) {
            if (state.pattern != null) {
                PwnFilter.logger.finer("Debug last match: " + state.pattern.pattern());
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
        } else if (state.pattern != null) {
            state.addLogMessage("|" + state.eventType.toString() + "| SENT <" +
                    state.playerName + "> " + state.message.getPlainString());
        }

        for (String s : state.getLogMessages()) {
            if (state.log || PwnFilter.debugMode.compareTo(PwnFilter.DebugModes.low) >= 0) {
                PwnFilter.logger.log(PwnFilter.getRuleLogLevel(),s);
            }
        }
        return true;
    }

    public boolean append(Rule r) {
        if (r.isValid()) {
            ruleChain.add(r); // Add the Rule to this chain
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
            PwnFilter.logger.config(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return !ruleChain.isEmpty();
    }

    public boolean isValid() {
        return !ruleChain.isEmpty();
    }

    @Override
    public Set<String> getPermissionList() {
        TreeSet<String> permList = new TreeSet<String>();

        for (ChainEntry r : ruleChain ) {
            permList.addAll(r.getPermissionList());
        }

        return permList;

    }
}