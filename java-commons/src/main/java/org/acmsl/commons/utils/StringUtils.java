//;-*- mode: java -*-
/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: StringUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some useful methods when working with Strings.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.Literals;
import org.acmsl.commons.patterns.Utils;
import org.acmsl.commons.patterns.Singleton;
import org.acmsl.commons.regexpplugin.Compiler;
import org.acmsl.commons.regexpplugin.Helper;
import org.acmsl.commons.regexpplugin.MalformedPatternException;
import org.acmsl.commons.regexpplugin.Matcher;
import org.acmsl.commons.regexpplugin.MatchResult;
import org.acmsl.commons.regexpplugin.Pattern;
import org.acmsl.commons.regexpplugin.RegexpEngine;
import org.acmsl.commons.regexpplugin.RegexpEngineNotFoundException;
import org.acmsl.commons.regexpplugin.RegexpManager;

/*
 * Importing some JDK classes.
 */
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/*
 * Importing commons-logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides some useful methods when working with Strings.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class StringUtils
    implements  Utils,
                Singleton
{
    /**
     * A token used to maintain underscores.
     */
    public static final String SEPARATOR_TOKEN =
        "q1w3e2ewqorgacmslcommonsutilsStringUtilsbvcxzmnf3ddsf";

    /**
     * The default separator.
     */
    public static final char DEFAULT_SEPARATOR = '_';

    /**
     * The default extra separators.
     */
    static final String[] DEFAULT_EXTRA_SEPARATORS =
        new String[]
        {
            "-",
            "\\.",
            ",",
            ";",
            ":"
        };

    /**
     * The Non-alphanumeric regexp.
     */
    static final java.util.regex.Pattern NON_ALPHANUMERIC_REGEXP = java.util.regex.Pattern.compile("\\W+");

    /**
     * The separator regexp.
     */
    static final java.util.regex.Pattern SEPARATOR_REGEXP = java.util.regex.Pattern.compile(DEFAULT_SEPARATOR + "+");

    /**
     * Matches separator followed by lower case letters.
     */
    static final java.util.regex.Pattern SEPARATOR_PLUS_LOWERCASE_REGEXP =
        java.util.regex.Pattern.compile("(.*?)" + DEFAULT_SEPARATOR + "(.)(.*)?");

    /**
     * The english locale.
     */
    protected static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    /**
     * Checks whether given value is null or empty.
     * @param value the value to check.
     * @return {@code true} in such case.
     */
    public boolean isEmpty(@Nullable final String value)
    {
        return (value == null) || ("".equals(value.trim()));
    }

    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class StringUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        @NotNull public static final StringUtils SINGLETON = new StringUtils();
    }

    /**
     * Compiler instance.
     */
    private static Compiler m__Compiler;

    /**
     * The subpackage pattern.
     */
    private static Pattern m__SubPackagePattern;

    /**
     * The package pattern.
     */
    private static Pattern m__PackagePattern;

    /**
     * The justify pattern.
     */
    private static Pattern m__JustifyPattern;

    /**
     * The uncapitalize pattern.
     */
    private static Pattern m__UnCapitalizePattern;

    /**
     * Retrieves a StringUtils instance.
     * @return such instance.
     */
    @NotNull
    public static StringUtils getInstance()
    {
        @NotNull final StringUtils result = StringUtilsSingletonContainer.SINGLETON;

        synchronized  (StringUtils.class)
        {
            initialize();
        }

        return result;
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected StringUtils() {}

    /**
     * Specifies the regexp compiler.
     * @param compiler the compiler.
     */
    protected static final void immutableSetCompiler(@NotNull final Compiler compiler)
    {
        m__Compiler = compiler;
    }

    /**
     * Specifies the regexp compiler.
     * @param compiler the compiler.
     */
    @SuppressWarnings("unused")
    protected static void setCompiler(@NotNull final Compiler compiler)
    {
        immutableSetCompiler(compiler);
    }

    /**
     * Retrieves the regexp compiler.
     * @return such compiler.
     */
    @Nullable
    protected static final Compiler immutableGetCompiler()
    {
        return m__Compiler;
    }

    /**
     * Retrieves the regexp compiler.
     * @return such compiler.
     */
    @NotNull
    protected static Compiler getCompiler()
    {
        @Nullable Compiler result = immutableGetCompiler();

        if (result == null)
        {
            initialize();
            result = immutableGetCompiler();
        }

        return result;
    }

    /**
     * Initializes the regex engine.
     */
    protected static void initialize()
    {
        @Nullable RuntimeException t_Exception = null;

        @Nullable Compiler t_Compiler = immutableGetCompiler();

        if  (t_Compiler == null)
        {
            try
            {
                t_Compiler = createCompiler(RegexpManager.getInstance());

                immutableSetCompiler(t_Compiler);

                try
                {
                    immutableSetSubPackagePattern(
                        t_Compiler.compile("(.*)\\.(.*)"));
                }
                catch  (final MalformedPatternException exception)
                {
                            /*
                             * This should never happen. It's a compile-time
                             * error not detected by the compiler, but it's
                             * nothing dynamic. So, if it fails, fix it once
                             * and forget.
                             */
                    LogFactory.getLog(StringUtils.class).error(
                        Literals.INVALID_SUB_PACKAGE_PATTERN, exception);

                    t_Exception = exception;
                }

                if  (t_Exception == null)
                {
                    try
                    {
                        immutableSetPackagePattern(
                            t_Compiler.compile("(.*?)\\.(.*)"));
                    }
                    catch  (final MalformedPatternException exception)
                    {
                                /*
                                 * This should never happen. It's a compile-time
                                 * error not detected by the compiler, but it's
                                 * nothing dynamic. So, if it fails, fix it once
                                 * and forget.
                                 */
                        LogFactory.getLog(StringUtils.class).error(
                            "Invalid package pattern", exception);

                        t_Exception = exception;
                    }
                }

                if  (t_Exception == null)
                {
                    try
                    {
                        immutableSetJustifyPattern(
                            t_Compiler.compile("(.*?)\\s+(.*)"));
                    }
                    catch  (final MalformedPatternException exception)
                    {
                                /*
                                 * This should never happen. It's a compile-time
                                 * error not detected by the compiler, but it's
                                 * nothing dynamic. So, if it fails, fix it once
                                 * and forget.
                                 */
                        LogFactory.getLog(StringUtils.class).error(
                            "Invalid justify pattern", exception);

                        t_Exception = exception;
                    }
                }

                if  (t_Exception == null)
                {
                    try
                    {
                        final boolean t_bCaseSensitive = t_Compiler.isCaseSensitive();
                        t_Compiler.setCaseSensitive(true);
                        immutableSetUnCapitalizePattern(
                            t_Compiler.compile("\\s*([^A-Z\\s]*)\\s*([A-Z]?)?(.*)"));
                        t_Compiler.setCaseSensitive(t_bCaseSensitive);
                    }
                    catch  (final MalformedPatternException exception)
                    {
                                /*
                                 * This should never happen. It's a compile-time
                                 * error not detected by the compiler, but it's
                                 * nothing dynamic. So, if it fails, fix it once
                                 * and forget.
                                 */
                        LogFactory.getLog(StringUtils.class).error(
                            "Invalid un-capitalize pattern", exception);

                        t_Exception = exception;
                    }
                }
                else
                {
                    LogFactory.getLog(StringUtils.class).error(
                        "compiler unavailable");
                }
            }
            catch  (final RegexpEngineNotFoundException exception)
            {
                LogFactory.getLog(StringUtils.class).error(
                    Literals.NO_REGEXP_ENGINE_FOUND, exception);

                t_Exception = exception;
            }
            catch  (final Throwable throwable)
            {
                LogFactory.getLog(StringUtils.class).fatal(
                    Literals.UNKNOWN_ERROR, throwable);

                t_Exception = new RuntimeException(Literals.COULD_NOT_INITIALIZE_STRING_UTILS, throwable);
            }

            if (t_Exception != null)
            {
                throw t_Exception;
            }
        }
    }

    /**
     * Specifies the subpackage pattern.
     * @param pattern the pattern.
     */
    protected static final void immutableSetSubPackagePattern(@NotNull final Pattern pattern)
    {
        m__SubPackagePattern = pattern;
    }

    /**
     * Specifies the subpackage pattern.
     * @param pattern the pattern.
     */
    @SuppressWarnings("unused")
    protected static void setSubPackagePattern(@NotNull final Pattern pattern)
    {
        immutableSetSubPackagePattern(pattern);
    }

    /**
     * Retrieves the subpackage pattern.
     * @return such pattern.
     */
    @NotNull
    public Pattern getSubPackagePattern()
    {
        return m__SubPackagePattern;
    }

    /**
     * Specifies the package pattern.
     * @param pattern the pattern.
     */
    protected static final void immutableSetPackagePattern(@NotNull final Pattern pattern)
    {
        m__PackagePattern = pattern;
    }

    /**
     * Specifies the package pattern.
     * @param pattern the pattern.
     */
    @SuppressWarnings("unused")
    protected static void setPackagePattern(@NotNull final Pattern pattern)
    {
        immutableSetPackagePattern(pattern);
    }

    /**
     * Retrieves the package pattern.
     * @return such pattern.
     */
    @NotNull
    public Pattern getPackagePattern()
    {
        return m__PackagePattern;
    }

    /**
     * Specifies the justify pattern.
     * @param pattern the pattern.
     */
    protected static final void immutableSetJustifyPattern(@NotNull final Pattern pattern)
    {
        m__JustifyPattern = pattern;
    }

    /**
     * Specifies the justify pattern.
     * @param pattern the pattern.
     */
    @SuppressWarnings("unused")
    protected static void setJustifyPattern(@NotNull final Pattern pattern)
    {
        immutableSetJustifyPattern(pattern);
    }

    /**
     * Retrieves the justify pattern.
     * @return such pattern.
     */
    @NotNull
    public Pattern getJustifyPattern()
    {
        return m__JustifyPattern;
    }

    /**
     * Specifies the uncapitalize pattern.
     * @param pattern the pattern.
     */
    protected static final void immutableSetUnCapitalizePattern(@NotNull final Pattern pattern)
    {
        m__UnCapitalizePattern = pattern;
    }

    /**
     * Specifies the uncapitalize pattern.
     * @param pattern the pattern.
     */
    @SuppressWarnings("unused")
    protected static void setUnCapitalizePattern(@NotNull final Pattern pattern)
    {
        immutableSetUnCapitalizePattern(pattern);
    }

    /**
     * Retrieves the uncapitalize pattern.
     * @return such pattern.
     */
    @NotNull
    public Pattern getUnCapitalizePattern()
    {
        return m__UnCapitalizePattern;
    }

    /**
     * Removes all duplicates of specified char in given text.
     * @param original the text to parse.
     * @param lookfor the char to remove duplicates.
     * @return the updated text.
     */
    @SuppressWarnings("unused")
    public String removeDuplicate(
        @NotNull final String original, final char lookfor)
    {
        return
            removeDuplicate(original, lookfor, StringValidator.getInstance());
    }

    /**
     * Removes all duplicates of specified char in given text.
     * @param original the text to parse.
     * @param lookfor the char to remove duplicates.
     * @param stringValidator the StringValidator instance.
     * @return the updated text.
     */
    @SuppressWarnings("unused")
    @NotNull
    protected String removeDuplicate(
        @NotNull final String original,
        final char lookfor,
        @NotNull final StringValidator stringValidator)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        if  (!stringValidator.isEmpty(original))
        {
            @NotNull final String t_strLookFor = new String(new char[]{ lookfor });

            @NotNull final StringTokenizer t_strTokenizer =
                new StringTokenizer(original, t_strLookFor, false);

            if  (!t_strTokenizer.hasMoreTokens())
            {
                result.append(original);
            }

            while  (t_strTokenizer.hasMoreTokens())
            {
                result.append(t_strTokenizer.nextToken());

                if  (t_strTokenizer.hasMoreTokens())
                {
                    result.append(t_strLookFor);
                }
            }
        }
        
        return result.toString();
    }

    /**
     * Replaces a sequence with another inside a text content.
     * @param content the text content to update.
     * @param original the text to replace.
     * @param replacement the replacement.
     * @return the updated version of the original text.
     */
    @NotNull
    public String replace(
        @NotNull final String content,
        @NotNull final String original,
        @NotNull final String replacement)
    {
        return
            replaceRegexp(
                content,
                escapeRegexp(original),
                replacement);
    }

    /**
     * Escapes given string to avoid conflicts with regexp patterns.
     * @param text the text to escape.
     * @return the escaped text.
     */
    @NotNull
    public String escapeRegexp(@NotNull final String text)
    {
        @NotNull String result = text;

        result = replaceRegexp(result, escapeRegexpChar('.'), escapeChar('.'));
        result = replaceRegexp(result, escapeRegexpChar('*'), escapeChar('*'));
        result = replaceRegexp(result, escapeRegexpChar('?'), escapeChar('?'));
        result = replaceRegexp(result, escapeRegexpChar('('), escapeChar('('));
        result = replaceRegexp(result, escapeRegexpChar(')'), escapeChar(')'));
        result = replaceRegexp(result, escapeRegexpChar('^'), escapeChar('^'));
        result = replaceRegexp(result, escapeRegexpChar('$'), escapeChar('$'));

        return result;
    }

    /**
     * Builds the escaped version of given character.
     * @param character the character to escape.
     * @return the escaped version.
     */
    @NotNull
    protected String escapeRegexpChar(@NotNull final String character)
    {
        return "\\" + character;
    }

    /**
     * Builds the escaped version of given character.
     * @param character the character to escape.
     * @return the escaped version.
     */
    @NotNull
    protected String escapeRegexpChar(final char character)
    {
        return escapeRegexpChar("" + character);
    }

    /**
     * Builds the escaped version of given character.
     * @param character the character to escape.
     * @return the escaped version.
     */
    @NotNull
    @SuppressWarnings("unused")
    protected String escapeChar(@NotNull final String character)
    {
        // The escaping mechanism is the same as for regexps.
        return escapeRegexpChar(character);
    }

    /**
     * Builds the escaped version of given character.
     * @param character the character to escape.
     * @return the escaped version.
     */
    @NotNull
    protected String escapeChar(final char character)
    {
        // The escaping mechanism is the same as for regexps.
        return escapeRegexpChar(character);
    }

    /**
     * Replaces a sequence with another inside a text content.
     * @param content the text content to update.
     * @param original the text to replace.
     * @param replacement the replacement.
     * @return the updated version of the original text.
     */
    @NotNull
    protected String replaceRegexp(
        @NotNull final String content,
        @NotNull final String original,
        @NotNull final String replacement)
    {
        return
            replaceRegexp(
                content,
                original,
                replacement,
                createHelper(RegexpManager.getInstance()));
    }

    /**
     * Replaces a sequence with another inside a text content.
     * @param content the text content to update.
     * @param original the text to replace.
     * @param replacement the replacement.
     * @param helper the helper.
     * @return the updated version of the original text.
     */
    @NotNull
    protected String replaceRegexp(
        @NotNull final String content,
        @NotNull final String original,
        @NotNull final String replacement,
        @Nullable final Helper helper)
    {
        @NotNull final String result;

        if (helper != null)
        {
            result = helper.replaceAll(content, original, replacement);
        }
        else
        {
            result = content.replace(original, replacement);
        }

        return result;
    }

    /**
     * Puts a quote before and after given string.
     * @param text the content to quote.
     * @return the quoted version of such content.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String quote(@NotNull final String text)
    {
        return quote(text, '\"');
    }

    /**
     * Puts a quote before and after given string.
     * @param text the content to quote.
     * @param quote the quote.
     * @return the quoted version of such content.
     */
    @NotNull
    public String quote(@NotNull final String text, final char quote)
    {
        return quote(text, quote, StringValidator.getInstance());
    }

    /**
     * Puts a quote before and after given string.
     * @param text the content to quote.
     * @param quote the quote.
     * @param stringValidator the StringValidator instance.
     * @return the quoted version of such content.
     */
    @NotNull
    public String quote(
        @NotNull final String text,
        final char quote,
        @NotNull final StringValidator stringValidator)
    {
        @NotNull String result = text;

        boolean t_bProcessed = true;

        if  (!stringValidator.isEmpty(text))
        {
            if  (text.charAt(0) != quote)
            {
                if  (text.charAt(0) != quote)
                {
                    result = quote + text + quote;
                }
                else
                {
                    if  (text.trim().length() > 0)
                    {
                        result =
                              quote
                            + text.substring(1, text.length() - 1)
                            + quote;
                    }
                    else
                    {
                        t_bProcessed = false;
                    }
                }
            }
            else
            {
                result = text;
            }
        }

        if  (!t_bProcessed)
        {
            result = "" + quote + quote;
        }

        return result;
    }

    /**
     * Removes all quotes at the beginning and ending of given string.
     * @param text the content to unquote.
     * @return the unquoted version of such content.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String unquote(@NotNull final String text)
    {
        return unquote(text, '\'');
    }

    /**
     * Removes all quotes at the beginning and ending of given string.
     * @param text the content to unquote.
     * @param quote the quote.
     * @return the unquoted version of such content.
     */
    @NotNull
    public String unquote(@NotNull final String text, final char quote)
    {
        return unquote(text, quote, StringValidator.getInstance());
    }

    /**
     * Removes all quotes at the beginning and ending of given string.
     * @param text the content to unquote.
     * @param quote the quote.
     * @param stringValidator the StringValidator instance.
     * @return the unquoted version of such content.
     */
    @NotNull
    public String unquote(
        @NotNull final String text,
        final char quote,
        @NotNull final StringValidator stringValidator)
    {
        @NotNull String result = text;

        if  (!stringValidator.isEmpty(text))
        {
            if  (text.charAt(0) != quote)
            {
                if  (text.charAt(0) != quote)
                {
                    result = text;
                }
                else
                {
                    if  (text.trim().length() > 0)
                    {
                        result =
                            text.substring(1, text.length() - 1);
                    }
                }
            }
            else
            {
                result =
                    text.substring(1, text.length() - 1);
            }
        }

        return result;
    }

    /**
     * Normalizes given value, meaning the <i>removal</i> of all
     * non-alphanumeric characters but given separator.
     * Note: the separator will be passed directly to the regexp engine,
     * so it's your task to ensure its value doesn't conflict with a
     * regexp and produce unexpected results.
     * @param value the value to normalize.
     * @return the normalized version.
     */
    @NotNull
    public String softNormalize(@NotNull final String value)
    {
        return softNormalize(value, "_");
    }

    /**
     * Normalizes given value, meaning the <i>removal</i> of all
     * non-alphanumeric characters but given separator.
     * Note: the separator will be passed directly to the regexp engine,
     * so it's your task to ensure its value doesn't conflict with a
     * regexp and produce unexpected results.
     * @param value the value to normalize.
     * @param separator the separator.
     * @return the normalized version.
     */
    @NotNull
    public String softNormalize(@NotNull final String value, final char separator)
    {
        return softNormalize(value, String.valueOf(separator));
    }

    /**
     * Normalizes given value, meaning the <i>removal</i> of all
     * non-alphanumeric characters but given separator.
     * Note: the separator will be passed directly to the regexp engine,
     * so it's your task to ensure its value doesn't conflict with a
     * regexp and produce unexpected results.
     * @param value the value to normalize.
     * @param separator the separator.
     * @return the normalized version.
     */
    @NotNull
    public String softNormalize(@NotNull final String value, final String separator)
    {
        return softNormalize(value, separator, DEFAULT_EXTRA_SEPARATORS);
    }

    /**
     * Normalizes given value, meaning the <i>removal</i> of all
     * non-alphanumeric characters but given separator.
     * Note: the separator will be passed directly to the regexp engine,
     * so it's your task to ensure its value doesn't conflict with a
     * regexp and produce unexpected results.
     * @param value the value to normalize.
     * @param separator the separator.
     * @param extraSeparators the additional tokens used to split words.
     * These are different from the separator only because the result
     * will use <i>separator</i> as delimiter even for tokens previously
     * separated by any of the <i>extraSeparators</i>.
     * @return the normalized version.
     */
    @NotNull
    public String softNormalize(
        @NotNull final String value,
        @NotNull final String separator,
        @NotNull final String[] extraSeparators)
    {
        @NotNull String result = value;

        try
        {
            @NotNull final Helper t_Helper = createHelper(RegexpManager.getInstance());

            result = t_Helper.replaceAll(result, "\\s+", separator);

            result = t_Helper.replaceAll(result, separator, SEPARATOR_TOKEN);

            final int t_iLength = extraSeparators.length;

            for  (int t_iIndex = 0; t_iIndex < t_iLength; t_iIndex++)
            {
                result = t_Helper.replaceAll(result, extraSeparators[t_iIndex], SEPARATOR_TOKEN);
            }

            result = t_Helper.replaceAll(result, "\\W", "");

            result = t_Helper.replaceAll(result, SEPARATOR_TOKEN, separator);
        }
        catch (final MalformedPatternException exception)
        {
            LogFactory.getLog(StringUtils.class).fatal(
                Literals.MALFORMED_PATTERN,
                exception);
        }
        catch (final RegexpEngineNotFoundException exception)
        {
            LogFactory.getLog(StringUtils.class).fatal(
                Literals.CANNOT_FIND_ANY_REGEXP_ENGINE,
                exception);
        }

        return result;
    }

    /**
     * Normalizes given value, whose words are defined by a concrete char
     * separator. In this situation, <i>normalization</i> means
     * <i>capitalization</i> and <i>removal</i> of all non-alphanumeric
     * characters.
     * @param value the value to normalize.
     * @param locale the locale.
     * @return the normalized version.
     */
    @NotNull
    public String normalize(@NotNull final String value, @NotNull final Locale locale)
    {
        return
            softNormalize(
                capitalize(value, locale, DEFAULT_SEPARATOR), DEFAULT_SEPARATOR);
    }

    /**
     * Normalizes given value, whose words are defined by a concrete char
     * separator. In this situation, <i>normalization</i> means
     * <i>capitalization</i> and <i>removal</i> of all non-alphanumeric
     * characters.
     * @param value the value to normalize.
     * @param locale the locale.
     * @param separator the word separator.
     * @return the normalized version.
     */
    @NotNull
    public String normalize(@NotNull final String value, final Locale locale, final char separator)
    {
        return
            softNormalize(
                capitalize(value, locale, separator), "" + separator);
    }

    /**
     * Capitalizes the words contained in given string, using a concrete char
     * separator. For instance,
     * <code>capitalize("asd-efg", '-').equals("AsdEfg")</code>.
     * @param text the text to process.
     * @param locale the locale.
     * @return the capitalized string.
     */
    @NotNull
    public String capitalize(@NotNull final String text, final Locale locale)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        String aux;
        try
        {
            aux = NON_ALPHANUMERIC_REGEXP.matcher(text).replaceAll(String.valueOf(DEFAULT_SEPARATOR));

            aux = SEPARATOR_REGEXP.matcher(aux).replaceAll(String.valueOf(DEFAULT_SEPARATOR));

            @NotNull java.util.regex.Matcher t_Matcher = SEPARATOR_PLUS_LOWERCASE_REGEXP.matcher(aux);

            if (t_Matcher.matches())
            {
                String rest;

                do
                {
                    final StringBuilder t_sbAux = new StringBuilder();

                    final String t_strFirstGroup = t_Matcher.group(1);

                    final String t_strFirst;

                    if (t_strFirstGroup.equals(t_strFirstGroup.toUpperCase(locale)))
                    {
                        t_strFirst = t_strFirstGroup.toLowerCase(locale);
                    }
                    else
                    {
                        t_strFirst = t_strFirstGroup;
                    }
                    t_sbAux.append(t_strFirst);
                    t_sbAux.append(t_Matcher.group(2).toUpperCase(locale));

                    result.append(t_sbAux.toString());

                    rest = t_Matcher.group(3);

                    t_Matcher = SEPARATOR_PLUS_LOWERCASE_REGEXP.matcher(rest);
                }
                while (t_Matcher.matches());

                if (rest != null)
                {
                    result.append(rest.toLowerCase(locale));
                }
            }
            else if (aux.equals(aux.toUpperCase(locale)))
            {
                result.append(aux.toLowerCase(locale));
            }
            else
            {
                result.append(aux);
            }
        }
        catch (final MalformedPatternException exception)
        {
            LogFactory.getLog(StringUtils.class).fatal(
                Literals.MALFORMED_PATTERN, exception);
        }
        catch (final RegexpEngineNotFoundException exception)
        {
            LogFactory.getLog(StringUtils.class).fatal(
                Literals.CANNOT_FIND_ANY_REGEXP_ENGINE, exception);
        }

        return capitalizeFirst(result.toString(), locale);
    }

    /**
     * Capitalizes the first letter of given text.
     * @param text the text.
     * @param locale the locale.
     * @return the capitalized version.
     */
    @NotNull
    public String capitalizeFirst(@NotNull final String text, @NotNull final Locale locale)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        if (text.length() > 0)
        {
            result.append(String.valueOf(text.charAt(0)).toUpperCase(locale));

            if (text.length() > 1)
            {
                result.append(text.substring(1));
            }
        }

        return result.toString();
    }

    /**
     * Capitalizes the words contained in given string, using a concrete char
     * separator. For instance,
     * <code>capitalize("asd-efg", '-').equals("AsdEfg")</code>.
     * @param text the text to process.
     * @param locale the locale.
     * @param separator the word separator.
     * @return the capitalized string.
     */
    @NotNull
    public String capitalize(@NotNull final String text, final Locale locale, final char separator)
    {
        @NotNull final StringBuilder t_sbResult = new StringBuilder(text.length());

        @NotNull final String aux = text; //.toLowerCase(locale);

        boolean t_bToUpper = true;

        for  (int t_iIndex = 0; t_iIndex < aux.length(); t_iIndex++)
        {
            if (aux.charAt(t_iIndex) == separator)
            {
                t_bToUpper = true;
                continue;
            }

            if (t_bToUpper)
            {
                t_sbResult.append(
                    Character.toUpperCase(aux.charAt(t_iIndex)));

                t_bToUpper = false;
                continue;
            }

            t_sbResult.append(String.valueOf(aux.charAt(t_iIndex)));
        }

        return t_sbResult.toString();
    }

    /**
     * Normalizes the words contained in given string, using a concrete char
     * separator. The first letter is always in lower case.
     * @param text the text to process.
     * @param locale the locale.
     * @param separator the word separator.
     * @return the processed string.
     */
    @NotNull
    public String toJavaMethod(@NotNull final String text, final Locale locale, final char separator)
    {
        @NotNull final StringBuilder t_sbResult = new StringBuilder();

        @NotNull final String t_strText = normalize(text, locale, separator);

        if  (t_strText.length() > 0)
        {
            t_sbResult.append(t_strText.substring(0,1).toLowerCase());
            t_sbResult.append(t_strText.substring(1));
        }
        else
        {
            t_sbResult.append(t_strText.toLowerCase());
        }

        return t_sbResult.toString();
    }

    /**
     * Capitalizes the contents, using given separator, and word list.
     * This is the same as, replacing all words from the list in the
     * input to its capitalized versions, and finally
     * applying <code>toJavaMethod(text, separator)</code> to the result.
     * @param text the text.
     * @param separator the separator.
     * @param words the predefined words.
     * @return the token collection.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String toJavaMethod(
        @NotNull final String text, final char separator, @NotNull final String[] words)
    {
        return toJavaMethod(text, DEFAULT_LOCALE, separator, words);
    }

    /**
     * Capitalizes the contents, using given separator, and word list.
     * This is the same as, replacing all words from the list in the
     * input to its capitalized versions, and finally
     * applying <code>toJavaMethod(text, separator)</code> to the result.
     * @param text the text.
     * @param locale the locale.
     * @param separator the separator.
     * @param words the predefined words.
     * @return the token collection.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String toJavaMethod(
        @NotNull final String text, final Locale locale, final char separator, @NotNull final String[] words)
    {
        @NotNull String result = text;

        try
        {
            @NotNull final Helper t_Helper = createHelper(RegexpManager.getInstance());

            for  (int t_iIndex = 0; t_iIndex < words.length; t_iIndex++)
            {
                result =
                    t_Helper.replaceAll(
                        result,
                        words[t_iIndex],
                        capitalize(words[t_iIndex], DEFAULT_LOCALE, separator));
            }

            result = toJavaMethod(result, DEFAULT_LOCALE, separator);
        }
        catch  (final MalformedPatternException exception)
        {
            /*
             * This exception is about pattern mismatch. In my opinion,
             * it's an error that should be detected at compile time,
             * but regexp API design cannot provide this functionality,
             * since all patterns are defined with strings, and therefore
             * they escape all compiler checks.
             */
            LogFactory.getLog(StringUtils.class).warn(
                Literals.MALFORMED_STATIC_PATTERNS_ARE_FATAL_CODING_ERRORS,
                exception);
        }
        catch  (final RegexpEngineNotFoundException exception)
        {
            /*
             * This exception is thrown only if no regexp library is available
             * at runtime. Not only this one, but any method provided by this
             * class that use regexps will not work.
             */
            LogFactory.getLog(StringUtils.class).fatal(
                Literals.CANNOT_FIND_ANY_REGEXP_ENGINE,
                exception);
        }
        
        return result;
    }

    /**
     * Retrieves all tokens found in given string, using given separator.
     * @param text the text.
     * @param separator the separator.
     * @return the token collection.
     */
    @NotNull
    public Collection<String> tokenize(@NotNull final String text, @NotNull final String separator)
    {
        return
            tokenize(
                text,
                separator,
                StringValidator.getInstance(),
                getCompiler());
    }

    /**
     * Retrieves all tokens found in given string, using given separator.
     * @param text the text.
     * @param separator the separator.
     * @param stringValidator the StringValidator instance.
     * @param compiler the compiler.
     * @return the token collection.
     */
    @NotNull
    protected Collection<String> tokenize(
        @NotNull final String text,
        @NotNull final String separator,
        @NotNull final StringValidator stringValidator,
        @NotNull final Compiler compiler)
    {
        @NotNull final List<String> result = new ArrayList<>();

        String t_strText = text;

        try
        {
            @NotNull final Matcher t_Matcher = createMatcher(RegexpManager.getInstance());

            @Nullable MatchResult t_MatchResult;

            @NotNull final Pattern t_Pattern =
                compiler.compile("(.*?)" + separator + "(.*)");

            while  (   (t_strText != null)
                    && (!stringValidator.isEmpty(t_strText))
                    && (t_Matcher.contains(t_strText, t_Pattern)))
            {
                t_MatchResult = t_Matcher.getMatch();

                if (t_MatchResult != null)
                {
                    result.add(t_MatchResult.group(1));

                    // the rest is parsed next.
                    t_strText = t_MatchResult.group(2);
                }
                else
                {
                    break;
                }
            }

            if  (!stringValidator.isEmpty(t_strText))
            {
                result.add(t_strText);
            }
        }
        catch  (final MalformedPatternException exception)
        {
            LogFactory.getLog(StringUtils.class).error(
                Literals.MALFORMED_PATTERN_POSSIBLY_DUE_TO_QUOTE_SYMBOL_CONFLICT,
                exception);
        }
        catch  (final RegexpEngineNotFoundException exception)
        {
            /*
             * This exception is thrown only if no regexp library is available
             * at runtime. Not only this one, but any method provided by this
             * class that use regexps will not work.
             */
            LogFactory.getLog(StringUtils.class).error(
                Literals.CANNOT_FIND_ANY_REGEXP_ENGINE, exception);
        }
        
        return result;
    }

    /**
     * Extracts the most specific package name from given text.
     * @param text the text.
     * @return the leaf package name.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String extractPackageName(@NotNull final String text)
    {
        return capitalize(extractPackageGroup(text, 2), DEFAULT_LOCALE, DEFAULT_SEPARATOR);
    }

    /**
     * Extracts the class name from given fully-qualified class identifier.
     * @param text the text.
     * @return the class name.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String extractClassName(@NotNull final String text)
    {
        return extractPackageGroup(text, 1);
    }

    /**
     * Extracts a concrete group in sub-package regexp from given text.
     * @param text the text.
     * @param group the group.
     * @return the selected group.
     */
    @NotNull
    protected String extractPackageGroup(
        @NotNull final String text, final int group)
    {
        @Nullable String result = text;

        try
        {
            @NotNull final Matcher t_Matcher = createMatcher(RegexpManager.getInstance());

            @Nullable final MatchResult t_MatchResult;

            @NotNull final Pattern t_SubPackagePattern = getSubPackagePattern();

            if  (   (result.trim().length() > 0)
                 && (t_Matcher.contains(result, t_SubPackagePattern)))
            {
                t_MatchResult = t_Matcher.getMatch();

                if (t_MatchResult != null)
                {
                    // the rest is parsed next.
                    result = t_MatchResult.group(group);
                }
            }
        }
        catch  (final RegexpEngineNotFoundException exception)
        {
            /*
             * This exception is thrown only if no regexp library is available
             * at runtime. Not only this one, but any method provided by this
             * class that use regexps will not work.
             */
            LogFactory.getLog(StringUtils.class).fatal(
                Literals.CANNOT_FIND_ANY_REGEXP_ENGINE, exception);
        }

        if (result == null)
        {
            result = text;
        }

        return result;
    }

    /**
     * Translates given package name to a relative file path.
     * @param packageName the pacjage name.
     * @return such path
     */
    @NotNull
    public String packageToFilePath(@NotNull final String packageName)
    {
        return packageToFilePath(packageName, StringValidator.getInstance());
    }

    /**
     * Translates given package name to a relative file path.
     * @param packageName the pacjage name.
     * @param stringValidator the StringValidator instance.
     * @return such path.
     */
    @NotNull
    protected String packageToFilePath(
        @NotNull final String packageName, @NotNull final StringValidator stringValidator)
    {
        @NotNull final StringBuilder t_sbResult = new StringBuilder();

        try
        {
            @Nullable String t_strPackageName = packageName;

            @NotNull final Matcher t_Matcher = createMatcher(RegexpManager.getInstance());

            @Nullable MatchResult t_MatchResult;

            boolean t_bMatched = false;

            @NotNull final Pattern t_PackagePattern = getPackagePattern();

            while  (   (!stringValidator.isEmpty(t_strPackageName))
                    && (t_strPackageName != null)
                    && (t_Matcher.contains(t_strPackageName, t_PackagePattern)))
            {
                t_MatchResult = t_Matcher.getMatch();

                if (t_MatchResult != null)
                {
                    @Nullable final String t_strSubPattern = t_MatchResult.group(1);

                    if  (   (!stringValidator.isEmpty(t_strSubPattern))
                         && (t_strSubPattern != null)
                         && (!".".equals(t_strSubPattern)))
                    {
                        if  (t_bMatched)
                        {
                            t_sbResult.append(File.separator);
                        }

                        t_sbResult.append(t_strSubPattern);

                        t_bMatched = true;
                    }

                    // the rest is parsed next.
                    t_strPackageName = t_MatchResult.group(2);
                }
            }

            if  (   (!stringValidator.isEmpty(t_strPackageName))
                 && (!".".equals(t_strPackageName)))
            {
                if  (t_bMatched)
                {
                    t_sbResult.append(File.separator);
                }

                t_sbResult.append(t_strPackageName);
            }
        }
        catch  (final RegexpEngineNotFoundException exception)
        {
            /*
             * This exception is thrown only if no regexp library is available
             * at runtime. Not only this one, but any method provided by this
             * class that use regexps will not work.
             */
            LogFactory.getLog(StringUtils.class).fatal(
                Literals.CANNOT_FIND_ANY_REGEXP_ENGINE, exception);
        }

        return t_sbResult.toString();
    }

    /**
     * Justifies given text to avoid exceeding specified margin,
     * if possible.
     * @param text the text to justify.
     * @param margin the margin.
     * @return the justified text.
     */
    @NotNull
    public String justify(@NotNull final String text, final int margin)
    {
        return justify(text, "", margin);
    }

    /**
     * Justifies given text to avoid exceeding specified margin,
     * if possible.
     * @param text the text to justify.
     * @param linePrefix the prefix to add to all justified lines.
     * @param margin the margin.
     * @return the justified text.
     */
    @NotNull
    public String justify(
        @NotNull final String text, @NotNull final String linePrefix, final int margin)
    {
        return
            justify(text, linePrefix, margin, StringValidator.getInstance());
    }

    /**
     * Justifies given text to avoid exceeding specified margin,
     * if possible.
     * @param text the text to justify.
     * @param linePrefix the prefix to add to all justified lines.
     * @param margin the margin.
     * @param stringValidator the StringValidator instance.
     * @return the justified text.
     */
    @NotNull
    protected String justify(
        @NotNull final String text,
        @NotNull final String linePrefix,
        final int margin,
        @NotNull final StringValidator stringValidator)
    {
        @NotNull final StringBuilder t_sbResult = new StringBuilder();

        @Nullable String t_strText = text;

        try
        {
            @NotNull final String t_strLinePrefix = linePrefix;

            @NotNull final Matcher t_Matcher = createMatcher(RegexpManager.getInstance());

            @Nullable MatchResult t_MatchResult;

            @NotNull final Pattern t_JustifyPattern = getJustifyPattern();

            String t_strCurrentLine = "";

            @Nullable String t_strCurrentWord;

            while  (   (!stringValidator.isEmpty(text))
                    && (t_strText != null)
                    && (t_Matcher.contains(t_strText, t_JustifyPattern)))
            {
                t_MatchResult = t_Matcher.getMatch();

                if  (t_MatchResult != null)
                {
                    t_strCurrentWord = t_MatchResult.group(1);

                    if  (t_strCurrentWord != null)
                    {
                        if  (  t_strCurrentLine.length()
                             + t_strCurrentWord.length()
                             + 1
                             > margin)
                        {
                            t_sbResult.append(t_strCurrentLine);

                            if  (t_strCurrentLine.length() > 0)
                            {
                                t_sbResult.append("\n");
                            }

                            t_strCurrentLine = t_strLinePrefix;
                        }
                        else
                        {
                            if  (t_strCurrentLine.length() > 0)
                            {
                                t_strCurrentLine += " ";
                            }
                        }

                        t_strCurrentLine += t_strCurrentWord;
                    }

                    // the rest is parsed next.
                    t_strText = t_MatchResult.group(2);
                }
            }

            if (t_strText != null)
            {
                t_strCurrentWord = t_strText;
            }
            else
            {
                t_strCurrentWord = "";
            }

            t_sbResult.append(t_strCurrentLine);

            if  (  t_strCurrentLine.length()
                 + t_strCurrentWord.length()
                 + 1
                 > margin)
            {
                if  (t_strCurrentLine.length() > 0)
                {
                    t_sbResult.append("\n");

                    t_sbResult.append(t_strLinePrefix);
                }

                if  (t_strCurrentWord.length() > margin)
                {
                    t_sbResult.append(t_strCurrentWord);
                    t_sbResult.append("\n");
                    t_sbResult.append(t_strLinePrefix);
                }
            }
            else
            {
                t_sbResult.append(" ");
            }

            t_sbResult.append(t_strCurrentWord);
        }
        catch  (final RegexpEngineNotFoundException exception)
        {
            /*
             * This exception is thrown only if no regexp library is available
             * at runtime. Not only this one, but any method provided by this
             * class that use regexps will not work.
             */
            LogFactory.getLog(StringUtils.class).fatal(
                Literals.CANNOT_FIND_ANY_REGEXP_ENGINE, exception);
        }

        return t_sbResult.toString();
    }

    /**
     * Uncapitalizes the beginning of given text.
     * @param input such input.
     * @return the processed input.
     */
    @NotNull
    public String unCapitalizeStart(@NotNull final String input)
    {
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    /**
     * Uncapitalizes given text, i.e. "thisTest"
     * to "this<code>[separator]</code>test".
     * @param input the input to process.
     * @param separator the separator.
     * @return the processed input.
     */
    @NotNull
    public String unCapitalize(@NotNull final String input, @NotNull final String separator)
    {
        return
            unCapitalize(
                input,
                separator,
                StringValidator.getInstance(),
                getUnCapitalizePattern(),
                createMatcher(RegexpManager.getInstance()));
    }

    /**
     * Uncapitalizes given text, i.e. "thisTest"
     * to "this<code>[separator]</code>test".
     * @param input the input to process.
     * @param separator the separator.
     * @param stringValidator the StringValidator instance.
     * @param pattern the regexp pattern to use.
     * @param matcher the matcher instance to use.
     * @return the processed input.
     */
    @NotNull
    protected String unCapitalize(
        @NotNull final String input,
        @NotNull final String separator,
        @NotNull final StringValidator stringValidator,
        @NotNull final Pattern pattern,
        @NotNull final Matcher matcher)
    {
        @NotNull final StringBuilder t_sbResult = new StringBuilder();

        try
        {
            @Nullable String t_strTextToProcess = input;

            @Nullable String t_strCurrentWord;

            @Nullable MatchResult t_MatchResult;

            while  (   (!stringValidator.isEmpty(t_strTextToProcess))
                    && (t_strTextToProcess != null)
                    && (matcher.contains(t_strTextToProcess, pattern)))
            {
                t_MatchResult = matcher.getMatch();

                if  (t_MatchResult != null)
                {
                    t_strCurrentWord = t_MatchResult.group(1);

                    if  (!stringValidator.isEmpty(t_strCurrentWord))
                    {
                        t_sbResult.append(t_strCurrentWord);
                    }

                    // the rest is parsed next.
                    t_strTextToProcess = t_MatchResult.group(3);

                    if  (!stringValidator.isEmpty(t_strTextToProcess))
                    {
                        t_sbResult.append(separator);
                    }

                    @Nullable final String t_strUpperCaseLetter = t_MatchResult.group(2);

                    if  (t_strUpperCaseLetter != null)
                    {
                        t_sbResult.append(t_strUpperCaseLetter.toLowerCase());
                    }
                }
            }

            if  (!stringValidator.isEmpty(t_strTextToProcess))
            {
                t_sbResult.append(t_strTextToProcess);
            }
        }
        catch  (final RegexpEngineNotFoundException exception)
        {
            /*
             * This exception is thrown only if no regexp library is available
             * at runtime. Not only this one, but any method provided by this
             * class that use regexps will not work.
             */
            LogFactory.getLog(StringUtils.class).fatal(
                Literals.CANNOT_FIND_ANY_REGEXP_ENGINE, exception);
        }

        return t_sbResult.toString();
    }

    /**
     * Adds given prefix and suffix to each line of given text.
     * <code>
     * applyToEachLine(" line 1   \n    and line 2", "||{0}{1}//").equals(
     *     "||line 1//\n||   and line 2//\n")
     * </code>
     * 0 - indentation spaces
     * 1 - trimmed text
     * @param text the text.
     * @param format the format.
     * @return the processed text.
     */
    @NotNull
    public String applyToEachLine(
        @NotNull final String text, @NotNull final String format)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        @NotNull final StringTokenizer t_StringTokenizer =
            new StringTokenizer(text, "\n", false);

        int t_iInitialIndent = retrieveMinimumIndentInAllLines(text);

        @NotNull final StringBuilder t_sbInitialIndent = new StringBuilder();

        for (int t_iIndex = 0; t_iIndex < t_iInitialIndent; t_iIndex++)
        {
            t_sbInitialIndent.append(" ");
        }

        @NotNull final String t_strInitialIndent = t_sbInitialIndent.toString();

        boolean t_bFirstLine = true;

        String t_strCurrentLine;
        String t_strTrimmedCurrentLine;

        @NotNull final MessageFormat t_Formatter = new MessageFormat(format);

        String t_strCurrentIndent = "";

        while  (t_StringTokenizer.hasMoreTokens())
        {
            t_strCurrentLine = t_StringTokenizer.nextToken();

            t_strTrimmedCurrentLine = t_strCurrentLine.trim();

            if  (!t_bFirstLine)
            {
                t_iInitialIndent =
                    t_strCurrentLine.indexOf(t_strTrimmedCurrentLine);

                if  (t_iInitialIndent > t_strInitialIndent.length())
                {
                    t_strCurrentIndent =
                          t_strCurrentLine.substring(
                              t_strInitialIndent.length(), t_iInitialIndent);
                }
                else
                {
                    t_strCurrentIndent = "";
                }
            }

            result.append(
                t_Formatter.format(
                    new Object[]
                    {
                        t_strCurrentIndent,
                        t_strTrimmedCurrentLine
                    }));

            result.append("\n");

            if  (t_bFirstLine)
            {
                t_bFirstLine = false;
            }
        }

        return result.toString();
    }

    /**
     * Finds out the minimum indent of all lines in given text.
     * <code>
     * retrieveMinimumIndentInAllLines(" line 1   \n    and line 2", "||" , "//") == 1
     * </code>
     * <code>
     * retrieveMinimumIndentInAllLines("    line 1   \n  .  and line 2", "||" , "//") == 2
     * </code>
     * @param text the text.
     * @return such information.
     */
    public int retrieveMinimumIndentInAllLines(@NotNull final String text)
    {
        int result = -1;

        @NotNull final StringTokenizer t_StringTokenizer =
            new StringTokenizer(text, "\n", false);

        int t_iInitialIndent;

        String t_strCurrentLine;
        String t_strTrimmedCurrentLine;

        while  (t_StringTokenizer.hasMoreTokens())
        {
            t_strCurrentLine = t_StringTokenizer.nextToken();

            t_strTrimmedCurrentLine = t_strCurrentLine.trim();

            t_iInitialIndent =
                t_strCurrentLine.indexOf(t_strTrimmedCurrentLine);

            if  (   (   (t_iInitialIndent > 0)
                     && (t_iInitialIndent < result))
                 || (result == -1))
            {
                result = t_iInitialIndent;
            }
        }

        if  (result == -1)
        {
            result = 0;
        }

        return result;
    }

    /**
     * Trims start and end lines, if they contain nothing but spaces.
     * @param text the text to trim.
     * @return the trimmed text.
     */
    @NotNull
    public String removeFirstAndLastBlankLines(@NotNull final String text)
    {
        return
            removeFirstAndLastBlankLines(text, StringValidator.getInstance());
    }

    /**
     * Trims start and end lines, if they contain nothing but spaces.
     * @param text the text to trim.
     * @param stringValidator the StringValidator instance.
     * @return the trimmed text.
     */
    @NotNull
    protected String removeFirstAndLastBlankLines(
        @NotNull final String text, @NotNull final StringValidator stringValidator)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        @NotNull final StringTokenizer t_StringTokenizer =
            new StringTokenizer(text, "\n", false);

        String t_strFirstLine = null;
        String t_strLastLine = "";
        String t_strCurrentLine = null;
        boolean t_bJustOneLine = false;

        while  (t_StringTokenizer.hasMoreTokens())
        {
            t_strCurrentLine = t_StringTokenizer.nextToken();

            if  (t_strFirstLine == null)
            {
                if  (!t_StringTokenizer.hasMoreTokens())
                {
                    t_bJustOneLine = true;
                }
                t_strFirstLine = t_strCurrentLine;
            }
            else if  (t_StringTokenizer.hasMoreTokens())
            {
                result.append(t_strCurrentLine);
                result.append("\n");
            }
        }

        if  (!t_bJustOneLine)
        {
            t_strLastLine = t_strCurrentLine;

            if  (   (t_strFirstLine != null)
                 && (stringValidator.isEmpty(t_strFirstLine)))
            {
                t_strFirstLine = t_strFirstLine.trim();
            }
            else
            {
                t_strFirstLine += "\n";
            }

            if  (   (t_strLastLine != null)
                 && (stringValidator.isEmpty(t_strLastLine)))
            {
                t_strLastLine = t_strLastLine.trim();
            }
            else if  (text.endsWith("\n"))
            {
                t_strLastLine += "\n";
            }
        }

        return t_strFirstLine + result + t_strLastLine;
    }

    /**
     * Retrieves the last word, using given separators.
     * @param phrase the phrase.
     * @param separators the separators.
     * @return the last word.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String retrieveLastWord(
        @NotNull final String phrase, @NotNull final String[] separators)
    {
        @NotNull String result = phrase;

        for  (int t_iSeparatorIndex = 0;
                  t_iSeparatorIndex < separators.length;
                  t_iSeparatorIndex++)
        {
            @NotNull final Collection<String> t_cTokens =
                tokenize(result, separators[t_iSeparatorIndex]);

            @NotNull final String[] t_astrTokens =
                t_cTokens.toArray(new String[t_cTokens.size()]);

            if  (t_astrTokens.length > 0)
            {
                result = t_astrTokens[t_astrTokens.length - 1];
            }
        }

        return result;
    }

    /**
     * Splits given value into multiple lines.
     * @param value the value.
     * @return such output.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String[] split(@NotNull final String value)
    {
        return
            split(
                value,
                new String[] { System.getProperty("line.separator"), "\n" });
    }
    
    /**
     * Splits given value into multiple lines.
     * @param value the value.
     * @param separators an ordered list of separators. The first non-null
     * will be the one used.
     * @return such output.
     */
    @NotNull
    public String[] split(@NotNull final String value, @NotNull final String[] separators)
    {
        @NotNull final Collection<String> t_cResult = new ArrayList<>();

        @Nullable String t_strSeparator = null;
        
        for  (@Nullable final String separator : separators)
        {
            if  (separator != null)
            {
                t_strSeparator = separator;
                break;
            }
        }

        if  (t_strSeparator != null)
        {
            @NotNull final StringTokenizer t_Tokenizer =
                new StringTokenizer(value.trim(), t_strSeparator, false);

            while  (t_Tokenizer.hasMoreTokens())
            {
                t_cResult.add(t_Tokenizer.nextToken());
            }
        }
        
        return t_cResult.toArray(new String[t_cResult.size()]);
    }

    /**
     * Surrounds given values using given separator.
     * @param values the values.
     * @param separator the separator.
     * @return the quoted values.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String[] surround(@NotNull final String[] values, @NotNull final String separator)
    {
        return surround(values, separator, separator);
    }

    /**
     * Surrounds given values using given separators.
     * @param values the values.
     * @param leftSeparator the left-side separator.
     * @param rightSeparator the right-side separator.
     * @return the quoted values.
     */
    @NotNull
    public String[] surround(
        @NotNull final String[] values,
        @NotNull final String leftSeparator,
        @NotNull final String rightSeparator)
    {
        @NotNull final String[] result;
        
        final int t_iLength = values.length;
        
        result = new String[t_iLength];
        
        for  (int t_iIndex = 0; t_iIndex < t_iLength; t_iIndex++)
        {
            result[t_iIndex] = values[t_iIndex];
            
            if  (result[t_iIndex] != null)
            {
                result[t_iIndex] =
                    leftSeparator + result[t_iIndex] + rightSeparator;
            }
        }
        
        return result;
    }

    /**
     * Trims given values.
     * @param values the values.
     * @return the trimmed lines.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String[] trim(@NotNull final String[] values)
    {
        @NotNull final Collection<String> t_cResult = new ArrayList<>(values.length);
        
        String t_strCurrentLine;
        
        for  (@Nullable String value : values)
        {
            if  (value != null)
            {
                value = value.trim();

                if  (value.length() > 0)
                {
                    t_cResult.add(value);
                }
            }
        }
        
        return t_cResult.toArray(new String[t_cResult.size()]);
    }

    /**
     * Escapes given char in a text.
     * @param text the text.
     * @param charToEscape the char to escape.
     * @return the processed value.
     */
    @NotNull
    public String escape(@NotNull final String text, final char charToEscape)
    {
        @NotNull final String t_strEscapedChar = escapeChar(charToEscape);

        return replace(text, t_strEscapedChar, t_strEscapedChar);
    }

    /**
     * Concatenates all elements of given collection using a separator.
     * @param items the collection.
     * @param separator the separator.
     * @return such concatenation.
     */
    @SuppressWarnings("unused")
    @NotNull
    public String concatenate(
        @NotNull final Collection<?> items, @NotNull final String separator)
    {
        @NotNull final StringBuilder t_sbResult = new StringBuilder();

        @NotNull final Iterator<?> t_itItems = items.iterator();

        if  (t_itItems.hasNext())
        {
            t_sbResult.append(t_itItems.next());
        }

        while  (t_itItems.hasNext())
        {
            t_sbResult.append(separator);
            t_sbResult.append(t_itItems.next());
        }

        return t_sbResult.toString();
    }

    /**
     * Creates the compiler.
     * @param regexpManager the <code>RegexpManager</code> instance.
     * @return the regexp compiler.
     * @throws RegexpEngineNotFoundException if no engine is available.
     */
    @NotNull
    protected static synchronized Compiler createCompiler(
        final RegexpManager regexpManager)
        throws RegexpEngineNotFoundException
    {
        return createCompiler(regexpManager.getEngine());
    }

    /**
     * Creates the compiler.
     * @param regexpEngine the RegexpEngine instance.
     * @return the regexp compiler.
     * @throws RegexpEngineNotFoundException if no engine is available.
     */
    @NotNull
    protected static synchronized Compiler createCompiler(
        @NotNull final RegexpEngine regexpEngine)
        throws RegexpEngineNotFoundException
    {
        return regexpEngine.createCompiler();
    }

    /**
     * Creates the matcher.
     * @param regexpManager the RegexpManager instance.
     * @return the regexp matcher.
     * @throws RegexpEngineNotFoundException if no engine is available.
     */
    @NotNull
    protected static synchronized Matcher createMatcher(
        @NotNull final RegexpManager regexpManager)
        throws RegexpEngineNotFoundException
    {
        return createMatcher(regexpManager.getEngine());
    }

    /**
     * Creates the matcher.
     * @param regexpEngine the RegexpEngine instance.
     * @return the regexp matcher.
     * @throws RegexpEngineNotFoundException if no engine is available.
     */
    @NotNull
    protected static synchronized Matcher createMatcher(
        @NotNull final RegexpEngine regexpEngine)
        throws RegexpEngineNotFoundException
    {
        return regexpEngine.createMatcher();
    }

    /**
     * Creates the helper.
     * @param regexpManager the RegexpManager instance.
     * @return the regexp helper.
     * @throws RegexpEngineNotFoundException if no engine is available.
     */
    @NotNull
    protected static synchronized Helper createHelper(
        @NotNull final RegexpManager regexpManager)
        throws RegexpEngineNotFoundException
    {
        return createHelper(regexpManager.getEngine());
    }

    /**
     * Creates the helper.
     * @param regexpEngine the RegexpEngine instance.
     * @return the regexp helper.
     * @throws RegexpEngineNotFoundException if no engine is available.
     */
    @NotNull
    protected static synchronized Helper createHelper(
        @NotNull final RegexpEngine regexpEngine)
        throws RegexpEngineNotFoundException
    {
        return regexpEngine.createHelper();
    }
}
