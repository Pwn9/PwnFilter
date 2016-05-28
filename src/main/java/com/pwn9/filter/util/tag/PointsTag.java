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

package com.pwn9.filter.util.tag;

import com.pwn9.filter.engine.PointManager;
import com.pwn9.filter.engine.api.FilterContext;

import java.text.DecimalFormat;

/**
 * Return the current points level a player has, if the PointManager is enabled.
 * <p>
 * Created by Sage905 on 15-09-04.
 */
class PointsTag implements Tag {

    private static final DecimalFormat df = new DecimalFormat("0.00##");

    @Override
    public String getValue(FilterContext filterTask) {
        PointManager pointManager = filterTask.getFilterClient().getFilterService().getPointManager();
        return (pointManager.isEnabled()) ?
                df.format(pointManager.getPoints(filterTask.getAuthor())) :
                "-";
    }
}
