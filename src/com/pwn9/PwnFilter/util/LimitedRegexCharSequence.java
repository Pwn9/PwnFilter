package com.pwn9.PwnFilter.util;

/**
 * Create a Timed Regex match.
 * User: ptoal
 * Date: 13-07-26
 * Time: 4:35 PM
 */

/* NOTE: The goal here is to create a matcher that won't run forever.
 Here's how this works:
 1. The TimeoutRegexCharSequence has a timeout set in it of the system time + some interval.
 2. On every charAt access, we check to see if we're past that time.
 3. If so, then throw an exception, which will halt the regex processing, and notify
 4. the caller.  In PwnFilter, we can then check for this exception, disable the rule, and log the offending regex and
 string.
*/

public class LimitedRegexCharSequence implements CharSequence {

    private final CharSequence inner;

    private final int timeoutMillis;

    private final long timeoutTime;

    private long accessCount;

    public LimitedRegexCharSequence(CharSequence inner, int timeoutMillis)  {
        super();
        this.inner = inner;
        this.timeoutMillis = timeoutMillis;
        timeoutTime = System.currentTimeMillis() + timeoutMillis;
        accessCount = 0;
    }

    public char charAt(int index) {
        accessCount++ ;
        if (System.currentTimeMillis() > timeoutTime) {
            throw new RuntimeException("Timeout occurred after " + timeoutMillis + "ms");
        }
        return inner.charAt(index);
    }

    public int length() {
        return inner.length();
    }

    public CharSequence subSequence(int start, int end) {
        return new LimitedRegexCharSequence(inner.subSequence(start, end), timeoutMillis);
    }

    public long getAccessCount() {
        return accessCount;
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
