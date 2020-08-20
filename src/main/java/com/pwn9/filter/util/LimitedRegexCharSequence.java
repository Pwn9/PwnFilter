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

package com.pwn9.filter.util;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Create a Timed Regex match.
 * User: Sage905
 * Date: 13-07-26
 * Time: 4:35 PM
 *
 * @author Sage905
 * @version $Id: $Id
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

    private final Ticker ticker;

    private final Stopwatch stopwatch;

    private final int timeoutMillis;

    private long accessCount;

    /**
     * <p>Constructor for LimitedRegexCharSequence.</p>
     *
     * @param inner         a {@link java.lang.CharSequence} object.
     * @param timeoutMillis a int.
     */

    public LimitedRegexCharSequence(CharSequence inner, int timeoutMillis) {
        this(inner, timeoutMillis, Ticker.systemTicker());
    }

    LimitedRegexCharSequence(CharSequence inner, int timeoutMillis, Ticker ticker) {
        super();
        if (inner == null) {
            throw new NullPointerException("CharSequence must not be null");
        }
        this.inner = inner;

        this.ticker = ticker;
        this.stopwatch = Stopwatch.createStarted(this.ticker);

        this.timeoutMillis = timeoutMillis;
        accessCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    public char charAt(int index) {
        accessCount++;
        if (stopwatch.elapsed(TimeUnit.MILLISECONDS) > timeoutMillis) {
            throw new RegexTimeoutException("Timeout occurred after " + timeoutMillis + "ms");
        }
        return inner.charAt(index);
    }

    /**
     * <p>length.</p>
     *
     * @return a int.
     */
    public int length() {
        return inner.length();
    }

    /**
     * {@inheritDoc}
     */
    public CharSequence subSequence(int start, int end) {
        return new LimitedRegexCharSequence(inner.subSequence(start, end), timeoutMillis, this.ticker);
    }

    /**
     * <p>Getter for the field <code>accessCount</code>.</p>
     *
     * @return a long.
     */
    long getAccessCount() {
        return accessCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String toString() {
        return inner.toString();
    }

    public final static class RegexTimeoutException extends RuntimeException {
        RegexTimeoutException(String message) {
            super(message);
        }
    }
}
