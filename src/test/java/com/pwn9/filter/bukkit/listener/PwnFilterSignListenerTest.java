/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.TestMinecraftAPI;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.config.FilterConfig;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import com.pwn9.filter.minecraft.api.MinecraftServer;
import com.pwn9.filter.engine.rules.chain.RuleChain;
import com.pwn9.filter.engine.rules.action.RegisterActions;
import com.pwn9.filter.util.FileLogger;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;

/**
 * Test the Bukkit Built-in Chat Filter Listener
 *
 * This is more of a smoke test than a Unit test.  It's difficult to test the
 * listener without testing a lot of the other components upon which it depends.
 *
 * Created by Sage905 on 15-09-10.
 */

@RunWith(EasyMockRunner.class)
public class PwnFilterSignListenerTest {

    @Mock
    Player mockPlayer;

    @Mock
    Block mockBlock;

    SignChangeEvent signChangeEvent;
    Configuration testConfig;
    final File resourcesDir = new File(getClass().getResource("/config.yml").getFile()).getParentFile();
    final PwnFilterSignListener signListener = new PwnFilterSignListener();
    final MinecraftAPI minecraftAPI = new TestMinecraftAPI();


    @Before
    public void setUp() {
        RegisterActions.builtin();
        FileLogger.getInstance(Logger.getAnonymousLogger(), new File("/pwnfiltertest.log"));
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        FilterConfig.getInstance().setRulesDir(rulesDir);
        testConfig = YamlConfiguration.loadConfiguration(new File(getClass().getResource("/config.yml").getFile()));
        BukkitConfig.loadConfiguration(testConfig, resourcesDir);
        MinecraftServer.setAPI(minecraftAPI);
        BukkitConfig.setGlobalMute(false); // To ensure it gets reset between tests.
    }

    @Test
    public void testBasicFunctionWorks() throws Exception {
        RuleChain ruleChain = new RuleChain("blank.txt");
        ruleChain.load();

        final String[] input = new String[]{"Test", "chat", "message", ""};

        signChangeEvent = new SignChangeEvent(mockBlock, mockPlayer,
                input);

        signListener.getCompiledChain(ruleChain);
        signListener.onSignChange(signChangeEvent);

        for (int i=0 ; i < 4 ; i++) {
            assertEquals(input[i],signChangeEvent.getLine(i));
        }

    }

    @Test
    public void testOneLineReplacement() throws Exception {
        RuleChain ruleChain = new RuleChain("replace.txt");
        ruleChain.load();

        String[] input = new String[]{"replaceme", "test", "message", ""};
        String[] output = new String[]{"PASS", "test", "message", ""};

        signChangeEvent = new SignChangeEvent(mockBlock, mockPlayer,
                input.clone());

        signListener.getCompiledChain(ruleChain);
        signListener.onSignChange(signChangeEvent);

        String[] changedLines = signChangeEvent.getLines();
        for (int i=0 ; i < 4 ; i++) {
            assertEquals(output[i],changedLines[i]);
        }

    }

}