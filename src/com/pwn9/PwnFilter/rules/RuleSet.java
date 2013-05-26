package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

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

public class RuleSet {
    public final PwnFilter plugin;
    private ArrayList<Rule> ruleChain = new ArrayList<Rule>();
    private ArrayList<Rule> chatRules = new ArrayList<Rule>();
    private ArrayList<Rule> signRules = new ArrayList<Rule>();
    private ArrayList<Rule> commandRules = new ArrayList<Rule>();

    public RuleSet(final PwnFilter p) {
        plugin = p;
    }

    public boolean init(final File f) {
        try {
            return loadRules(new FileReader(f));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    /**
     * Iterate over the ruleChain in order, checking the Rule pattern against the
     * current message.  If the text pattern matches, test the rule conditions, to
     * ensure they are all met.  If all of the conditions are met, execute the Rule's
     * actions in sequential order.  If the Rule sets the stop=true of the FilterState,
     * stop processing rules.  If not, continue along the rule chain, checking the
     * (possibly modified) message against subsequent rules.
     *
     * @param event The event being handled.
     * @return true if successful
     */
    public boolean apply(AsyncPlayerChatEvent event) {
        // Take the message from the ChatEvent and send it through the filter.

        FilterState state = new FilterState(plugin, event.getMessage(),event.getPlayer());

        runFilter(state, chatRules);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setMessage(state.message.getColoredString());
        }
        if (state.cancel) event.setCancelled(true);
        return true;
    }

    public boolean apply(PlayerCommandPreprocessEvent event) {
        // Take the message from the Command Event and send it through the filter.

        FilterState state = new FilterState(plugin, event.getMessage(),event.getPlayer());

        runFilter(state, commandRules);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setMessage(state.message.getColoredString());
        }
        if (state.cancel) event.setCancelled(true);
        return true;
    }

    public boolean apply(SignChangeEvent event) {
        // Take the message from the CommandPreprocessEvent and send it through the filter.
        StringBuilder builder = new StringBuilder();

        for (String l :event.getLines()) {
            builder.append(l).append(" ");
        }
        String signLines = builder.toString();

        FilterState state = new FilterState(plugin, signLines, event.getPlayer());

        runFilter(state, signRules);

        if (state.messageChanged()){
            // TODO: Can colors be placed on signs?  Wasn't working. Find out why.
            // Break the changed string into words
            String[] words = state.message.getPlainString().split("\\b");
            String[] lines = new String[4];

            // Iterate over the 4 sign lines, applying one word at a time, until the line is full.
            // If all 4 lines are full, the rest of the words are just discarded.
            // This may negatively affect plugins that use signs and require text to appear on a certain
            // line, but we only do this when we've matched a rule.
            int wordIndex = 0;
            for (int i = 0 ; i < 4 ; i++) {
                lines[i] = "";
              while (wordIndex < words.length) {
                  if (lines[i].length() + words[wordIndex].length() < 15) {
                      lines[i] = lines[i] + words[wordIndex] + " ";
                      wordIndex++;
                  } else {
                      break;
                  }
              }
            }

            for (int i = 0 ; i < 4 ; i++ ) {
                if (lines[i] != null) {
                    event.setLine(i,lines[i]);
                }
            }
        }

        if (state.cancel) {
            event.setCancelled(true);
            state.player.sendMessage("Your sign broke, there must be something wrong with it.");
            state.addLogMessage("SIGN " + state.player.getName() + " sign text: "
                    + state.getOriginalMessage().getColoredString());
        }

        return true;
    }

    public void runFilter(FilterState state, ArrayList<Rule> chain) {

        if (plugin.debugMode.compareTo(PwnFilter.DebugModes.medium) >= 0) {
            plugin.logger.finer("Checking: " + state.getOriginalMessage());
        }
        for (Rule rule : chain) {
            rule.apply(state);
            if (state.stop) {
                break;
            }
        }

        if (plugin.debugMode == PwnFilter.DebugModes.high) {
            if (state.pattern != null) {
                plugin.logger.finer("Debug match: " + state.pattern.pattern());
                plugin.logger.finer("Debug original: " + state.getOriginalMessage().getColoredString());
                plugin.logger.finer("Debug current: " + state.message.getColoredString());
                plugin.logger.finer("Debug log: " + (state.log?"yes":"no"));
                plugin.logger.finer("Debug deny: " + (state.cancel?"yes":"no"));
            } else {
                plugin.logger.finer("[PwnFilter] Debug no match: " + state.getOriginalMessage().getColoredString());
            }
        }

        if (state.cancel){
            state.addLogMessage("<"+state.player.getName() + "> Original message cancelled.");
        } else if (state.pattern != null ||
                plugin.debugMode.compareTo(PwnFilter.DebugModes.low) >= 0) {
            state.addLogMessage("SENT <"+state.player.getName() + "> " + state.message.getPlainString());
        }

        for (String s : state.getLogMessages()) {
            if (state.log) {
                plugin.logger.info(s);
            } else plugin.logger.log(plugin.ruleLogLevel,s);
        }

    }

    public boolean append(Rule r) {
        if (r.isValid()) {
            ruleChain.add(r);
            for (Rule.EventType e : r.events ) {
                if (e == Rule.EventType.sign) signRules.add(r);
                else if (e == Rule.EventType.chat) chatRules.add(r);
                else if (e == Rule.EventType.command) commandRules.add(r);
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
                            plugin.logger.warning("Unable to add action/condition to rule: " + command + " " + lineData);
                        }
                    }
                }
            }

            // Make sure we add the last action
            if (currentRule != null && currentRule.isValid()) append(currentRule);

            input.close();

            plugin.logger.config("Read " + count.toString() + " rules from file.  Installed " + ruleChain.size() + " valid rules.");
            plugin.logger.config("Command Rules: " + commandRules.size() + " Sign Rules: " + signRules.size() + " Chat Rules: " + chatRules.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return !ruleChain.isEmpty();
    }

}