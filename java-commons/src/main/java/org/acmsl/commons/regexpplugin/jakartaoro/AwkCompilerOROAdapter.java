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
 * Filename: AwkCompilerOROAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Jakarta ORO-specific Awk compiler adapter. A delegation is used
 *              because AwkCompiler is a final class.
 *
 */
package org.acmsl.commons.regexpplugin.jakartaoro;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.Compiler;
import org.acmsl.commons.regexpplugin.Pattern;

/*
 * Importing ORO classes.
 */
import org.apache.oro.text.awk.AwkCompiler;
import org.apache.oro.text.regex.MalformedPatternException;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Jakarta ORO-specific Awk compiler adapter. A delegation is used because
 * AwkCompiler is a final class.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class AwkCompilerOROAdapter
    implements  Compiler
{
    /**
     * Delegated instance.
     */
    private AwkCompiler m__Instance;

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
     */
    @Override
    @NotNull
    public Pattern compile(@NotNull final String regexp)
        throws  org.acmsl.commons.regexpplugin.MalformedPatternException
    {
        @Nullable Pattern result = null;

        try
        {
            @NotNull final AwkCompiler t_Compiler = getDelegatedInstance();

            result = new PatternOROAdapter(t_Compiler.compile(regexp));
        }
        catch  (final org.apache.oro.text.regex.MalformedPatternException exception)
        {
            throw new MalformedPatternExceptionOROAdapter(exception);
        }
        catch  (final IllegalArgumentException illegalArgumentException)
        {
            if  (resetOptions())
            {
                result = compile(regexp);
            }

            if (result == null)
            {
                throw
                    new MalformedPatternExceptionOROAdapter(
                        new MalformedPatternException(illegalArgumentException.getMessage()));
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
     * Retrieves an instance of AwkCompiler class.
     * @return a new (or already existing) compiler.
     */
    @NotNull
    AwkCompiler getDelegatedInstance()
    {
        @NotNull final AwkCompiler result;

        if  (m__Instance == null)
        {
            m__Instance = new AwkCompiler();
        }

        result = m__Instance;

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
        return "AwkCompilerOROAdapter{" +
               "caseSensitive=" + m__bCaseSensitive +
               ", instance=" + m__Instance +
               ", multiline=" + m__bMultiline +
               '}';
    }
}
