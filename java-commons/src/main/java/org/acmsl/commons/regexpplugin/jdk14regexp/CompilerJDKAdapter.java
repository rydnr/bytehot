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
 * Filename: CompilerJDKAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: JDK1.4-specific regexp compiler adapter.
 *              This class makes possible the use of JDK1.4
 *              compilers inside this API.
 *
 */
package org.acmsl.commons.regexpplugin.jdk14regexp;

/*
 * Importing some ACM classes.
 */
import org.acmsl.commons.regexpplugin.Compiler;
import org.acmsl.commons.regexpplugin.MalformedPatternException;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing JDK1.4 regexp classes.
 */
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * JDK1.4-specific regexp compiler adapter. This class makes
 * possible the use of JDK1.4 compilers within this API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class CompilerJDKAdapter
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
     * Compiles given regular expression and creates a Pattern object to
     * apply such rule on concrete text contents.
     * @param regexp the regular expression to compile.
     * @return the Pattern associated to such regular expression.
     * @throws MalformedPatternException if the regexp is not valid.
     */
    @Override
    @NotNull
    public org.acmsl.commons.regexpplugin.Pattern compile(@NotNull final String regexp)
        throws  MalformedPatternException
    {
        return compile(regexp, isCaseSensitive(), isMultiline());
    }

    /**
     * Compiles given regular expression and creates a Pattern object to
     * apply such rule on concrete text contents.
     * @param regexp the regular expression to compile.
     * @param caseSensitive such condition.
     * @param multiline such condition.
     * @return the Pattern associated to such regular expression.
     * @throws MalformedPatternException if the regexp is not valid.
     */
    @NotNull
    protected org.acmsl.commons.regexpplugin.Pattern compile(
        @NotNull final String regexp,
        final boolean caseSensitive,
        final boolean multiline)
      throws  MalformedPatternException
    {
        @NotNull org.acmsl.commons.regexpplugin.Pattern result;

        try
        {
            int t_iOptions = 0;

            t_iOptions |=
                (caseSensitive)
                ?  0
                :  Pattern.CASE_INSENSITIVE;

            t_iOptions |=
                (multiline)
                ?  Pattern.MULTILINE
                :  0;

            final Pattern t_Pattern;

            if  (t_iOptions == 0)
            {
                t_Pattern = Pattern.compile(regexp);
            }
            else
            {
                t_Pattern = Pattern.compile(regexp, t_iOptions);
            }

            if  (t_Pattern != null)
            {
                result = new PatternJDKAdapter(t_Pattern);
            }
            else 
            {
                throw new MalformedPatternException("pattern compilation error");
            }
        }
        catch  (final PatternSyntaxException exception)
        {
            throw new MalformedPatternExceptionJDKAdapter(exception);
        }
        catch  (final IllegalArgumentException illegalArgumentException)
        {
            if  (resetOptions())
            {
                result = compile(regexp);
            }
            else
            {
                throw new MalformedPatternException("pattern compilation error");
            }
        }

        return result;
    }

    /**
     * Resets the compiler options.
     * @return true if the options actually changed.
     */
    protected boolean resetOptions()
    {
        final boolean result =
            (   (isCaseSensitive())
             || (isMultiline()));

        setCaseSensitive(false);

        setMultiline(false);

        return result;
    }

    /**
     * Sets whether the compiler should care about case sensitiveness
     * or not.
     * @param caseSensitive true for differentiate upper from lower case.
     */
    protected final void immutableSetCaseSensitive(final boolean caseSensitive)
    {
        m__bCaseSensitive = caseSensitive;
    }

    /**
     * Sets whether the compiler should care about case sensitiveness
     * or not.
     * @param caseSensitive true for differentiate upper from lower case.
     */
    @Override
    public void setCaseSensitive(final boolean caseSensitive)
    {
        immutableSetCaseSensitive(caseSensitive);
    }

    /**
     * Retrieves whether the compiler should care about case sensitiveness
     * or not.
     * @return true if upper from lower cases are processed differently.
     */
    @Override
    public boolean isCaseSensitive()
    {
        return m__bCaseSensitive;
    }

    /**
     * Sets whether the compiler should care about new line delimiters
     * or not.
     * @param multiline false for parsing each line at a time.
     */
    protected final void immutableSetMultiline(final boolean multiline)
    {
        m__bMultiline = multiline;
    }

    /**
     * Sets whether the compiler should care about new line delimiters
     * or not.
     * @param multiline false for parsing each line at a time.
     */
    @Override
    public void setMultiline(final boolean multiline)
    {
        immutableSetMultiline(multiline);
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
    @NotNull
    public String toString()
    {
        return "CompilerJDKAdapter{" +
               "caseSensitive=" + m__bCaseSensitive +
               ", multiline=" + m__bMultiline +
               '}';
    }
}
