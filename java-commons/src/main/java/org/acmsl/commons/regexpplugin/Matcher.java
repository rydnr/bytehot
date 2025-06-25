//;-*- mode: java -*-
/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: Matcher.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents a regexp pattern matcher. Different implementations
 *              vary but they all must respect this set of methods.
 *
 */
package org.acmsl.commons.regexpplugin;

/*
 * Importing JetBrains annotations.
 */

/**
 * Represents a regexp pattern matcher. Different implementations vary but they
 * all must respect this set of methods.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface Matcher
{
    /**
     * Checks if given text contains specified pattern.
     * @param text the text to analyze.
     * @param pattern the regular expression to apply.
     * @return true if the pattern is found.
     */
    public boolean contains(final String text, final Pattern pattern);

    /**
     * Retrieves the last match found due to a previous call to
     * <i>contains</i> method.
     * @return such match result.
     */
    
    public MatchResult getMatch();
}
