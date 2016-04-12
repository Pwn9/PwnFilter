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

import com.pwn9.filter.MockPlayer;
import com.pwn9.filter.MockPlugin;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.rules.TestAuthor;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Test the Bukkit Built-in Chat Filter Listener
 *
 * This is more of a smoke test than a Unit test.  It's difficult to test the
 * listener without testing a lot of the other components upon which it depends.
 *
 * Created by Sage905 on 15-09-10.
 */

public class PwnFilterSignListenerTest {

    private Player mockPlayer = new MockPlayer();
    private Block mockBlock;

    private SignChangeEvent signChangeEvent;
    private Configuration testConfig;
    private final File resourcesDir = new File(getClass().getResource("/config.yml").getFile()).getParentFile();
    private PwnFilterPlugin testPlugin;
    private FilterService filterService;
    private PwnFilterSignListener signListener;


    @Before
    public void setUp() throws InvalidConfigurationException {
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        testPlugin = new MockPlugin();
        signListener = new PwnFilterSignListener(testPlugin);
        filterService = testPlugin.getFilterService();
        filterService.getConfig().setRulesDir(rulesDir);
        testConfig = YamlConfiguration.loadConfiguration(new File(getClass().getResource("/config.yml").getFile()));
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        filterService.registerAuthorService(uuid -> new TestAuthor());
        BukkitConfig.loadConfiguration(testConfig, resourcesDir,filterService);
        BukkitConfig.setGlobalMute(false); // To ensure it gets reset between tests.
    }

    @Test
    public void testBasicFunctionWorks() throws Exception {
        final String[] input = new String[]{"Test", "chat", "message", ""};

        signChangeEvent = new SignChangeEvent(mockBlock, mockPlayer,
                input);

        signListener.loadRuleChain("blank.txt");
        signListener.onSignChange(signChangeEvent);

        for (int i=0 ; i < 4 ; i++) {
            assertEquals(input[i],signChangeEvent.getLine(i));
        }

    }

    @Test
    public void testOneLineReplacement() throws Exception {
        String[] input = new String[]{"replaceme", "test", "message", ""};
        String[] output = new String[]{"PASS", "test", "message", ""};

        signChangeEvent = new SignChangeEvent(mockBlock, mockPlayer,
                input.clone());

        signListener.loadRuleChain("replace.txt");
        signListener.onSignChange(signChangeEvent);

        String[] changedLines = signChangeEvent.getLines();
        for (int i=0 ; i < 4 ; i++) {
            assertEquals(output[i],changedLines[i]);
        }

    }

}