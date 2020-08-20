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

package com.pwn9.filter.bukkit.listener;

import com.pwn9.filter.MockPlayer;
import com.pwn9.filter.MockPlugin;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the Bukkit Built-in Chat Filter Listener
 * <p>
 * This is more of a smoke test than a Unit test.  It's difficult to test the
 * listener without testing a lot of the other components upon which it depends.
 * <p>
 * Created by Sage905 on 15-09-10.
 */

public class PwnFilterPlayerListenerTest {

    private final File resourcesDir = new File(getClass().getResource("/config.yml").getFile()).getParentFile();
    private final Player mockPlayer = new MockPlayer();
    private AsyncPlayerChatEvent chatEvent;
    private Configuration testConfig;
    private FilterService filterService;
    private PwnFilterPlayerListener playerListener;
    private MinecraftAPI api;

    @Before
    public void setUp() throws InvalidConfigurationException {
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        PwnFilterPlugin testPlugin = new MockPlugin();
        api = testPlugin.getApi();
        playerListener = new PwnFilterPlayerListener(testPlugin);
        filterService = testPlugin.getFilterService();
        filterService.getConfig().setRulesDir(rulesDir);
        testConfig = YamlConfiguration.loadConfiguration(new File(getClass().getResource("/config.yml").getFile()));
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        filterService.registerAuthorService(MockPlugin.getMockAuthorService());
        BukkitConfig.loadConfiguration(testConfig, resourcesDir, filterService);
        BukkitConfig.setGlobalMute(false); // To ensure it gets reset between tests.
    }

    @Test
    public void testBasicFunctionWorks() throws Exception {
        String input = "Test Chat Message";
        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("blank.txt");
        playerListener.onPlayerChat(chatEvent);
        assertEquals(chatEvent.getMessage(), input);
    }

    @Test
    public void testExecutesRules() throws Exception {
        String input = "Test replaceme Message";
        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("replace.txt");
        playerListener.onPlayerChat(chatEvent);
        assertEquals(chatEvent.getMessage(), "Test PASS Message");
    }

    @Test
    public void testGlobalMuteCancelsMessage() throws Exception {
        String input = "Test Message";
        api.setMutStatus(true);
        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("blank.txt");
        playerListener.onPlayerChat(chatEvent);
        assertTrue(chatEvent.isCancelled());
    }

    @Test
    public void testDecolorMessage() throws Exception {
        String input = "Test&4 Message";
        testConfig.set("decolor", true);
        BukkitConfig.loadConfiguration(testConfig, resourcesDir, filterService);

        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("blank.txt");
        playerListener.onPlayerChat(chatEvent);
        assertEquals(chatEvent.getMessage(), "Test Message");
        testConfig.set("decolor", false);
    }

    // https://github.com/Pwn9/PwnFilter/issues/13
    @Test
    public void testLowerMessage() throws Exception {
        String input = "HEY! THIS SHOULD ALL GET LOWERED.";
        BukkitConfig.loadConfiguration(testConfig, resourcesDir, filterService);

        chatEvent = new AsyncPlayerChatEvent(true, mockPlayer, input, new HashSet<>());
        playerListener.loadRuleChain("actionTests.txt");
        playerListener.onPlayerChat(chatEvent);
        assertFalse(chatEvent.isCancelled());
        assertEquals(chatEvent.getMessage(), "HEY! this should all get lowered.");
    }

}