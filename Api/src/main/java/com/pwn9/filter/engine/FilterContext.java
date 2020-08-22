package com.pwn9.filter.engine;

import com.pwn9.filter.engine.api.EnhancedString;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.rules.chain.Rule;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/08/2020.
 */
public interface FilterContext {
    void setNotifyMessage(String perm, String message);
    Map<String, String> getNotifyMessages();
    MessageAuthor getAuthor();
    EnhancedString getOriginalMessage();
    EnhancedString getModifiedMessage();
    void setModifiedMessage(EnhancedString newMessage);
    FilterClient getFilterClient();
    boolean isAborted();
    List<Rule> getMatchedRules();
    void setPattern(Pattern pattern);
    Pattern getPattern();
    boolean isCancelled();
    void setCancelled();
    boolean loggingOn();
    List<String> getLogMessages();
    void addLogMessage(String message);
    void setLogging();
    void setAborted();
    Rule getRule();
    void setRule(Rule rule);

}
