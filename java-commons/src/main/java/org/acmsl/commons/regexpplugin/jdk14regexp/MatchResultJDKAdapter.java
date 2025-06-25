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
 * Filename: MatchResultJDKAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents the result of match in a regexp parsing
 *              process using JDK1.4 regexp package.
 *
 */
package org.acmsl.commons.regexpplugin.jdk14regexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.MatchResult;

/*
 * Importing some JDK1.4 regexp classes.
 */
import java.lang.IllegalStateException;
import java.util.regex.Matcher;

/*
 * Importing Commons-Logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing Jetbrains annotations.
 */

/**
 * Represents the result of match in a regexp parsing process using
 * JDK1.4 regexp package.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class MatchResultJDKAdapter
    implements  MatchResult
{
    /**
     * Adapted object.
     */
    private Matcher m__Adaptee;

    /**
     * Constructs a MatchResultJDKAdapter from given JDK1.4
     * Regexp-specific instance.
     * @param matcher JDK1.4 matcher object to adapt.
     */
    public MatchResultJDKAdapter(final Matcher matcher)
    {
        immutableSetMatcher(matcher);
    }

    /**
     * Specifies the instance to adapt.
     * @param matcher the adaptee.
     */
    protected final void immutableSetMatcher(final Matcher matcher)
    {
        m__Adaptee = matcher;
    }

    /**
     * Specifies the instance to adapt.
     * @param matcher the adaptee.
     */
    protected void setMatcher(final Matcher matcher)
    {
        immutableSetMatcher(matcher);
    }

    /**
     * Retrieves the adapted instance.
     * @return such adaptee.
     */
    
    public Matcher getMatcher()
    {
        return m__Adaptee;
    }

    /**
     * Taken from JDK1.4 javadoc:
     * <i>Returns the input subsequence captured by the given group
     * during the previous match operation.</i>.
     * @param group The index of a capturing group in this matcher's
     * pattern.
     * @return The (possibly empty) subsequence captured by the group
     * during the previous match, or null if the group failed to match
     * part of the input.
     */
    @Override
    
    public String group(final int group)
    {
        return group(group, getMatcher());
    }

    /**
     * Taken from JDK1.4 javadoc:
     * <i>Returns the input subsequence captured by the given group
     * during the previous match operation.</i>.
     * @param group The index of a capturing group in this matcher's
     * pattern.
     * @param matcher the regexp matcher.
     * @return The (possibly empty) subsequence captured by the group
     * during the previous match, or null if the group failed to match
     * part of the input.
     */
    
    protected String group(final int group, final Matcher matcher)
    {
        String result = null;

        try
        {
            // To ensure the groups are filled in.
            matcher.matches();

            result = matcher.group(group);
        }
        catch  (final IllegalStateException illegalStateException)
        {
            LogFactory.getLog(MatchResultJDKAdapter.class).error(
                "group not found",
                illegalStateException);
        }

        return result;
    }

    /**
     * Taken from JDK 1.4 javadoc:
     * <i>Returns the number of capturing groups in this matcher's
     * pattern.</i>.
     * @return such value.
     */
    @Override
    public int groups()
    {
        return groups(getMatcher());
    }

    /**
     * Taken from JDK 1.4 javadoc:
     * @param matcher the matcher.
     * <i>Returns the number of capturing groups in this matcher's
     * pattern.</i>.
     * @return such value.
     */
    protected int groups(final Matcher matcher)
    {
        return matcher.groupCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    
    public String toString()
    {
        return
              "{ \"adaptee\": \"" + m__Adaptee.hashCode() + '"'
            + ", \"class\": \"MatchResultJDKAdapter\""
            + ", \"package\": \"org.acmsl.commons.regexpplugin.jdk14regexp\""
            + '}';
    }
}
