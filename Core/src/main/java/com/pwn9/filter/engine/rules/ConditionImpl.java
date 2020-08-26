/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.engine.rules;

import com.pwn9.filter.engine.FilterContext;

/**
 * <p>Condition class.</p>
 *
 * @author Sage905
 * @version $Id: $Id
 */
public class ConditionImpl implements Condition {


    private final CondType type;
    private final CondFlag flag;

    @Override
    public String getParameters() {
        return parameters;
    }

    private final String parameters;
    /**
     * <p>Constructor for Condition.</p>
     *
     * @param t a {@link ConditionImpl.CondType} object.
     * @param f a {@link ConditionImpl.CondFlag} object.
     * @param p a {@link java.lang.String} object.
     */
    private ConditionImpl(CondType t, CondFlag f, String p) {
        type = t;
        flag = f;
        parameters = p;
    }

    /**
     * <p>newCondition.</p>
     *
     * @param line a {@link java.lang.String} object.
     * @return a {@link ConditionImpl} object.
     */
    public static Condition newCondition(String line) {
        String[] parts = line.split("\\s", 2);
        String conditionName = parts[0];
        String conditionData;
        conditionData = ((parts.length > 1) ? parts[1] : "");

        return newCondition(conditionName, conditionData);
    }

    /**
     * <p>newCondition.</p>
     *
     * @param command         a {@link java.lang.String} object.
     * @param parameterString a {@link java.lang.String} object.
     * @return a {@link ConditionImpl} object.
     */
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
            newType = ConditionImpl.CondType.valueOf(subCmd);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (parts.length > 1) {
            newParameters = parts[1];
        } else {
            newParameters = "";
        }

        return new ConditionImpl(newType, newFlag, newParameters);
    }

    /**
     * <p>isCondition.</p>
     *
     * @param command a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean isCondition(String command) {
        try {
            return CondFlag.valueOf(command) != CondFlag.NONE;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks a message against this condition.  This method returns true if
     * the condition is met, false otherwise.  Processing of the current rule
     * will be aborted if _any_ check returns false.
     *
     * @param state The MessageState
     * @return true if this condition is met, false otherwise
     */
    public boolean check(FilterContext state) {
        boolean matched = false;
        switch (type) {
            case user:
                for (String check : parameters.split("\\s")) {
                    if (state.getAuthor().getName().equalsIgnoreCase(check))
                        matched = true;
                }
            case permission:
                for (String check : parameters.split("\\s")) {
                    if (state.getAuthor().hasPermission(check)) matched = true;
                }
            case string:
                for (String check : parameters.split("\\|")) {
                    if (state.getOriginalMessage().toString().toUpperCase().contains(check.toUpperCase()))
                        matched = true;
                }
            case command:
                for (String check : parameters.split("\\|")) {
                    if (state.getFilterClient().getShortName().equals("COMMAND")) {
                        String command = state.getOriginalMessage().toString().split("\\s")[0].replaceFirst("^\\/", "");
                        if (command.toUpperCase().matches(check.toUpperCase()))
                            matched = true;
                    }
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

    @Override
    public Condition.CondType getType() {
        return type;
    }

    @Override
    public CondFlag getFlag() {
        return flag;
    }
}

