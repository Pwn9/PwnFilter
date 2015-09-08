/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.rules.action.core.*;
import com.pwn9.PwnFilter.rules.action.minecraft.*;
import com.pwn9.PwnFilter.rules.action.targeted.*;

/**
 * Routines to register all PwnFilter "builtin" actions.
 *
 * Created by ptoal on 15-09-05.
 */
public class RegisterActions {

    public static void all() {
        builtin();
        minecraft();
        targeted();
    }

    public static void builtin() {
        ActionFactory.add("abort", Abort.class);
        ActionFactory.add("deny", Deny.class);
        ActionFactory.add("log", Log.class);
        ActionFactory.add("lower", Lower.class);
        ActionFactory.add("points", Points.class);
        ActionFactory.add("randrep", RandomReplace.class);
        ActionFactory.add("replace", Replace.class);
        ActionFactory.add("rewrite", Rewrite.class);
        ActionFactory.add("upper", Upper.class);
    }

    public static void minecraft() {
        ActionFactory.add("command", Command.class);
        ActionFactory.add("cmdchain", CommandChain.class);
        ActionFactory.add("console", Console.class);
        ActionFactory.add("conchain", ConsoleChain.class);
    }

    public static void targeted() {
        ActionFactory.add("broadcast", Broadcast.class);
        ActionFactory.add("broadcastfile", BroadcastFile.class);
        ActionFactory.add("burn", Burn.class);
        ActionFactory.add("fine", Fine.class);
        ActionFactory.add("kick", Kick.class);
        ActionFactory.add("kill", Kill.class);
        ActionFactory.add("notify", Notify.class);
        ActionFactory.add("respond", Respond.class);
        ActionFactory.add("respondfile", RespondFile.class);
        ActionFactory.add("warn", Warn.class);
    }

}
