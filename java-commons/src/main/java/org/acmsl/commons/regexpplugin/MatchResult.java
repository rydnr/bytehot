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
 * Filename: MatchResult.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents the result of match in a regexp parsing process.
 *              Different implementations vary but they all must respect this
 *              set of methods.
 *
 */
package org.acmsl.commons.regexpplugin;

/*
 * Importing JetBrains annotations.
 */

/**
 * Represents the result of match in a regexp parsing process. Different
 * implementations vary but they all must respect this set of methods.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface MatchResult
{
    /**
     * Taken from Jakarta ORO javadoc:
     * <i>Returns the contents of the parenthesized subgroups of a match,
     * counting parentheses from left to right and starting from 1. Group 0
     * always refers to the entire match. <br>
     * For example, if the pattern foo(\d+)
     * is used to extract a match from the input abfoo123 , then group(0) will
     * return foo123 and group(1) will return 123 . group(2) will return null
     * because there is only one subgroup in the original pattern.</i>
     * @param group The pattern subgroup to return.
     * @return A string containing the indicated pattern subgroup. Group 0
     * always refers to the entire match. If a group was never matched, it
     * returns null. This is not to be confused with a group matching the null
     * string, which will return a String of length 0.
     */
    
    public String group(int group);

    /**
     * Taken from Jakarta ORO 2.0.6 javadoc:
     * <i>The number of groups contained in the result. This number
     * includes the 0th group. In other words, the result refers to
     * the number of parenthesized subgroups plus the entire match
     * itself.</i>.
     * @return such value.
     */
    public int groups();
}
