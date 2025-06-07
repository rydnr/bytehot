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
 * Filename: HelperOROAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Jakarta ORO-specific regexp helper adapter.
 *
 */
package org.acmsl.commons.regexpplugin.jakartaoro;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.Helper;
import org.acmsl.commons.utils.ClassLoaderUtils;

/*
 * Importing ORO classes.
 */
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.StringSubstitution;
import org.apache.oro.text.regex.Util;

/*
 * Importing some Apache Commons Logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Jakarta ORO-specific regexp helper adapter.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class HelperOROAdapter
    implements  Helper
{
    /**
     * Finds all occurrences of a specified pattern in given input contents,
     * and replaces them with passed String.
     * @param input the input text to process.
     * @param pattern the pattern to replace.
     * @param replacement the replacement text.
     * @return the updated input.
     * @throws org.acmsl.commons.regexpplugin.MalformedPatternException if given regexp is malformed.
     */
    @Override
    @NotNull
    public String replaceAll(
        @NotNull final String input, @NotNull final String pattern, @NotNull final String replacement)
      throws  org.acmsl.commons.regexpplugin.MalformedPatternException
    {
        @Nullable String result = null;

        try
        {
            result =
                Util.substitute(
                    new Perl5Matcher(),
                    new Perl5Compiler().compile(pattern),
                    new StringSubstitution(
//                            Perl5Compiler.quotemeta(replacement)),
                        replacement),
                    input,
                    Util.SUBSTITUTE_ALL);
        }
        catch  (final MalformedPatternException malformedPatternException)
        {
            throw
                new
                    org.acmsl.commons.regexpplugin
                        .MalformedPatternException(
                            "Invalid pattern: " + pattern,
                            malformedPatternException);
        }
        catch  (final NoSuchMethodError incompatibleVersionError)
        {
            @Nullable final String location = findLocation(Util.class);

            // This happens on Oro 2.0.8 if another Oro version
            // is loaded first.
            LogFactory.getLog(HelperOROAdapter.class).fatal(
                  "Incompatible Oro version, loaded from: "
                + ((location != null) ? "[" + location + "]" : ""),
                incompatibleVersionError);
        }

        if  (result == null) 
        {
            result = "";
        }

        if  (result.length() == 0) 
        {
            result = input;
        }
        
        return result;
    }

    /**
     * Finds the location of given instance.
     * @param classInstance the class to locate.
     * @param <T> the class type.
     * @return such information.
     */
    @Nullable
    protected <T> String findLocation(@NotNull final Class<T> classInstance)
    {
        return findLocation(classInstance, ClassLoaderUtils.getInstance());
    }

    /**
     * Finds the location of given instance.
     * @param classInstance the class to locate.
     * @param classLoaderUtils the {@link ClassLoaderUtils} instance.
     * @param <T> the class type.
     * @return such information.
     */
    @Nullable
    protected <T> String findLocation(
        @NotNull final Class<T> classInstance, @NotNull final ClassLoaderUtils classLoaderUtils)
    {
        return classLoaderUtils.findLocation(classInstance, true);
    }
}
