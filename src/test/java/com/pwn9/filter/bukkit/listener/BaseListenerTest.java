/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2016 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.rules.TestStatsTracker;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by Sage905 on 2016-04-01.
 */
public class BaseListenerTest {

    BaseListener test;
    FilterService filterService = new FilterService(new TestStatsTracker());

    @Before
    public void setup() {
        test = new BaseListener(filterService) {
            @Override
            public String getShortName() {
                return null;
            }

            @Override
            public void activate() {

            }
        };

    }
    @Test
    public void getCompiledChainChecksCorrectPath() throws Exception {

        try {
            test.getCompiledChain("/rules/blank.txt");
            Assert.fail("getCompiledChain throws exception on invalid file.");
        } catch (InvalidChainException ex) {
            Assert.assertTrue("Rule File not found when path invalid"
                    ,ex.getMessage().endsWith("Rule File not found: /rules/blank.txt"));
        }

        System.out.println(getClass().getResource("/").getFile());
        filterService.getConfig().setRulesDir(new File(getClass().getResource("/").getFile()));
        Assert.assertNotNull("Rulechain generated from valid path.",
                test.getCompiledChain("rules/blank.txt"));


    }
}