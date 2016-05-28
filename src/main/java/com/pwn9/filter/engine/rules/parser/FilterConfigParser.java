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

package com.pwn9.filter.engine.rules.parser;

import com.pwn9.filter.engine.rules.chain.Chain;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;

import java.io.File;
import java.util.List;

/**
 * Parser takes an inputStream, and uses a RuleChainBuilder object to build
 * a RuleChain
 **/

interface FilterConfigParser {

    Chain parse(File source,
                List<File> parents) throws InvalidChainException;
}
