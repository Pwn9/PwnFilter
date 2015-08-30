/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.parser;

import com.pwn9.PwnFilter.rules.*;
import com.pwn9.PwnFilter.rules.action.Action;
import com.pwn9.PwnFilter.rules.action.ActionFactory;
import com.pwn9.PwnFilter.util.LogManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Text-file Rule Parser
 * User: ptoal
 * Date: 13-11-16
 * Time: 3:17 PM
 *
 * @author ptoal
 * @version $Id: $Id
 */
public class FileParser {

    final private String filename;
    final private FileParser parent;
    final private boolean createFile;

    private int lineNo;
    private Map<String, String> shortcuts = null;
    private Chain chain;

    /**
     * <p>Constructor for FileParser.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @param parent a {@link com.pwn9.PwnFilter.rules.parser.FileParser} object.
     * @param createFile a boolean.
     */
    public FileParser(String filename, FileParser parent, boolean createFile) {
        this.filename = filename;
        this.parent = parent;
        this.createFile = createFile;
    }

    /**
     * <p>Constructor for FileParser.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public FileParser(String filename) {
        this(filename, null, true);
    }

    /**
     * <p>Getter for the field <code>filename</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * <p>Getter for the field <code>parent</code>.</p>
     *
     * @return a {@link com.pwn9.PwnFilter.rules.parser.FileParser} object.
     */
    public FileParser getParent() {
        return parent;
    }

    /**
     * <p>getParentFiles.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getParentFiles() {
        FileParser nextParent = parent;
        HashSet<String> result = new HashSet<String>();

        while (nextParent != null) {
            result.add(nextParent.getFilename());
            nextParent = nextParent.getParent();
        }

        return result;
    }

    /**
     * Load rules from a Reader stream.  This is the top-level parser for the file.
     *
     * @return true on successful read.  false if file was not found, or an IOException occurred.
     * @param chain a {@link com.pwn9.PwnFilter.rules.Chain} object.
     */
    public boolean parseRules(Chain chain) {
        RuleStreamReader reader;

        this.chain = chain;
        // Check to make sure this file isn't already in the parent chain
        if (getParentFiles().contains(filename)) {
            parserError(lineNo, "Recursion loop!  " + filename + " is included already!");
            return false;
        }

        // Open the file for reading.
        File ruleFile = RuleManager.getInstance().getFile(filename, createFile);
        if(ruleFile == null) {
            LogManager.logger.warning("File not found: " + filename + ". Aborting parsing.");
            return false;
        }
        try {
            reader = new RuleStreamReader(new FileReader(ruleFile));
        } catch (FileNotFoundException ex) {
            LogManager.logger.warning("File not found: " + filename + ". Aborting parsing.");
            return false;
        }

        // Now read the file.  This loop reads the top-level instructions, and passes
        // second-level handling off to individual methods.
        try {
            String line;

            while ((line = reader.readLine()) != null) {

                // Outside of a section, we ignore blank lines.
                if (line.isEmpty()) continue;

                TokenString tokenString = new TokenString(line);
                String command = tokenString.popToken();
                lineNo = reader.getLineNumber();

                try {
                    // Process an Action Group
                    if (command.equalsIgnoreCase("actiongroup")) {
                        String groupName = tokenString.popToken();
                        parseActionGroup(groupName,reader.readSection());
                    }
                    // Process a Condition Group
                    else if (command.equalsIgnoreCase("conditiongroup")) {
                        String groupName = tokenString.popToken();
                        parseConditionGroup(groupName,reader.readSection());
                    }
                    // Check if this is a toggle for shortcuts.
                    else if (command.equalsIgnoreCase("shortcuts")) {
                        String fileName = tokenString.popToken();
                        toggleShortcuts(fileName);
                    }
                    // Process an included file
                    else if (command.equalsIgnoreCase("include")) {
                        String fileName = tokenString.popToken();
                        processIncludedFile(fileName);
                    }
                    // Parse a rule starting with the pattern
                    else if (command.matches("match|catch|replace|rewrite")) {
                        String pattern = ShortCutManager.replace(shortcuts,tokenString.getString());
                        parseRule(new Rule(pattern), reader.readSection());
                    }
                    // Parse a rule starting with the ID/Description
                    else if (command.matches("rule")) {
                        String id = tokenString.popToken();
                        String descr = tokenString.getString();
                        parseRule(new Rule(id, descr), reader.readSection());
                    }
                } catch (ParserException e) {
                    parserError(e.getLineNo(),e.getMessage());
                }

            }

            reader.close();

        } catch (IOException e) {
            LogManager.logger.severe("IO Exception during processing: " + e.getMessage());
            return false;
        }
        return !chain.isEmpty();
    }


