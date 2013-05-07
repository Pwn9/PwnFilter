package com.pwn9.PwnFilter.rules;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


class Condition {


    public enum CondFlag {
        NONE, ignore, require
    }

    public enum CondType {
        permission, user, string,
    }

    final CondType type;
    final CondFlag flag;
    final String parameters;


    public Condition(CondType t, CondFlag f, String p) {
        type = t;
        flag = f;
        parameters = p;
    }

    public static Condition newCondition(String command, String parameterString) {
        String subCmd;
        CondType newType;
        CondFlag newFlag;
        String newParameters;

        try {
            newFlag = CondFlag.valueOf(command);
        } catch (IllegalArgumentException e) {
            return null;
        }

        String[] parts = parameterString.split("\\s", 2);
        subCmd = parts[0].toLowerCase();
        try {
            newType = Condition.CondType.valueOf(subCmd);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (parts.length > 1) {
            newParameters = parts[1];
        } else {
            newParameters = "";
        }

        return new Condition(newType, newFlag, newParameters);
    }

    public static boolean isCondition(String command) {
        try {
            if (CondFlag.valueOf(command) != CondFlag.NONE ) {
                return true;
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
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

