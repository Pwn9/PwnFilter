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

/**
 * Test the Bukkit Built-in Chat Filter Listener
 *
 * This is more of a smoke test than a Unit test.  It's difficult to test the
 * listener without testing a lot of the other components upon which it depends.
 *
 * Created by Sage905 on 15-09-10.
 */

public class PwnFilterPlayerListenerTest {

//    @Mock
//    Player mockPlayer;
//
//    AsyncPlayerChatEvent chatEvent;
//    Configuration testConfig;
//    final File resourcesDir = new File(getClass().getResource("/config.yml").getFile()).getParentFile();
//    final PwnFilterPlayerListener playerListener = new PwnFilterPlayerListener();
//    final MinecraftAPI minecraftAPI = new TestMinecraftAPI();
//
//
//
//    @Before
//    public void setUp() {
//        RegisterActions.all();
//        FileLogger.getInstance(Logger.getAnonymousLogger(), new File("/pwnfiltertest.log"));
//        File rulesDir = new File(getClass().getResource("/rules").getFile());
//        FilterConfig.getInstance().setRulesDir(rulesDir);
//        testConfig = YamlConfiguration.loadConfiguration(new File(getClass().getResource("/config.yml").getFile()));
//        BukkitConfig.loadConfiguration(testConfig, resourcesDir);
//        MinecraftServer.setAPI(minecraftAPI);
//        BukkitConfig.setGlobalMute(false); // To ensure it gets reset between tests.
//    }
//
//    @Test
//    public void testBasicFunctionWorks() throws Exception {
//        RuleChain ruleChain = new RuleChain("blank.txt");
//        ruleChain.load();
//
//        String input = "Test Chat Message";
//        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<Player>());
//        playerListener.getCompiledChain(ruleChain);
//        playerListener.onPlayerChat(chatEvent);
//        assertEquals(chatEvent.getMessage(), input);
//    }
//
//    @Test
//    public void testExecutesRules() throws Exception {
//        RuleChain ruleChain = new RuleChain("replace.txt");
//        ruleChain.load();
//
//        String input = "Test replaceme Message";
//        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<Player>());
//        playerListener.getCompiledChain(ruleChain);
//        playerListener.onPlayerChat(chatEvent);
//        assertEquals(chatEvent.getMessage(), "Test PASS Message");
//    }
//
//    @Test
//    public void testGlobalMuteCancelsMessage() throws Exception {
//        RuleChain ruleChain = new RuleChain("blank.txt");
//        ruleChain.load();
//
//        String input = "Test Message";
//        BukkitConfig.setGlobalMute(true);
//        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<Player>());
//        playerListener.getCompiledChain(ruleChain);
//        playerListener.onPlayerChat(chatEvent);
//        assertTrue(chatEvent.isCancelled());
//    }
//
//    @Test
//    public void testDecolorMessage() throws Exception {
//        RuleChain ruleChain = new RuleChain("blank.txt");
//        ruleChain.load();
//
//        String input = "Test&4 Message";
//        testConfig.set("decolor", true);
//        BukkitConfig.loadConfiguration(testConfig, resourcesDir);
//
//        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<Player>());
//        playerListener.getCompiledChain(ruleChain);
//        playerListener.onPlayerChat(chatEvent);
//        assertEquals(chatEvent.getMessage(), "Test Message");
//        testConfig.set("decolor", false);
//    }
//
//    // https://github.com/Pwn9/PwnFilter/issues/13
//    @Test
//    public void testLowerMessage() throws Exception {
//        RuleChain ruleChain = new RuleChain("actionTests.txt");
//        ruleChain.load();
//
//        String input = "HEY! THIS SHOULD ALL GET LOWERED.";
//        BukkitConfig.loadConfiguration(testConfig, resourcesDir);
//
//        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<Player>());
//        playerListener.getCompiledChain(ruleChain);
//        playerListener.onPlayerChat(chatEvent);
//        assertTrue(!chatEvent.isCancelled());
//        assertEquals(chatEvent.getMessage(), "HEY! this should all get lowered.");
//    }



}