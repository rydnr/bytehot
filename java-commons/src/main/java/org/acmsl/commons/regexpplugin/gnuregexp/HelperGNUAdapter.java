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
 * Filename: HelperGNUAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: GNU Regexp 1.1.4-specific helper adapter.
 *
 */
package org.acmsl.commons.regexpplugin.gnuregexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.Helper;
import org.acmsl.commons.regexpplugin.MalformedPatternException;

/*
 * Importing GNU Regexp 1.1.4 classes.
 */
import gnu.regexp.RE;
import gnu.regexp.REException;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/**
 * GNU Regexp 1.1.4-specific helper adapter.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class HelperGNUAdapter
    implements  Helper
{
    /**
     * Finds all occurrences of a specified pattern in given input contents,
     * and replaces them with passed String.
     * @param input the input text to process.
     * @param pattern the pattern to replace.
     * @param replacement the replacement text.
     * @return the updated input.
     * @throws MalformedPatternException if given regexp is malformed.
     */
    @Override
    @NotNull
    public String replaceAll(
        @NotNull final String input, @NotNull final String pattern, @NotNull final String replacement)
        throws  MalformedPatternException
    {
        @NotNull final String result;

        try
        {
            @NotNull final RE t_RE = new RE(pattern);

            result = t_RE.substituteAll(input, replacement);
        }
        catch  (@NotNull final REException reException)
        {
            throw
                new MalformedPatternExceptionGNUAdapter(
                    reException);
        }

        return result;
    }
}
