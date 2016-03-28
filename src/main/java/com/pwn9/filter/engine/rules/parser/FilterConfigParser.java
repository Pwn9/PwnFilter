/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2015 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.filter.engine.rules.parser;

import com.pwn9.filter.engine.rules.chain.Chain;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;

import java.io.File;
import java.util.List;

/**
 * Parser takes an inputStream, and uses a RuleChainBuilder object to build
 * a RuleChain
 *
 * Created by Sage905 on 15-10-04.
 */
public interface FilterConfigParser {

    Chain parse(File source,
                List<File> parents) throws InvalidChainException ;
}
