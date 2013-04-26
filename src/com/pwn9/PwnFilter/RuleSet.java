package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.action.Action;
import com.pwn9.PwnFilter.action.ActionFactory;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public enum CondFlag {
        NONE, ignore, require
    }

    enum CondType {
        permission, user, string
    }

    public RuleSet(final PwnFilter p) {
        plugin = p;
    }

    public boolean init(final File f) {
        return loadRules(f);
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

        FilterState state = new FilterState(event.getMessage(),event.getPlayer());

        runFilter(state);

        // Only update the message if it has been changed.
        if (state.messageChanged()){
            event.setMessage(state.message.getColoredString());
        }
        if (state.cancel) event.setCancelled(true);
        return true;
    }

    public boolean apply(PlayerCommandPreprocessEvent event) {
        // Take the message from the Command Event and send it through the filter.

        FilterState state = new FilterState(event.getMessage(),event.getPlayer());

        runFilter(state);

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

        FilterState state = new FilterState(signLines, event.getPlayer());

        runFilter(state);

        if (state.messageChanged()){
            // TODO: Can colors be placed on signs?  Wasn't working. Find out why.
            String[] words = state.message.getPlainString().split("\\b");
            String[] lines = new String[4];

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

        if (state.log) {
            for (String s : state.logMessages) plugin.logToFile(s);
        }
        return true;
    }


    public void runFilter(FilterState state) {

        for (Rule rule : ruleChain) {
            rule.apply(state);
            if (state.stop) {
                break;
            }
        }

        if (plugin.debugEnable) {
            if (state.pattern != null) {
                state.addLogMessage("[PwnFilter] Debug match: " + state.pattern.pattern());
                state.addLogMessage("[PwnFilter] Debug original: " + state.getOriginalMessage().getColoredString());
                state.addLogMessage("[PwnFilter] Debug current: " + state.message.getColoredString());
                state.addLogMessage("[PwnFilter] Debug log: " + (state.log?"yes":"no"));
                state.addLogMessage("[PwnFilter] Debug deny: " + (state.cancel?"yes":"no"));
            } else {
                state.addLogMessage("[PwnFilter] Debug no match: " + state.getOriginalMessage().getColoredString());
            }

            state.log = true;
        }
        if (state.log) {
            if (state.cancel){
                state.addLogMessage("SENT <"+state.player.getName() + "> message cancelled by deny rule.");
            }
            state.addLogMessage("SENT <"+state.player.getName() + "> " + state.getOriginalMessage().getPlainString());

            for (String s : state.logMessages) {
                plugin.logToFile(s);
            }

        }

    }

    /**
     * Rule object
     * <p/>
     * <P>Each Rule has a single match Pattern, an ArrayList of {@link Condition}'s and an ArrayList of {@link com.pwn9.PwnFilter.action.Action}'s</P>
     * TODO: Finish docs
     */
    public class Rule {
        /**
         * A Rule contains the match regex, conditions and actions for a action.
         * Conditions are checked in order.  The first condition that matches
         */
        final Pattern pattern;
        String name; // TODO: Give rules names for logs and troubleshooting
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        ArrayList<Action> actions = new ArrayList<Action>();
        boolean log = false;


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
                    if (plugin.debugEnable) state.addLogMessage("CONDITION not met <"+ c.flag.toString()+
                            " " + c.type.toString()+" " + c.parameters + "> " + state.getOriginalMessage());
                    return false;
                }

            }

            // If we get this far, execute the actions
            for (Action a : actions) {
                a.execute(plugin, state);
            }
            return true;
        }

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
                    plugin.logToFile("Unable to add action to rule:" + parameterString);
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
                    plugin.logToFile("Unable to add condition to rule: " + command + " " + parameterString);
                    return false;
                }
            }

            plugin.logToFile("Unable to parse line: Command: " + command + " Data: " + parameterString);
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

    /**
     * Load rules from a file
     */
    public boolean loadRules(File rulesFile) {

        // Now read in the rules.txt file
        try {
            BufferedReader input = new BufferedReader(new FileReader(rulesFile));
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
                // TODO: There has to be a better way?  Worst case, we need a method, because this is a recurring pattern. :tokenizer:
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
                        ruleChain.add(currentRule);
                    }  // TODO: Should we warn if an invalid action is discarded?

                    // Now start on a new action.  If the match string is invalid, we'll still get the new action,
                    // and we'll still collect statements until the next match, but we'll throw it all away,
                    // because it won't be valid.
                    currentRule = new Rule(lineData);
                } else {
                    // Not a match statement, so much be part of a rule.
                    if (currentRule != null) currentRule.addLine(command, lineData);
                }
            }

            // Make sure we add the last action
            if (currentRule != null && currentRule.isValid()) ruleChain.add(currentRule);

            input.close();
            plugin.logToFile("Read " + count.toString() + " rules from file.  Installed " + ruleChain.size() + " valid rules.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return !ruleChain.isEmpty();
    }

}