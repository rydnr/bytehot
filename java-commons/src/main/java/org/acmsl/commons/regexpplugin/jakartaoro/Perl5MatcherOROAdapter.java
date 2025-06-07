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
 * Filename: Perl5MatcherOROAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Jakarta ORO-specific regexp matcher adapter.
 *
 */
package org.acmsl.commons.regexpplugin.jakartaoro;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.Matcher;
import org.acmsl.commons.regexpplugin.MatchResult;
import org.acmsl.commons.regexpplugin.Pattern;

/*
 * Importing Jakarta ORO classes.
 */
import org.apache.oro.text.regex.Perl5Matcher;

/*
 * Importing Jetbrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Jakarta ORO-specific regexp matcher adapter.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class Perl5MatcherOROAdapter
    implements  Matcher
{
    /**
     * Delegated instance.
     */
    private Perl5Matcher m__Instance;

    /**
     * Constructs a Perl5MatcherOROAdapter for given object.
     */
    public Perl5MatcherOROAdapter()
    {
        immutableSetMatcher(new Perl5Matcher());
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the matcher to adapt.
     */
    protected final void immutableSetMatcher(@NotNull final Perl5Matcher adaptee)
    {
        m__Instance = adaptee;
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the matcher to adapt.
     */
    protected void setMatcher(@NotNull final Perl5Matcher adaptee)
    {
        immutableSetMatcher(adaptee);
    }

    /**
     * Retrieves an instance of Perl5Matcher class.
     * @return a the adapted matcher.
     */
    @NotNull
    protected Perl5Matcher getMatcher()
    {
        return m__Instance;
    }

    /**
     * Checks if given text contains specified pattern.
     * @param text the text to analyze.
     * @param pattern the regular expression to apply.
     * @return <code>true</code> if the pattern is found.
     */
    @Override
    public boolean contains(@NotNull final String text, @NotNull final Pattern pattern)
    {
        return contains(text, pattern, getMatcher());
    }

    /**
     * Checks if given text contains specified pattern.
     * @param text the text to analyze.
     * @param pattern the regular expression to apply.
     * @param matcher the matcher.
     * @return <code>true</code> if the pattern is found.
     */
    protected boolean contains(
        @NotNull final String text, @NotNull final Pattern pattern, @NotNull final Perl5Matcher matcher)
    {
        return
            matcher.contains(
                text, ((PatternOROAdapter) pattern).getPattern());
    }

    /**
     * Retrieves the last match found due to a previous call to
     * <i>contains</i> method.
     * @return such match result.
     */
    @Override
    @Nullable
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
    @Nullable
    protected MatchResult getMatch(@NotNull final Perl5Matcher matcher)
    {
        return new MatchResultOROAdapter(matcher.getMatch());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString()
    {
        return
              "{ \"adaptee\": \"" + m__Instance.hashCode() + '"'
            + ", \"class\": \"Perl5MatcherOROAdapter\""
            + ", \"package\": \"org.acmsl.commons.regexplugin.jakartaoro\" }";
    }
}
