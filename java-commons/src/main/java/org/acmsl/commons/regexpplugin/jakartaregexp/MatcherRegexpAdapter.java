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
 * Filename: MatcherRegexpAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Implements a matcher using Jakarta Regexp package.
 */
package org.acmsl.commons.regexpplugin.jakartaregexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.Matcher;
import org.acmsl.commons.regexpplugin.MatchResult;
import org.acmsl.commons.regexpplugin.Pattern;

/*
 * Importing some Jakarta Regexp classes.
 */
import org.apache.regexp.RE;

/*
 * Importing Jetbrains annotations.
 */

/**
 * Implements a {@link Matcher} in Jakarta Regexp package.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class MatcherRegexpAdapter
    implements  Matcher
{
    /**
     * Private RE reference.
     */
    private RE m__RE;

    /**
     * Constructs an empty REMatcherRegexpAdapter.
     */
    public MatcherRegexpAdapter()  {}

    /**
     * Sets the instance to adapt.
     * @param adaptee the re object to adapt.
     */
    protected final void immutableSetRE(final RE adaptee)
    {
        m__RE = adaptee;
    }

    /**
     * Sets the instance to adapt.
     * @param adaptee the re object to adapt.
     */
    protected void setRE(final RE adaptee)
    {
        immutableSetRE(adaptee);
    }

    /**
     * Retrieves the adapted instance.
     * @return the RE object.
     */
    
    public RE getRE()
    {
        return m__RE;
    }

    /**
     * Checks if given text contains specified pattern.
     * @param text the text to analyze.
     * @param pattern the regular expression to apply.
     * @return <code>true</code> if the pattern is found.
     */
    @Override
    public boolean contains(final String text, final Pattern pattern)
    {
        final boolean result;

        final PatternRegexpAdapter t_PatternAdapter = (PatternRegexpAdapter) pattern;

        final RE t_RE = t_PatternAdapter.getRE();

        setRE(t_RE);

        result = t_RE.match(text);

        return result;
    }

    /**
     * Retrieves the last match found due to a previous call to
     * <i>contains</i> method.
     * @return such match result.
     */
    @Override
    
    public MatchResult getMatch()
    {
        return getMatch(getRE());
    }

    /**
     * Retrieves the last match found due to a previous call to
     * <i>contains</i> method.
     * @param re the RE instance.
     * @return such match result.
     */
    protected MatchResult getMatch(final RE re)
    {
        return new MatchResultRegexpAdapter(re);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    
    public String toString()
    {
        return
              "{ \"adaptee\": \"" + m__RE.hashCode() + '"'
            + ", \"class\": \"MatcherRegexpAdapter\""
            + ", \"package\": \"org.acmsl.commons.regexpplugin.jakartaregexp"
            + '}';
    }
}
