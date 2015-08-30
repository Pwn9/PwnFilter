package com.pwn9.PwnFilter.command;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.command.Command;

/**
 * SubCommand adds the plugin to the instance of Command.
 * User: ptoal
 * Date: 13-08-16
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 *
 * @author ptoal
 * @version $Id: $Id
 */
public abstract class SubCommand extends Command {

    protected final PwnFilter plugin;

    /**
     * <p>Constructor for SubCommand.</p>
     *
     * @param plugin a {@link com.pwn9.PwnFilter.PwnFilter} object.
     * @param name a {@link java.lang.String} object.
     */
    public SubCommand(PwnFilter plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    /**
     * <p>getHelpMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHelpMessage() {
        String message = "";
        if (!getUsage().isEmpty()) message = getUsage();
        if (!getDescription().isEmpty()) {
            if (!message.isEmpty()) {
                message = message + " -- " + getDescription();
            } else {
                message = getDescription();
            }
        }
        return message;
    }
}
