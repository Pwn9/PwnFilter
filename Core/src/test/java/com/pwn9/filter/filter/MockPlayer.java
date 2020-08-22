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
 */

package com.pwn9.filter.filter;


import com.pwn9.filter.engine.api.CommandSender;
import com.pwn9.filter.engine.api.NotifyTarget;
import com.pwn9.filter.engine.api.Player;
import com.pwn9.filter.engine.rules.action.targeted.BurnTarget;
import com.pwn9.filter.engine.rules.action.targeted.KickTarget;
import com.pwn9.filter.engine.rules.action.targeted.KillTarget;
import com.pwn9.filter.minecraft.api.MinecraftAPI;

public class MockPlayer extends PwnPlayer implements Player, CommandSender, NotifyTarget, BurnTarget, KickTarget, KillTarget {

    public MockPlayer(MinecraftAPI api) {
        super(api);
    }

    @Override
    public MinecraftAPI getMineCraftApi() {
        return null;
    }

    @Override
    public void executeCommand(String cmd) {

    }

    @Override
    public void notifyWithPerm(String permissionString, String sendString) {

    }

    @Override
    public boolean burn(int duration, String message) {
        return false;
    }

    @Override
    public void kick(String message) {

    }

    @Override
    public void kill(String message) {

    }
}
