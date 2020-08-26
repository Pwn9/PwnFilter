package com.pwn9.filter.engine.rules.chain;

import com.pwn9.filter.engine.FilterContext;
import com.pwn9.filter.engine.FilterService;
import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.rules.Condition;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/08/2020.
 */
public interface Rule extends ChainEntry {
    boolean isValid();
    String toString();
    String getId();
    String getDescription();
    Pattern getPattern();
    void setPattern(String pattern);
    void setDescription(String description);
    Set<String> getConditionsMatching(String matchString);
    void apply(FilterContext filterContext, FilterService filterServiceImpl);
    boolean addCondition(Condition c);
    List<Condition> getConditions();
    boolean addConditions(Collection<Condition> conditionList);
    boolean addAction(Action a);
}
