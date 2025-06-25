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
 * Filename: AwkMatcherOROAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Jakarta ORO-specific regexp compiler adapter. This class makes
 *              possible the use of ORO compilers inside this API. A delegation
 *              is used because Perl5Compiler is a final class.
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
 * Importing ORO classes.
 */
import org.apache.oro.text.awk.AwkMatcher;

/*
 * Importing some commons-logging classes.
 */

/**
 * Jakarta ORO-specific regexp compiler adapter. This class makes possible the
 * use of ORO compilers inside this API. A delegation is used because
 * Perl5Compiler is a final class.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class AwkMatcherOROAdapter
    implements  Matcher
{
    /**
     * Delegated instance.
     */
    private AwkMatcher m__Instance;

    /**
     * Constructs an AwkMatcherOROAdapter for given object.
     */
    public AwkMatcherOROAdapter()
    {
        immutableSetAwkMatcher(new AwkMatcher());
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the instance to adapt.
     */
    protected final void immutableSetAwkMatcher(final AwkMatcher adaptee)
    {
        m__Instance = adaptee;
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the instance to adapt.
     */
    @SuppressWarnings("unused")
    protected void setAwkMatcher(final AwkMatcher adaptee)
    {
        immutableSetAwkMatcher(adaptee);
    }

    /**
     * Retrieves an instance of AwkMatcher class.
     * @return the adapted matcher.
     */
    
    protected AwkMatcher getAwkMatcher()
    {
        return m__Instance;
    }

    /**
     * Checks if given text contains specified pattern.
     * @param text the text to analyze.
     * @param pattern the regular expression to apply.
     * @return true if the pattern is found.
     */
    @Override
    public boolean contains(final String text, final Pattern pattern)
    {
        final boolean result;

        if  (pattern instanceof PatternOROAdapter)
        {
            final AwkMatcher t_AwkMatcher = getAwkMatcher();

            result =
                t_AwkMatcher.contains(
                    text, ((PatternOROAdapter) pattern).getPattern());
        }
        else
        {
            result = false;
        }


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
        final MatchResult result;

        final AwkMatcher t_AwkMatcher = getAwkMatcher();

        final org.apache.oro.text.regex.MatchResult t_MatchResult = t_AwkMatcher.getMatch();

        if (t_MatchResult != null)
        {
            result = new MatchResultOROAdapter(t_MatchResult);
        }
        else
        {
            result = null;
        }

        return result;
    }

    @Override
    
    public String toString()
    {
        return "AwkMatcherOROAdapter{" +
               "instance=" + m__Instance +
               '}';
    }
}
