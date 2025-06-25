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
 * Filename: CompilerRegexpAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Jakarta Regexp-specific regexp compiler adapter.
 *              This class makes possible the use of Jakarta Regexp
 *              compilers inside this API. Delegation is used to be
 *              able to write compile(String) method with different
 *              signature as Jakarta Regexp's.
 *
 */
package org.acmsl.commons.regexpplugin.jakartaregexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.Literals;
import org.acmsl.commons.regexpplugin.Compiler;
import org.acmsl.commons.regexpplugin.MalformedPatternException;
import org.acmsl.commons.regexpplugin.Pattern;

/*
 * Importing Jakarta Regexp classes.
 */
import org.apache.regexp.RE;
import org.apache.regexp.RECompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;

/*
 * Importing JetBrains annotations.
 */

/**
 * Jakarta Regexp-specific regexp compiler adapter. This class makes
 * possible the use of Jakarta Regexp compilers inside this API.
 * Delegation is used to be able to write compile(String) method
 * with different signature as Jakarta Regexp's.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class CompilerRegexpAdapter
    implements  Compiler
{
    /**
     * Delegated instance.
     */
    private RECompiler m__Instance;

    /**
     * Case sensitiveness.
     */
    private boolean m__bCaseSensitive;

    /**
     * Multiline parsing.
     */
    private boolean m__bMultiline;

    /**
     * Specifies the adapted instance.
     * @param compiler the compiler to adapt.
     */
    protected final void immutableSetRECompiler(final RECompiler compiler)
    {
        m__Instance = compiler;
    }

    /**
     * Specifies the adapted instance.
     * @param compiler the compiler to adapt.
     */
    protected void setRECompiler(final RECompiler compiler)
    {
        immutableSetRECompiler(compiler);
    }

    /**
     * Retrieves an instance of RECompilerCompiler class.
     * @return a new (or already existing) compiler.
     */
    protected synchronized RECompiler getRECompiler()
    {
        RECompiler result = m__Instance;

        if  (result == null)
        {
            result = new RECompiler();

            setRECompiler(result);
        }

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

    /**
     * Compiles given regular expression and creates a Pattern object to
     * apply such rule on concrete text contents.
     * @param regexp the regular expression to compile.
     * @return the Pattern associated to such regular expression.
     * @throws MalformedPatternException if given regexp is malformed.
     */
    @Override
    
    public Pattern compile(final String regexp)
        throws  MalformedPatternException
    {
        return
            compile(
                regexp, getRECompiler(), isCaseSensitive(), isMultiline());
    }

    /**
     * Compiles given regular expression and creates a Pattern object to
     * apply such rule on concrete text contents.
     * @param regexp the regular expression to compile.
     * @param compiler the compiler.
     * @param caseSensitive the case sensitiveness.
     * @param multiline the multiline.
     * @return the Pattern associated to such regular expression.
     * @throws MalformedPatternException if given regexp is malformed.
     */
    
    public Pattern compile(
        final String regexp,
        final RECompiler compiler,
        final boolean caseSensitive,
        final boolean multiline)
      throws  MalformedPatternException
    {
        Pattern result = null;

        try
        {
            final REProgram t_REProgram = compiler.compile(regexp);

            final RE t_RE = new RE(t_REProgram);

            int t_iOptions = 0;

            t_iOptions |=
                (caseSensitive)
                ?  RE.MATCH_NORMAL
                :  RE.MATCH_CASEINDEPENDENT;

            t_iOptions |=
                (multiline)
                ?  RE.MATCH_MULTILINE
                :  RE.MATCH_SINGLELINE;

            if (t_iOptions != 0)
            {
                t_RE.setMatchFlags(t_iOptions);
            }

            result = new PatternRegexpAdapter(t_REProgram, t_RE);
        }
        catch  (final RESyntaxException exception)
        {
            throw new MalformedPatternExceptionRegexpAdapter(exception);
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
    protected boolean resetOptions()
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

    @Override
    
    public String toString()
    {
        return "CompilerRegexpAdapter{" +
               Literals.M_B_CASE_SENSITIVE + m__bCaseSensitive +
               ", m__Instance=" + m__Instance +
               Literals.M_B_MULTILINE + m__bMultiline +
               '}';
    }
}
