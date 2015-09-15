package com.pwn9.filter.minecraft.command;

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
abstract class SubCommand extends Command {

    /**
     * <p>Constructor for SubCommand.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public SubCommand(String name) {
        super(name);
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
