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

import com.pwn9.filter.minecraft.api.MinecraftAPI;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stubbed out MinecraftAPI for our tests.
 * Created by Sage905 on 15-09-11.
 */
public class MockMinecraftAPI implements MinecraftAPI {

    // Simple flag to set what we want permission checks to return.
    public Boolean permReturnValue = false;
    private String executedCommand = "";
    private boolean muteStatus = false;
    private final List<MockPlayer> onlinePlayers = new ArrayList<>();

    public void addPlayer(MockPlayer player){
        onlinePlayers.add(player);
    }

    @Override
    public void reset() {
    }

    @Override
    public Boolean playerIdHasPermission(UUID u, String s) {
        return permReturnValue;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return null;
    }

    @Override
    public boolean burn(java.util.UUID uuid, int duration, String messageString) {
        return false;
    }

    @Override
    public void sendMessage(java.util.UUID uuid, String message) {
        for(MockPlayer player: onlinePlayers){
            if (player.getUniqueId().equals(uuid)){
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void sendMessages(java.util.UUID uuid, List<String> messages) {
        for(MockPlayer player: onlinePlayers){
            if (player.getUniqueId().equals(uuid)){
                messages.forEach(player::sendMessage);
            }
        }
    }

    @Override
    public void executePlayerCommand(java.util.UUID uuid, String command) {

    }

    @Override
    public boolean withdrawMoney(java.util.UUID uuid, Double amount, String messageString) {
        return false;
    }

    @Override
    public void kick(java.util.UUID uuid, String messageString) {

    }

    @Override
    public void kill(java.util.UUID uuid, String messageString) {

    }

    @Override
    public String getPlayerWorldName(java.util.UUID uuid) {
        return null;
    }

    @Override
    public void sendConsoleMessage(String message) {
        //todo
    }

    @Override
    public void sendConsoleMessage(TextComponent message) {
       //todo
    }

    @Override
    public void sendConsoleMessages(List<String> messageList) {
        //todo
    }

    @Override
    public void sendBroadcast(String message) {
        onlinePlayers.forEach(mockPlayer -> mockPlayer.sendMessage(message));
    }

    @Override
    public void sendBroadCast(TextComponent component) {
        onlinePlayers.forEach(mockPlayer -> mockPlayer.sendMessage(component));
    }

    @Override
    public void sendBroadcast(List<String> messageList) {
        onlinePlayers.forEach(mockPlayer ->  messageList.forEach(mockPlayer::sendMessage));
    }

    @Override
    public void executeCommand(String command) {
        executedCommand = command;
    }

    @Override
    public boolean globalMute() {
        return muteStatus;
    }

    @Override
    public void setMutStatus(boolean status) {
        this.muteStatus = status;
    }

    public String getExecutedCommand() {
        return executedCommand;
    }

    public AudienceProvider audiences(){
        return new MockAudienceProvider();
    }
}
