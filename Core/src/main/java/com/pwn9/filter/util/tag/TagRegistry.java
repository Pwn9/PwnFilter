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

import com.pwn9.filter.engine.FilterContext;
import com.pwn9.filter.engine.api.FilterContextImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The TagRegistry is used to manage the currently active %tag% replacements.
 * These Tags are used during the substitution of strings, for example, with
 * a player named "Joe", and the following action in a rule file:
 * then broadcast %player% has just pooped their self
 * every time this rule matches, a broadcast message will be sent which reads:
 * Joe has just pooped their self
 * <p>
 * Created by Sage905 on 15-09-04.
 */
public class TagRegistry {

    private static final HashMap<String, Tag> tagMap
            = new HashMap<>();

    static void addTag(String name, Tag tag) {
        if (tagMap.containsKey(name)) {
            if (tagMap.get(name).getClass() != tag.getClass()) {
                throw new RuntimeException("Duplicate Tag value.");
            } else return;
        }
        tagMap.put(name, tag);
    }

    @Nullable
    private static Tag getTag(String name) {
        return tagMap.get(name);
    }

    /**
     * <p>replaceTags.</p>
     *
     * @param line       a {@link StringTag} object.
     * @param filterTask a {@link FilterContextImpl} object.
     * @return a {@link StringTag} object.
     */
    public static String replaceTags(String line, FilterContext filterTask) {

        Matcher tagMatcher = Pattern.compile("%(\\w+)%", Pattern.CASE_INSENSITIVE).matcher(line);

        boolean result = tagMatcher.find();

        if (!result) return line;

        StringBuffer sb = new StringBuffer();
        do {
            Tag tag = getTag(tagMatcher.group(1));

            if (tag != null) {
                tagMatcher.appendReplacement(sb, wrapReplacement(tag.getValue(filterTask)));
            } else {
                tagMatcher.appendReplacement(sb, tagMatcher.group());
            }
            result = tagMatcher.find();
        } while (result);
        tagMatcher.appendTail(sb);
        return sb.toString();

    }

    private static String wrapReplacement(String s) {
        // Wrap the replacement text, so it isn't processed as regex.  Also,
        // Replace null values with a '-'.
        return (s != null) ? Matcher.quoteReplacement(s) : "-";
    }
}
