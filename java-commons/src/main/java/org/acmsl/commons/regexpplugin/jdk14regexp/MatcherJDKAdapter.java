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
 * Filename: MatcherJDKAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: JDK1.4-specific regexp matcher adapter. This class
 *              makes possible the use of JDK1.4 matchers inside this
 *              API.
 *
 */
package org.acmsl.commons.regexpplugin.jdk14regexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.MatchResult;

/*
 * Importing JDK1.4 regexp classes.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/**
 * JDK1.4-specific regexp matcher adapter. This class makes possible
 * the use of JDK1.4 matchers inside this API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class MatcherJDKAdapter
    implements  org.acmsl.commons.regexpplugin.Matcher
{
    /**
     * Concrete engine implementation.
     */
    private Matcher m__Matcher;

    /**
     * Constructs a MatcherJDKAdapter.
     */
    public MatcherJDKAdapter()  {}

    /**
     * Checks if given text contains specified pattern.
     * @param text the text to analyze.
     * @param pattern the regular expression to apply.
     * @return <code>true</code> if the pattern is found.
     */
    @Override
    public boolean contains(
        @NotNull final String text,
        @NotNull final org.acmsl.commons.regexpplugin.Pattern pattern)
    {
        final boolean result;

        final Pattern t_Pattern =
            ((PatternJDKAdapter) pattern).getPattern();

        final @NotNull Matcher t_Matcher = t_Pattern.matcher(text);

        result = t_Matcher.find();

        setMatcher(t_Matcher);

        return result;
    }

    /**
     * Specifies the matcher.
     * @param matcher the new matcher.
     */
    protected final void immutableSetMatcher(final Matcher matcher)
    {
        m__Matcher = matcher;
    }

    /**
     * Specifies the matcher.
     * @param matcher the new matcher.
     */
    protected void setMatcher(final Matcher matcher)
    {
        immutableSetMatcher(matcher);
    }

    /**
     * Retrieves the adapted Matcher instance.
     * @return a the adapted matcher.
     */
    protected Matcher getMatcher()
    {
        return m__Matcher;
    }

    /**
     * Retrieves the last match found due to a previous call to
     * <i>contains</i> method.
     * @return such match result.
     */
    @Override
    public MatchResult getMatch()
    {
        return getMatch(getMatcher());
    }

    /**
     * Retrieves the last match found due to a previous call to
     * <i>contains</i> method.
     * @param matcher the matcher.
     * @return such match result.
     */
    protected MatchResult getMatch(final Matcher matcher)
    {
        return new MatchResultJDKAdapter(matcher);
    }

    @NotNull
    @Override
    public String toString()
    {
        return
              "{ \"class\": \"" + MatcherJDKAdapter.class.getName() + "\""
            + ", \"matcher\": \"" + this.m__Matcher + "\" }";
    }
}
