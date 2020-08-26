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

/**
 * Deny this event from propagating by cancelling it (i.e.: throw the message
 * in the trash)
 * <p>
 * Deny is a singleton, because it does not have any parameters and always
 * performs the same action on a FilterContext ( calls setCancelled())
 *
 * @author Sage905
 * @version $Id: $Id
 */
public enum Deny implements Action {

    INSTANCE;

    @Override
    public void execute(final FilterContext filterTask, FilterService filterServiceImpl) {
        filterTask.setCancelled();
    }
}
