package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.util.ColoredString;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Filterstate
 *
 * An object to keep track of the state of a filter event through execution
 * of the rules.
 * <p>
 *
 * In addition to the public methods, it is possible to modify state by writing directly
 * to the following attributes:
 *
 * log: If true, actions will be logged to the file/console
 * stop: If true, no further rules will be processed
 * cancel: If true, this event will be set Cancelled (if possible)
 *
 */
public class FilterState {
    private final ColoredString originalMessage; // Original message
    public final PwnFilter plugin; // Which plugin is this state attached to?
    public final Player player; // Player that this event is connected to.
    public final String playerName,playerWorldName;
    public final PwnFilter.EventType eventType;
    public ColoredString message; // Modified message string
    final int messageLen; // New message can't be longer than original.
    private List<String> logMessages = new ArrayList<String>(); // Rules can add strings to this array.  They will be output to log if log=true
    public boolean log = false;  // If true, actions will be logged
    public boolean stop = false; // If set true by a rule, will stop further processing.
    public boolean cancel = false; // If set true, will cancel this event.
    public Pattern pattern; // Pattern that we currently matched.

    // NOTE: pattern should always match originalMessage, but may not match
    // the new message, if another rule has modified it.

    /**
     * Class Constructor with the text string to act upon.  Colour codes must
     * already be converted to the section character (u00A7), otherwise they
     * will not be correctly processed.
     *
     * @param m The original text string to run rules against.
     */
    public FilterState(PwnFilter pl, String m, Player p, PwnFilter.EventType et) {
        originalMessage = new ColoredString(m);
        message = new ColoredString(m);
        messageLen = originalMessage.length();
        player = p;
        playerName = PwnFilter.dataCache.getPlayerName(p);
        playerWorldName = PwnFilter.dataCache.getPlayerWorld(p);
        plugin = pl;
        eventType = et;
    }

    /**
     * Add a string to the list of log messages that will be written to the logfile /console.
     * These messages will only be output if the rule has the "then log" action, or if debug: true
     * in the comfig.yml
     * @param message A string containing the log message to be output.
     */
    public void addLogMessage (String message) {
        logMessages.add(message);
    }

    public List<String> getLogMessages() {
        return logMessages;
    }
    /**
     * @return true if the modified message is different than the original.
     */
    public boolean messageChanged() {
        return !originalMessage.toString().equals(message.toString());
    }

    public boolean playerHasPermission(String perm) {
        return PwnFilter.dataCache.hasPermission(player,perm);
    }
    /**
     *
     * @return a new Instance of ColouredString with a copy of the originalMessage.
     */
    public ColoredString getOriginalMessage() {
        return new ColoredString(originalMessage);
    }
}
