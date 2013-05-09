package com.pwn9.PwnFilter;

import com.pwn9.PwnFilter.rules.RuleSet;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.StringReader;
import java.util.logging.Logger;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replayAll;

/**
 * Created with IntelliJ IDEA.
 * User: ptoal
 * Date: 13-05-08
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(PowerMockRunner.class)
public class HandlerTest {
    Logger logger;
    RuleSet ruleSet;
    PwnFilter plugin = new PwnFilter();
    Player player;

    final String rules =
      "match foo\n"
    + "then replace bar\n";


    public HandlerTest() {
        logger = Logger.getLogger("Test");
        ruleSet = new RuleSet(plugin);
        ruleSet.loadRules(new StringReader(rules));
        player = PowerMock.createMock(Player.class);

    }

    @Before
    public void InitTest() {

    }

    @Test
    public void testCommandHandler() {
        PlayerCommandPreprocessEvent event = PowerMock.createMock(PlayerCommandPreprocessEvent.class);
        expect(event.getMessage()).andReturn("me is foo right now.");
        expect(event.getPlayer()).andReturn(player);
        replayAll();


    }
}
