/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2014 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.handlers;

import com.pwn9.PwnFilter.DataCache;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.listener.PwnFilterSignListener;
import com.pwn9.PwnFilter.rules.RuleChain;
import com.pwn9.PwnFilter.rules.RuleManager;
import com.pwn9.PwnFilter.util.LogManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.logging.Logger;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertArrayEquals;

/**
 * Tests for the Sign Listener
 * User: ptoal
 * Date: 13-05-04
 * Time: 11:28 AM
 */

public class SignListenerTest {

    final String TESTRULECHAIN = "signTests.txt";
    private Block mockBlock;
    private Player mockPlayer;

    RuleManager ruleManager;
    RuleChain rs;
    PwnFilter mockPlugin = new PwnFilter();
    LogManager pwnLogger;
    PwnFilterSignListener signListener = new PwnFilterSignListener(mockPlugin);


    @Before
    public void setUp() throws Exception {
        ruleManager = RuleManager.getInstance();
        File testFile = new File(getClass().getResource("/" + TESTRULECHAIN).getFile());
        ruleManager.setRuleDir(testFile.getParent());
        rs = ruleManager.getRuleChain(TESTRULECHAIN);
        Logger logger = Logger.getAnonymousLogger();
        pwnLogger = LogManager.getInstance(logger, new File("/tmp/"));
        LogManager.setRuleLogLevel("INFO");
        DataCache.getInstance();
        rs.loadConfigFile();
        mockBlock = EasyMock.createMock(Block.class);
        mockPlayer = EasyMock.createMock(Player.class);
        expect(mockPlayer.hasPermission("pwnfilter.bypass.signs")).andReturn(false);
        expect(mockPlayer.isOnline()).andReturn(true);
        replay(mockPlayer);
        signListener.setRuleChain(rs);

    }

    @Test
    public void testSignListenerNoMatch() {
        String[] theLines = {"Test Line1", "Test Line2", "Test Line3", "Test Line4"};
        SignChangeEvent event = new SignChangeEvent(mockBlock,mockPlayer,theLines);
        signListener.onSignChange(event);
        assertArrayEquals(new String[]{"Test Line1", "Test Line2", "Test Line3", "Test Line4"}, event.getLines());
    }


    @Test
    public void testSignListenerReplacesText() {
        String[] theLines = {"Test Line1", "Changeme", "Test Line3", "Test Line4"};
        SignChangeEvent event = new SignChangeEvent(mockBlock,mockPlayer,theLines);
        signListener.onSignChange(event);
        assertArrayEquals(new String[]{"Test Line1", "Changed", "Test Line3", "Test Line4"}, event.getLines());
    }

    @Test
    public void testSignListenerTruncatesLine() {
        String[] theLines = {"Test Line1", "ReplaceLong", "Test Line3", "Test Line4"};
        SignChangeEvent event = new SignChangeEvent(mockBlock,mockPlayer,theLines);
        signListener.onSignChange(event);
        assertArrayEquals(new String[]{"Test Line1", "123456789012345", "Test Line3", "Test Line4"}, event.getLines());
    }

    @Test
    public void testSignColorsWork() {
        String[] theLines = {"&1Test Line1", "§2Test Line2", "&3Test Line3", "§4Test Line4"};
        SignChangeEvent event = new SignChangeEvent(mockBlock,mockPlayer,theLines);
        signListener.onSignChange(event);
        assertArrayEquals(new String[]{"&1Test Line1", "§2Test Line2", "&3Test Line3", "§4Test Line4"}, event.getLines());
    }

    @Test
    public void testSignColorsWorkAfterReplace() {
        String[] theLines = {"&1Test Line1", "§2Test Changeme", "&3Test Line3", "§4Test Line4"};
        SignChangeEvent event = new SignChangeEvent(mockBlock,mockPlayer,theLines);
        signListener.onSignChange(event);
        assertArrayEquals(new String[]{"§1Test Line1", "§2Test Changed", "§3Test Line3", "§4Test Line4"}, event.getLines());
    }

    @Test
    public void testSignDeletesExtraLines() {
        String[] theLines = {"d", "e", "r", "p"};
        SignChangeEvent event = new SignChangeEvent(mockBlock,mockPlayer,theLines);
        signListener.onSignChange(event);
        assertArrayEquals(new String[]{"foo","","",""}, event.getLines());
    }

    @After
    public void tearDown() throws Exception {
    }

}
