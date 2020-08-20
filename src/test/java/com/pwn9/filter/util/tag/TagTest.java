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

import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.TestAuthor;
import com.pwn9.filter.engine.rules.TestClient;
import com.pwn9.filter.minecraft.tag.PlayerTag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Tags
 * Created by Sage905 on 15-09-07.
 */
public class TagTest {
    private FilterClient testClient;
    private MessageAuthor testAuthor;

    @Before
    public void setUp() throws Exception {
        testClient = new TestClient();
        testAuthor = new TestAuthor();
    }

    @Test
    public void testBuiltinTags() throws Exception {
        String input = "Test %player% tag";
        FilterContext testState = new FilterContext(input, testAuthor, testClient);
        TagRegistry.addTag("player", new PlayerTag());
        Assert.assertEquals(TagRegistry.replaceTags(input, testState), "Test Sage905 tag");

    }
}