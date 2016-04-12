package com.pwn9.filter.util;

import com.pwn9.filter.bukkit.TestTicker;
import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Tests the LimitedRegexCharSequence
 * User: Sage905
 * Date: 13-09-13
 * Time: 8:19 PM
 */

public class LimitedRegexCharSequenceTest {

    private final static String simpleString = "This is a pretty simple test string.";


    @Test
    public void testCharAt() throws Exception {
        LimitedRegexCharSequence lrcs = new LimitedRegexCharSequence(simpleString,1000);
        Assert.assertEquals(lrcs.charAt(18),'i');
        Assert.assertEquals(lrcs.charAt(3),'s');
    }


    @Test
    public void testAccessDoesNotTimeout() {
        TestTicker ticker = new TestTicker(); // This ticker doesn't Tick. :)
        LimitedRegexCharSequence lrcs = new LimitedRegexCharSequence(simpleString, 1, ticker);
        // This should not blow up.
        int i;
        for (i = 0; i < 20; i++) {
            lrcs.charAt(i);
        }
        assertEquals(i, lrcs.getAccessCount());
    }

    @Test(expected = LimitedRegexCharSequence.RegexTimeoutException.class)
    public void testAccessDoesTimeout() {
        TestTicker ticker = new TestTicker();
        LimitedRegexCharSequence lrcs = new LimitedRegexCharSequence(simpleString, 100, ticker);

        ticker.setElapsed(TimeUnit.NANOSECONDS.convert(101,TimeUnit.MILLISECONDS));
        lrcs.charAt(0); // This should throw RegexTimeoutException
    }

    @Test(expected = LimitedRegexCharSequence.RegexTimeoutException.class)
    public void testTimeout() throws Exception {
        // demonstrates behavior for regular expression running into catastrophic backtracking for given input
        LimitedRegexCharSequence timedString = new LimitedRegexCharSequence("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",100);
        Pattern pattern = Pattern.compile("(x+x+)+y");
        Matcher matcher = pattern.matcher(timedString);
        matcher.matches(); // This should throw the RegexTimeoutException
    }
}
