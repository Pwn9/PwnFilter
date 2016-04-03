/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.util.tag;

import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.FilterContext;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.TestAuthor;
import com.pwn9.filter.engine.rules.TestClient;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for TagRegistry
 * Created by Sage905 on 15-09-07.
 */
public class TagRegistryTest {

    MessageAuthor testAuthor;
    FilterClient testClient;


    @Before
    public void setUp() {
        testAuthor = new TestAuthor();
        testClient = new TestClient();

    }

    @Test
    public void testUntaggedStringIsUnmodified() throws Exception {

        FilterContext testState = new FilterContext("TestString", testAuthor, testClient);

        String input = "This is a test";
        String result = TagRegistry.replaceTags(input, testState);
        Assert.assertEquals(input, result);
    }

    @Test
    public void testUnmatchedTagIsUnmodified() throws Exception {
        FilterContext testState = new FilterContext("TestString", testAuthor, testClient);

        String input = "This is a %nonexistenttag%";
        String result = TagRegistry.replaceTags(input, testState);
        Assert.assertEquals(input, result);
    }

    @Test
    public void testStaticTagIsReplaced() throws Exception {
        FilterContext testState = new FilterContext("TestString", testAuthor, testClient);

        TagRegistry.addTag("test", new Tag() {
            @Override
            public String getValue(FilterContext filterTask) {
                return "foo";
            }
        });

        String input = "This is a %test%";
        String result = TagRegistry.replaceTags(input, testState);
        Assert.assertEquals("This is a foo", result);

    }
}