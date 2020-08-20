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
import com.pwn9.filter.bukkit.MockBook;
import com.pwn9.filter.bukkit.MockServer;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.bukkit.config.BukkitConfig;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.rules.action.minecraft.MinecraftAction;
import com.pwn9.filter.engine.rules.action.targeted.TargetedAction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Test the Bukkit Built-in Chat Filter Listener
 * <p>
 * This is more of a smoke test than a Unit test.  It's difficult to test the
 * listener without testing a lot of the other components upon which it depends.
 * <p>
 * Created by Sage905 on 15-09-10.
 */

public class PwnFilterBookListenerTest {

    private final File resourcesDir = new File(getClass().getResource("/config.yml").getFile()).getParentFile();
    private final String[] testPages = new String[]{
            "This is the first page",
            "This is the second page",
            "This is the third page",
            "This is the final page"};
    private final Player mockPlayer = new MockPlayer();
    private PlayerEditBookEvent event;
    private PwnFilterBookListener bookListener;

    @Before
    public void setUp() throws InvalidConfigurationException {
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(new MockServer());
        }
        File rulesDir = new File(getClass().getResource("/rules").getFile());
        PwnFilterPlugin testPlugin = new MockPlugin();
        bookListener = new PwnFilterBookListener(testPlugin);
        FilterService filterService = testPlugin.getFilterService();
        filterService.getConfig().setRulesDir(rulesDir);
        Configuration testConfig = YamlConfiguration.loadConfiguration(new File(getClass().getResource("/config.yml").getFile()));
        filterService.getActionFactory().addActionTokens(MinecraftAction.class);
        filterService.getActionFactory().addActionTokens(TargetedAction.class);
        filterService.registerAuthorService(MockPlugin.getMockAuthorService());
        BukkitConfig.loadConfiguration(testConfig, resourcesDir, filterService);
        BukkitConfig.setGlobalMute(false); // To ensure it gets reset between tests.
    }

    @Test
    public void testBasicFunctionWorks() throws Exception {
        final BookMeta bookMeta = new MockBook("TestTitle", mockPlayer.getName());
        bookMeta.setPages(testPages);

        event = new PlayerEditBookEvent(mockPlayer, 1, new MockBook("", ""), bookMeta, false);

        bookListener.loadRuleChain("blank.txt");
        bookListener.onBookEdit(event);

        for (int i = 0; i < bookMeta.getPageCount() - 1; i++) {
            assertEquals(testPages[i], event.getNewBookMeta().getPage(i));
        }

    }

    @Test
    public void testBasicReplacementWorks() throws Exception {
        final BookMeta oldBookMeta = new MockBook("TestTitle", mockPlayer.getName());
        oldBookMeta.setPages(testPages);
        String testPage = "This test should replaceme";
        oldBookMeta.addPage(testPage);

        event = new PlayerEditBookEvent(mockPlayer, 1, new MockBook("", ""), oldBookMeta, false);

        bookListener.loadRuleChain("replace.txt");
        bookListener.onBookEdit(event);

        int pageCount = oldBookMeta.getPageCount();
        BookMeta newBookMeta = event.getNewBookMeta();
        for (int i = 0; i < pageCount - 2; i++) {
            assertEquals(testPages[i], event.getNewBookMeta().getPage(i));
        }
        assertEquals("This test should PASS", newBookMeta.getPage(pageCount - 1));

    }


}