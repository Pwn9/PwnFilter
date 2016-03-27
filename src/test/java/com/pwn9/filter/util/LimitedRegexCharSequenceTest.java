package com.pwn9.filter.util;

import junit.framework.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void testTimeout() throws Exception {
        // demonstrates behavior for regular expression running into catastrophic backtracking for given input
        LimitedRegexCharSequence timedString = new LimitedRegexCharSequence("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",1000);
        Pattern pattern = Pattern.compile("(x+x+)+y");
        Matcher matcher = pattern.matcher(timedString);

        try {
            //noinspection ResultOfMethodCallIgnored
            matcher.matches();
            Assert.fail("Shouldn't get here!");
        } catch (RuntimeException ex) {
//            System.out.println(timedString.getAccessCount());
//            System.out.println("PASS: " + ex.getMessage());
        }
    }
}
