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

package com.pwn9.filter;

import com.avaje.ebean.EbeanServer;
import com.google.common.collect.MapMaker;
import com.pwn9.filter.bukkit.PwnFilterBukkitPlugin;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.AuthorService;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.TestAuthor;
import com.pwn9.filter.minecraft.api.MinecraftAPI;
import com.pwn9.filter.minecraft.api.MinecraftConsole;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class MockPlugin implements PwnFilterPlugin, Plugin {

    public static final ConcurrentMap<UUID, String> lastMessage = new MapMaker().concurrencyLevel(2).weakKeys().makeMap();
    private final FilterService filterService = new FilterService();
    private final MinecraftAPI minecraftAPI = new MockMinecraftAPI();
    private final static AuthorService authorService = new AuthorService() {
        final TestAuthor author = new TestAuthor();
        @Override
        public MessageAuthor getAuthorById(UUID uuid) {
            return author;
        }

        @Override
        public com.pwn9.filter.engine.api.CommandSender getSenderById(UUID uuid) {
            return author;
        }
    };

    @Override
    public FilterService getFilterService() {
        return filterService;
    }

    @Override
    public MinecraftConsole getConsole() {
        return new MinecraftConsole(minecraftAPI);
    }

    public static AuthorService getMockAuthorService() {
        return authorService;
    }
    @Override
    public Logger getLogger() {
        return Logger.getAnonymousLogger();
    }

    @Override
    public MinecraftAPI getApi() {
        return minecraftAPI;
    }

    @Override
    public boolean configurePlugin() {
        return true;
    }

    @Override
    public boolean checkRecentMessage(UUID uuid, String string) {
        return (lastMessage.containsKey(uuid) && PwnFilterBukkitPlugin.lastMessage.get(uuid).equals(string));

    }

    @Override
    public void addRecentMessage(UUID uuid, String string) {
        lastMessage.put(uuid,string);
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return null;
    }

    @Override
    public FileConfiguration getConfig() {
        return null;
    }

    @Override
    public InputStream getResource(String filename) {
        return null;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public PluginLoader getPluginLoader() {
        return null;
    }

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean canNag) {

    }

    @Override
    public EbeanServer getDatabase() {
        return null;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
