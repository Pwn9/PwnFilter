package com.pwn9.PwnFilter.rules.action;

/**
 * This factory returns an action object selected by the rules file.
 * eg: "then kick" would return the Actionkick object.
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class ActionFactory {

    public static Action getActionFromString(String s)
    {
        String[] parts = s.split("\\s",2);
        String actionName = parts[0];
        String actionData = "";
        if (parts.length > 1) {
            actionData = parts[1];
        }
        return getAction(actionName, actionData);
    }

    public static Action getAction (final String actionName, final String actionData)
    {
        // Return a subclass instance based on actionName.
        try {
            Action newAction;
            String className = "com.pwn9.PwnFilter.rules.action.Action" + actionName;
            newAction = (Action)(Class.forName(className).newInstance());
            newAction.init(actionData);
            return newAction;
        } catch ( ClassNotFoundException ex ) {
            return null;
        } catch ( InstantiationException ex ) {
            return null;
        } catch ( IllegalAccessException ex) {
            return null;
        }
    }
}

