package com.pwn9.filter.engine.rules.chain;

import com.pwn9.filter.engine.FilterContext;
import com.pwn9.filter.engine.FilterService;

import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/08/2020.
 */
public interface RuleChain extends Chain, ChainEntry {

    int ruleCount();

    void execute(FilterContext context, FilterService filterServiceImpl);

    List<ChainEntry> getChain();
}