    /* Private Parser Methods */

    private boolean parseRule(Rule rule, List<NumberedLine> lines) throws IOException, ParserException {

        for (NumberedLine line : lines) {
            TokenString tokenString = new TokenString(line.string);
            String command = tokenString.popToken();

            // rule <id> [description]
            if (command.equalsIgnoreCase("rule")) {
                rule.setId(tokenString.popToken()); // First argument is the ID
                rule.setDescription(tokenString.getString()); // Second argument is the Description
            }
            else if (command.equalsIgnoreCase("match")) {
                rule.setPattern(ShortCutManager.replace(shortcuts, tokenString.getString()));
            }
            // conditions <conditiongroup>
            else if (command.equalsIgnoreCase("conditions")) {
                String groupName = tokenString.popToken();
                if (!rule.addConditions(chain.getConditionGroups().get(groupName))) {
                    throw new ParserException(line.number,"Unable to find Condition Group: " + groupName);
                }
            }
            // actions <actiongroup>
            else if (command.equalsIgnoreCase("actions")) {
                String groupName = tokenString.popToken();
                if (!rule.addActions(chain.getActionGroups().get(groupName))) {
                    throw new ParserException(line.number,"Unable to find Action Group: " + groupName);
                }
            }
            // then <action> [parameters]
            else if (command.equalsIgnoreCase("then")) {
                String actionName = tokenString.popToken();
                try {
                    Action newAction = ActionFactory.getAction(actionName,tokenString.getString());
                    if (!rule.addAction(newAction)) {
                        throw new ParserException(line.number,"Unable to add action to rule: " + actionName);
                    }
                } catch (IllegalArgumentException ex) {
                    parserError(line.number,"Error in action line: " + ex.getMessage());
                }
            }
            // condition <parameters>
            else if ( Condition.isCondition(command))  {
                // This is a condition.  Add a new condition to this rule.
                Condition newCondition = Condition.newCondition(tokenString.getOriginalString());
                if (!rule.addCondition(newCondition)) {
                    throw new ParserException(line.number,"Could not parse condition: " + tokenString.getOriginalString());
                }
            }
            // events [not] <event>,<event>...
            else if (command.equalsIgnoreCase("events")) {
                parserError(line.number, "Deprecation warning: 'events' keyword is deprecated.  Please add " +
                        "rules to the correct file instead of using 'events' (eg: command.txt, chat.txt, etc.)");
                List<String> eventlist;
                if (tokenString.getString().matches("^not\b")) {
                    eventlist = rule.excludeEvents;
                } else {
                    eventlist = rule.includeEvents;
                }
                String token;
                while (!(token = tokenString.popToken()).isEmpty()) {
                    for (String subtoken : token.split(",")) {
                        eventlist.add(subtoken.trim().toUpperCase());
                    }
                }
            }
        }
        if (rule != null && rule.isValid()) {
            chain.append(rule);
            return true;
        }

        throw new ParserException(lineNo,"Unable to parse a valid rule.");

    }

