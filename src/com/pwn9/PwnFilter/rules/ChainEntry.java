package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;

import java.util.Set;

/**
 * Objects that can be attached to ruleChains (eg: rules, and other ruleChains)
 * User: ptoal
 * Date: 13-09-24
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChainEntry {

    public String toString();

    public boolean isValid();

    public boolean apply(FilterState state);

    public Set<String> getPermissionList();


}
