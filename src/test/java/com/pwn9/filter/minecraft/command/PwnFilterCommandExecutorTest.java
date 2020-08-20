package com.pwn9.filter.minecraft.command;

import com.pwn9.filter.MockMinecraftAPI;
import com.pwn9.filter.MockPlayer;
import com.pwn9.filter.MockPlugin;
import com.pwn9.filter.bukkit.PwnFilterPlugin;
import org.junit.Before;
import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.assertEquals;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 20/08/2020.
 */
public class PwnFilterCommandExecutorTest {
    private final PwnFilterPlugin testPlugin = new MockPlugin();
    private final MockPlayer testPlayer1 = new MockPlayer();
    private final MockPlayer testPlayer2 = new MockPlayer();


    @Before
    public void setup(){
        MockMinecraftAPI api = (MockMinecraftAPI) testPlugin.getApi();
        api.addPlayer(testPlayer1);
        api.addPlayer(testPlayer2);

    }

    @Test
    public void pwnClearScreenTest(){
        PwnFilterCommandExecutor command = new PwnClearScreen(testPlugin);
        command.onCommand(testPlayer1,"","","","");
        Queue<String> forOne = testPlayer1.getAllMessages();
        Queue<String> forTwo = testPlayer2.getAllMessages();
        assertEquals(2,forOne.size());
        assertEquals(1,forTwo.size());
    }

}