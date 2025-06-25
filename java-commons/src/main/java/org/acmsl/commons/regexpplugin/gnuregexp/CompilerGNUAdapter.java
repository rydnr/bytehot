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
 * Filename: CompilerGNUAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: GNU Regexp 1.1.4-specific compiler adapter.
 *              This class makes possible the use of GNU Regexp 1.1.4
 *              compilers inside this API.
 *
 */
package org.acmsl.commons.regexpplugin.gnuregexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.Literals;
import org.acmsl.commons.regexpplugin.Compiler;
import org.acmsl.commons.regexpplugin.Pattern;
import org.acmsl.commons.regexpplugin.MalformedPatternException;

/*
 * Importing GNU Regexp 1.1.4 classes.
 */
import gnu.regexp.RE;
import gnu.regexp.REException;

/*
 * Importing JetBrains annotations.
 */

/**
 * GNU Regexp 1.1.4-specific compiler adapter. This class makes
 * possible the use of GNU Regexp 1.1.4 compilers inside this API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class CompilerGNUAdapter
    implements  Compiler
{
    /**
     * Case sensitiveness.
     */
    private boolean m__bCaseSensitive;

    /**
     * Multiline parsing.
     */
    private boolean m__bMultiline;

    /**
     * Compiles given regular expression and creates a Pattern object
     * to apply such rule on concrete text contents.
     * @param regexp the regular expression to compile.
     * @return the Pattern associated to such regular expression.
     * @throws MalformedPatternException if given regexp is malformed.
     */
    
    @Override
    public Pattern compile(final String regexp)
        throws  MalformedPatternException
    {
        org.acmsl.commons.regexpplugin.Pattern result = null;

        try
        {
            int t_iOptions = 0;

            t_iOptions |=
                (isCaseSensitive())
                ?   0
                :   RE.REG_ICASE;

            t_iOptions |=
                (isMultiline())
                ?   RE.REG_MULTILINE
                :   0;

            final RE t_RE;

            if  (t_iOptions == 0)
            {
                t_RE = new RE(regexp);
            }
            else
            {
                t_RE = new RE(regexp, t_iOptions);
            }

            result = new PatternGNUAdapter(t_RE);
        }
        catch  (final REException exception)
        {
            throw new MalformedPatternExceptionGNUAdapter(exception);
        }
        catch  (final IllegalArgumentException illegalArgumentException)
        {
            if  (resetOptions())
            {
                result = compile(regexp);
            }
        }

        return result;
    }

    /**
     * Resets the compiler options.
     * @return true if the options actually changed.
     */
    protected final boolean resetOptions()
    {
        final boolean result;

        result =
            (   (isCaseSensitive())
             || (isMultiline()));

        if  (result)
        {
            setCaseSensitive(false);

            setMultiline(false);
        }

        return result;
    }

    /**
     * Sets whether the compiler should care about case sensitiveness
     * or not.
     * @param caseSensitive true for differentiate upper from lower case.
     */
    @Override
    public void setCaseSensitive(final boolean caseSensitive)
    {
        m__bCaseSensitive = caseSensitive;
    }

    /**
     * Retrieves whether the compiler should care about case sensitiveness
     * or not.
     * @return true if upper from lower cases are processed differently.
     */
    public boolean isCaseSensitive()
    {
        return m__bCaseSensitive;
    }

    /**
     * Sets whether the compiler should care about new line delimiters
     * or not.
     * @param multiline false for parsing each line at a time.
     */
    @Override
    public void setMultiline(final boolean multiline)
    {
        m__bMultiline = multiline;
    }

    /**
     * Sets whether the compiler should care about new line delimiters
     * or not.
     * @return false if the engine parses each line one at a time.
     */
    @Override
    public boolean isMultiline()
    {
        return m__bMultiline;
    }

    @Override
    public String toString()
    {
        return "CompilerGNUAdapter{" +
               Literals.M_B_CASE_SENSITIVE + m__bCaseSensitive +
               Literals.M_B_MULTILINE + m__bMultiline +
               '}';
    }
}