    /**
     * Parse the provided strings into action objects, and add the group to the ruleChain.
     *
     * @param groupName A String containing the name of the group
     * @param lines A list of Strings containing the actions to parse.
     * @return true if at least one action is successfully parsed
     * @throws IOException
     */
    private boolean parseActionGroup(String groupName, List<NumberedLine> lines) throws IOException, ParserException {

        ArrayList<Action> actionGroup = new ArrayList<Action>();

        for (NumberedLine line : lines) {
            TokenString tString = new TokenString(line.string);
            String command  = tString.popToken();

            if (command.equals("then")) command = tString.popToken();
            Action thisAction = ActionFactory.getAction(command,tString.getString());

            if (thisAction != null) {
                actionGroup.add(thisAction);
            } else {
               throw new ParserException(line.number, "Unable to parse action: " + command);
            }
        }

        if (actionGroup.size() == 0) {
            throw new ParserException(lineNo,"Empty actionGroup found: " + groupName);
        } else {
            chain.addActionGroup(groupName,actionGroup);
            return true;
        }

    }

    /**
     * Parse the provided strings into Condition objects, and add the group to the ruleChain.
     *
     * @param groupName A String containing the name of the group
     * @param lines A list of Strings containing the Conditions to parse.
     * @return true if at least one action is successfully parsed
     * @throws IOException
     */
    private boolean parseConditionGroup(String groupName, List<NumberedLine> lines) throws IOException, ParserException {

        ArrayList<Condition> conditionGroup = new ArrayList<Condition>();

        for (NumberedLine line : lines) {
            TokenString tString = new TokenString(line.string);
            String command  = tString.popToken();

            Condition thisCondition = Condition.newCondition(command, tString.getString());

            if (thisCondition != null) {
                conditionGroup.add(thisCondition);
            } else {
                throw new ParserException(line.number, "Unable to parse condition: " + command + " " + tString.getString());
            }
        }

        if (conditionGroup.size() == 0) {
            throw new ParserException(lineNo,"Empty Condition Group found: " + groupName);
        } else {
            chain.addConditionGroup(groupName,conditionGroup);
            return true;
        }

    }

    private void processIncludedFile(String lineData) throws ParserException {
        // Major change to the way this is done.  It used to be its own chain, which was linked.
        // Now, we are going to import each statement into this chain.  We will apply this chain's
        // shortcuts, actiongroups, etc. to the chain.  This will allow different chains to include
        // the same files, but have different actions.  Eg: a chat filter might want to kick a player
        // but an itemfilter would not kick the player, and instead destroy the item.
        LogManager.getInstance().debugMedium("Including chain: " + lineData + " in: " + chain.getConfigName());

        if (getParentFiles().contains(lineData))
            throw new ParserException(lineNo, "Recursion error.  File: " + lineData + " has already been included.");

        FileParser includedChainParser = new FileParser(lineData,this, false);

        includedChainParser.parseRules(chain);

    }

    // Updates parser with new shortcuts.
    private void toggleShortcuts(String name) throws ParserException {
        if (name.isEmpty()) {
            shortcuts = null;
        } else {
            shortcuts = ShortCutManager.getInstance().getShortcutMap(name);
            if (shortcuts == null || shortcuts.isEmpty()) {
                throw new ParserException(lineNo,"Could not load shortcuts file: " + name);
            }
        }
    }

    /**
     * The parserError method generates a standardized warning message in the
     * Minecraft console log.
     *
     * @param line The line number that triggered the error.
     * @param error A String containing the error message.
     */
    private void parserError(int line, String error) {
        LogManager.logger.warning(String.format("Parser Error (%s:%d) %s",filename,line,error));
    }

    /* Exceptions */

    @SuppressWarnings("UnusedDeclaration")
    class ParserException extends Exception {
        int lineNo;

        public ParserException(String error) {
            super(error);
        }

        public ParserException(int lineNo, String error) {
            super(error);
            this.lineNo = lineNo;
        }

        public int getLineNo() {
            return lineNo;
        }
    }
}
