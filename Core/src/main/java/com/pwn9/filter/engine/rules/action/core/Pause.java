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

package com.pwn9.filter.engine.rules.action.core;

import com.pwn9.filter.engine.FilterContext;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.rules.action.InvalidActionException;

/**
 * ** DEBUGGING ONLY ***
 * <p>
 * This action is here only for debugging purposes.  You do NOT want to use it
 * on a production server.  It will just make your server hurt.  You have been
 * warned.
 * <p>
 * Causes the filter to wait for x milliseconds before continuing.
 *
 * @author Sage905
 * @version $Id: $Id
 */

class Pause implements Action {

    private final Long waitTime;

    private Pause(Long waitTime) {
        this.waitTime = waitTime;
    }

    static Action getAction(String s) throws InvalidActionException {
        long duration;
        try {
            duration = Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new InvalidActionException("'pause' action did not have a valid duration.");
        }
        return new Pause(duration);
    }

    @Override
    public void execute(final FilterContext filterTask, FilterService filterServiceImpl) {
        try {
            filterServiceImpl.getLogger().fine("Paws (" + waitTime + " milliseconds)");
            wait(waitTime);
            filterServiceImpl.getLogger().fine("Un-paws");

        } catch (InterruptedException ex) {
            filterServiceImpl.getLogger().info("Pause action was interrupted:" + ex.getMessage());
        }

    }
}
