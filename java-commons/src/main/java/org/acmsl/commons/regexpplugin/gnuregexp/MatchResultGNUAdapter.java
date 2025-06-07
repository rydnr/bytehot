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
 * Filename: MatchResultGNUAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents the result of match in a regexp parsing
 *              process using GNU Regexp 1.1.4 package.
 *
 */
package org.acmsl.commons.regexpplugin.gnuregexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.MatchResult;

/*
 * Importing some GNU Regexp 1.1.4 classes.
 */
import gnu.regexp.REMatch;

/**
 * Represents the result of match in a regexp parsing process using
 * GNU Regexp 1.1.4 package.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class MatchResultGNUAdapter
    implements  MatchResult
{
    /**
     * Adapted object.
     */
    private REMatch m__Adaptee;

    /**
     * Group count.
     */
    private int m__iGroups = 0;

    /**
     * Constructs a MatchResultGNUAdapter from given GNU Regexp 1.1.4
     * REMatch instance.
     * @param adaptee GNU Regexp 1.1.4 REMatch object to adapt.
     * @param groups the group count.
     */
    public MatchResultGNUAdapter(final REMatch adaptee, final int groups)
    {
        immutableSetREMatch(adaptee);

        immutableSetGroups(groups);
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the instance to adapt.
     */
    protected final void immutableSetREMatch(REMatch adaptee)
    {
        m__Adaptee = adaptee;
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the instance to adapt.
     */
    protected void setREMatch(REMatch adaptee)
    {
        immutableSetREMatch(adaptee);
    }

    /**
     * Retrieves the adaptee.
     * @return the instance to adapt.
     */
    protected REMatch getREMatch()
    {
        return m__Adaptee;
    }

    /**
     * Specifies the group count.
     * @param groups the number of groups.
     */
    protected final void immutableSetGroups(final int groups)
    {
        m__iGroups = groups;
    }

    /**
     * Specifies the group count.
     * @param groups the number of groups.
     */
    protected void setGroups(final int groups)
    {
        immutableSetGroups(groups);
    }

    /**
     * Taken from GNU Regexp 1.4 javadoc:
     * <i>Returns the maximum number of subexpressions in this regular
     * expression</i>.
     * @return such value.
     */
    @Override
    public int groups()
    {
        return m__iGroups;
    }

    /**
     * Taken from JDK1.4 javadoc:
     * <i>Returns the string matching the given subexpression. The
     * subexpressions are indexed starting with one, not zero.
     * That is, the subexpression identified by the first set of
     * parentheses in a regular expression could be retrieved from an
     * REMatch by calling match.toString(1).</i>
     * @param group Index of the subexpression.
     * @return the group matched at given position.
     */
    @Override
    public String group(final int group)
    {
        String result = "";

        final REMatch t_REMatch = getREMatch();

        if  (t_REMatch != null)
        {
            if  (group < 1)
            {
                result = t_REMatch.toString();
            }
            else
            {
                result = t_REMatch.toString(group);
            }
        }

        return result;
    }
}
