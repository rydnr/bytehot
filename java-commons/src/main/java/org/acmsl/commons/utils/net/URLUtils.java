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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA


    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: URLUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some useful methods when working with URLs.
 *
 */
package org.acmsl.commons.utils.net;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.Literals;
import org.acmsl.commons.patterns.Singleton;
import org.acmsl.commons.patterns.Utils;
import org.acmsl.commons.regexpplugin.Compiler;
import org.acmsl.commons.regexpplugin.Matcher;
import org.acmsl.commons.regexpplugin.MatchResult;
import org.acmsl.commons.regexpplugin.MalformedPatternException;
import org.acmsl.commons.regexpplugin.Pattern;
import org.acmsl.commons.regexpplugin.RegexpEngine;
import org.acmsl.commons.regexpplugin.RegexpEngineNotFoundException;
import org.acmsl.commons.regexpplugin.RegexpManager;
import org.acmsl.commons.regexpplugin.RegexpPluginMisconfiguredException;
import org.acmsl.commons.utils.regexp.RegexpUtils;

/*
 * Importing Commons-Logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides some useful methods when working with URLs.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class URLUtils
    implements  Utils,
                Singleton
{
    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class URLUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        @NotNull public static final URLUtils SINGLETON = new URLUtils();
    }

    /**
     * Question mark pattern.
     */
    private volatile static Pattern m__QuestionMarkPattern;

    /**
     * Block pattern.
     */
    private volatile static Pattern m__BlockPattern;

    /**
     * Parameter pattern.
     */
    private volatile static Pattern m__ParameterPattern;

    /**
     * Retrieves an URLUtils instance.
     * @return such instance.
     */
    @NotNull
    public static URLUtils getInstance()
    {
        @NotNull final URLUtils result =  URLUtilsSingletonContainer.SINGLETON;

        synchronized (URLUtils.class)
        {
            @NotNull final Compiler t_Compiler = retrieveCompiler();

            result.initialize(t_Compiler);
        }
        
        return result;
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected URLUtils() {}

    /**
     * Initializes given URLUtils instance.
     * @param compiler the regexp compiler.
     */
    protected final void initialize(@NotNull final Compiler compiler)
    {
        immutableSetQuestionMarkPattern(
            compiler.compile("(.*?)\\?(.*)"));

        immutableSetBlockPattern(
            compiler.compile("(.*?)&(.*)"));

        immutableSetParameterPattern(
            compiler.compile("(.*?)=(.*)"));
    }

    /**
     * Specifies the question mark pattern.
     * @param pattern the pattern.
     */
    protected static final void immutableSetQuestionMarkPattern(@NotNull final Pattern pattern)
    {
        m__QuestionMarkPattern = pattern;
    }

    /**
     * Specifies the question mark pattern.
     * @param pattern the pattern.
     */
    @SuppressWarnings("unused")
    protected static void setQuestionMarkPattern(@NotNull final Pattern pattern)
    {
        immutableSetQuestionMarkPattern(pattern);
    }

    /**
     * Retrieves the question mark pattern.
     * @return such pattern.
     */
    @NotNull
    public static Pattern getQuestionMarkPattern()
    {
        return m__QuestionMarkPattern;
    }

    /**
     * Specifies the block pattern.
     * @param pattern the pattern.
     */
    protected static final void immutableSetBlockPattern(@NotNull final Pattern pattern)
    {
        m__BlockPattern = pattern;
    }

    /**
     * Specifies the block pattern.
     * @param pattern the pattern.
     */
    @SuppressWarnings("unused")
    protected static void setBlockPattern(@NotNull final Pattern pattern)
    {
        immutableSetBlockPattern(pattern);
    }

    /**
     * Retrieves the block pattern.
     * @return such pattern.
     */
    @NotNull
    public static Pattern getBlockPattern()
    {
        return m__BlockPattern;
    }

    /**
     * Specifies the parameter pattern.
     * @param pattern the pattern.
     */
    protected static final void immutableSetParameterPattern(@NotNull final Pattern pattern)
    {
        m__ParameterPattern = pattern;
    }

    /**
     * Specifies the parameter pattern.
     * @param pattern the pattern.
     */
    @SuppressWarnings("unused")
    protected static void setParameterPattern(@NotNull final Pattern pattern)
    {
        immutableSetParameterPattern(pattern);
    }

    /**
     * Retrieves the parameter pattern.
     * @return such pattern.
     */
    @NotNull
    public static Pattern getParameterPattern()
    {
        return m__ParameterPattern;
    }

    /**
     * Safely appends extra information to given url.
     * @param url the first part of the url.
     * @param extraInfo the new information to add.
     * @return the updated url.
     */
    @NotNull
    @SuppressWarnings("unused")
    public String append(@NotNull final String url, @NotNull final String extraInfo)
    {
        String result;

        int t_iQuestionPosition = url.indexOf("?");

        result =
            (t_iQuestionPosition == -1)
            ?   "?"
            :   "&";

        t_iQuestionPosition = extraInfo.indexOf("?");

        @NotNull final String t_strExtraInfo =
            (t_iQuestionPosition == -1)
            ?   extraInfo
            :   (t_iQuestionPosition == extraInfo.length() - 1)
                ?    extraInfo.substring(0, t_iQuestionPosition)
                :      extraInfo.substring(0, t_iQuestionPosition)
                     + "&"
                     + extraInfo.substring(t_iQuestionPosition + 1);

        result = url + result + t_strExtraInfo;

        return result;
    }

    /**
     * Puts a parameter in the query string. It simulates the query
     * string as a Map, so this method overrides previous values
     * if the parameter name matches.
     * Note: If the parameter has an array of values, then this
     * method adds a new one.
     * One drawback found is when the url has more than one question
     * mark in a row, like in "www.m-centric.com/search??q=wap".
     * @param query the query String to update.
     * @param name the parameter name.
     * @param value the parameter value.
     * @return the updated query.
     * @throws RegexpEngineNotFoundException if the regexp services are
     * not available.
     */
    @NotNull
    public String putParamInQueryString(
        @NotNull final String query,
        @NotNull final String name,
        @NotNull final String value)
      throws  RegexpEngineNotFoundException
    {
        return
            putParamInQueryString(
                query,
                name,
                value,
                isMultiple(query, name),
                valuePresent(query, name, value),
                createMatcher(RegexpManager.getInstance()),
                getQuestionMarkPattern(),
                getBlockPattern());
    }

    /**
     * Puts a parameter in the query string. It simulates the query
     * string as a Map, so this method overrides previous values
     * if the parameter name matches.
     * Note: If the parameter has an array of values, then this
     * method adds a new one.
     * One drawback found is when the url has more than one question
     * mark in a row, like in "www.m-centric.com/search??q=wap".
     * @param query the query String to update.
     * @param name the parameter name.
     * @param value the parameter value.
     * @param isMultiple whether given value appears more than once in the
     * query.
     * @param valuePresent whether given value is present in the query.
     * @param matcher the regexp matcher.
     * @param questionMarkPattern the question mark pattern.
     * @param blockPattern the block pattern.
     * @return the updated query.
     */
    @NotNull
    protected String putParamInQueryString(
        @NotNull final String query,
        @NotNull final String name,
        @NotNull final String value,
        final boolean isMultiple,
        final boolean valuePresent,
        @NotNull final Matcher matcher,
        @NotNull final Pattern questionMarkPattern,
        @NotNull final Pattern blockPattern)
    {
        @NotNull final StringBuilder t_sbResult = new StringBuilder();

        if  (matcher.contains(query, questionMarkPattern))
        {
            @Nullable MatchResult t_MatchResult = matcher.getMatch();

            if (t_MatchResult != null)
            {
                @Nullable final String t_strGroup = t_MatchResult.group(1);

                if (t_strGroup != null)
                {
                    t_sbResult.append(t_sbResult);
                }

                t_sbResult.append("?");

                boolean t_bFirstBlock = true;

                if  (t_MatchResult.groups() > 1)
                {
                    @Nullable String t_strLeft = t_MatchResult.group(2);

                    while  (   (t_strLeft != null)
                            && (matcher.contains(t_strLeft, blockPattern)))
                    {
                        t_MatchResult = matcher.getMatch();

                        if (t_MatchResult != null)
                        {
                            @Nullable final String t_strBlock = t_MatchResult.group(1);

                            t_sbResult.append(
                                (isMultiple)
                                ?   t_strBlock + "&"
                                :   (   (t_strBlock == null)
                                     || (t_strBlock.length() == 0))
                                    ?   name + "=" + value
                                    :   (   (t_bFirstBlock)
                                         || (valuePresent))
                                        ?   parseParameter(
                                                t_strBlock,
                                                name,
                                                value,
                                                valuePresent)
                                        :     "&"
                                            + parseParameter(
                                                  t_strBlock,
                                                  name,
                                                  value,
                                                  valuePresent));

                            if (t_bFirstBlock)
                            {
                                t_bFirstBlock = false;
                            }

                            t_strLeft = t_MatchResult.group(2);
                        }
                    }

                    t_sbResult.append(
                        (   (isMultiple)
                         && (!valuePresent))
                        ?   t_strLeft + "&" + name + "=" + value
                        :   (   (t_strLeft == null)
                             || (t_strLeft.length() == 0))
                            ?   name + "=" + value
                            :   (   (t_bFirstBlock)
                                 || (valuePresent))
                                ?   parseParameter(
                                        t_strLeft,
                                        name,
                                        value,
                                        valuePresent)
                                :     "&"
                                    + parseParameter(
                                          t_strLeft,
                                          name,
                                          value,
                                          valuePresent));
                }
                else
                {
                    t_sbResult.append("?");
                    t_sbResult.append(name);
                    t_sbResult.append("=");
                    t_sbResult.append(value);
                }
            }
        }
        else
        {
            t_sbResult.append(
                (valuePresent)
                ?     "?"
                    + parseParameter(
                          query,
                          name,
                          value,
                          valuePresent)
                :     "?"
                    + query
                    + "&"
                    + name
                    + "="
                    + value);
        }

        return t_sbResult.toString();
    }

    /**
     * Parses a block analyzing if it should be updated.
     * @param block the text block.
     * @param name the parameter name.
     * @param value the parameter value.
     * @param valuePresent true if the value of the parameter is
     * already present in the query string.
     * @return the updated block.
     * @throws RegexpEngineNotFoundException if the system is not properly
     * configured to provide regexp services.
     */
    @NotNull
    protected String parseParameter(
        @NotNull final String block,
        @NotNull final String name,
        @NotNull final String value,
        final boolean valuePresent)
      throws  RegexpEngineNotFoundException
    {
        return
            parseParameter(
                block,
                name,
                value,
                valuePresent,
                createMatcher(RegexpManager.getInstance()),
                getParameterPattern());
    }

    /**
     * Parses a block analyzing if it should be updated.
     * @param block the text block.
     * @param name the parameter name.
     * @param value the parameter value.
     * @param valuePresent true if the value of the parameter is
     * already present in the query string.
     * @param matcher the regexp matcher.
     * @param parameterPattern the parameter pattern.
     * @return the updated block.
     */
    @NotNull
    protected String parseParameter(
        @NotNull final String block,
        @NotNull final String name,
        @NotNull final String value,
        final boolean valuePresent,
        @NotNull final Matcher matcher,
        @NotNull final Pattern parameterPattern)
    {
        @NotNull final StringBuilder t_sbResult = new StringBuilder();

        if  (matcher.contains(block, parameterPattern))
        {
            @Nullable final MatchResult t_ParamMatchResult = matcher.getMatch();

            if  (t_ParamMatchResult != null)
            {
                @Nullable final String t_strName = t_ParamMatchResult.group(1);

                String t_strValue = "";

                if  (   (name.equals(t_strName))
                     && (!valuePresent))
                {
                    t_strValue = value;
                }
                else if  (t_ParamMatchResult.groups() > 1)
                {
                    t_strValue = t_ParamMatchResult.group(2);
                }

                t_sbResult.append(t_strName);

                if  (   (t_strValue != null)
                     && (t_strValue.length() > 0))
                {
                    t_sbResult.append("=");
                }

                t_sbResult.append(t_strValue);
            }
        }
        else
        {
            t_sbResult.append(block);

            if  (block.equals(name))
            {
                t_sbResult.append("=");
                t_sbResult.append(value);
            }
        }

        return t_sbResult.toString();
    }

    /**
     * Checks whether the parameter appears more than once in
     * the specified query.
     * @param query the query to parse.
     * @param name the parameter name.
     * @return true if the parameter has more than one value.
     */
    public boolean isMultiple(@NotNull final String query, @NotNull final String name)
    {
        boolean result = false;

        try
        {
            @Nullable final Compiler t_Compiler = retrieveCompiler();

            @Nullable final Pattern t_Pattern =
                t_Compiler.compile(
                    "(.*?)(\\?|&)?"
//                        + Perl5Compiler.quotemeta(name)
                  + name
                  + "(=|&)(.*)");

            @NotNull final Matcher t_Matcher =
                createMatcher(RegexpManager.getInstance());

            if  (t_Matcher.contains(query, t_Pattern))
            {
                @Nullable final MatchResult t_Result = t_Matcher.getMatch();

                if (t_Result != null)
                {
                    @Nullable final String t_strGroup = t_Result.group(4);

                    if (t_strGroup != null)
                    {
                        result = t_Matcher.contains(t_strGroup, t_Pattern);
                    }
                }
            }
        }
        catch (final MalformedPatternException malformedPatternException)
        {
            /*
             * Possibly the name contains invalid characters, that
             * conflicts with Perl5 regexp syntax.
             */
            LogFactory.getLog(URLUtils.class).error(
                "Invalid pattern (possibly quote symbol conflict)",
                malformedPatternException);

        }
        catch  (final RegexpEngineNotFoundException regexpEngineNotFoundException)
        {
            LogFactory.getLog(URLUtils.class).error(
                Literals.NO_REGEXP_ENGINE_FOUND,
                regexpEngineNotFoundException);
        }

        return result;
    }

    /**
     * Checks if the specified parameter already contains given value
     * in the query string.
     * @param query the query to parse.
     * @param name the parameter name.
     * @param value the parameter value.
     * @return <code>true</code> if the parameter value is already present.
     * @throws RegexpEngineNotFoundException if the system is not configured
     * properly in order to provide regexp services.
     */
    public boolean valuePresent(@NotNull final String query, @NotNull final String name, @NotNull final String value)
        throws  RegexpEngineNotFoundException
    {
        boolean result = false;

        @NotNull final Compiler t_Compiler = retrieveCompiler();

        try
        {
            @NotNull final Pattern t_Pattern =
                t_Compiler.compile(
                      "(.*?)(\\?|&)?"
//                  + Perl5Compiler.quotemeta(name)
                    + name
                    + "="
//                  + Perl5Compiler.quotemeta(value)
                    + value
                    + "(&)?(.*)");

            result =
                valuePresent(
                    query,
                    name,
                    value,
                    t_Pattern,
                    createMatcher(RegexpManager.getInstance()));
        }
        catch  (final MalformedPatternException malformedPatternException)
        {
            LogFactory.getLog(URLUtils.class).error(
                "Invalid pattern (possibly quote symbol conflict)",
                malformedPatternException);
        }

        return result;
    }

    /**
     * Checks if the specified parameter already contains given value
     * in the query string.
     * @param query the query to parse.
     * @param name the parameter name.
     * @param value the parameter value.
     * @param pattern the pattern.
     * @param matcher the regexp matcher.
     * @return <code>true</code> if the parameter value is already present.
     */
    public boolean valuePresent(
        @NotNull final String query,
        @SuppressWarnings("unused") final String name,
        @SuppressWarnings("unused") final String value,
        @NotNull final Pattern pattern,
        @NotNull final Matcher matcher)
    {
        return matcher.contains(query, pattern);
    }

    /**
     * Retrieves the regexp compiler.
     * @return such compiler.
     * @throws RegexpEngineNotFoundException if the system is not configured
     * properly in order to provide regexp services.
     */
    @NotNull
    protected static Compiler retrieveCompiler()
        throws  RegexpEngineNotFoundException
    {
        return RegexpUtils.getRegexpCompiler();
    }

    /**
     * Creates the matcher.
     * @return the regexp matcher.
     * @throws RegexpEngineNotFoundException if a suitable instance
     * cannot be created.
     * @throws RegexpPluginMisconfiguredException if RegexpPlugin is
     * misconfigured.
     */
    @NotNull
    protected static synchronized Matcher createMatcher()
      throws RegexpEngineNotFoundException,
             RegexpPluginMisconfiguredException
    {
        return createMatcher(RegexpManager.getInstance());
    }

    /**
     * Creates the matcher.
     * @param regexpManager the RegexpManager instance.
     * @return the regexp matcher.
     * @throws RegexpEngineNotFoundException if a suitable instance
     * cannot be created.
     * @throws RegexpPluginMisconfiguredException if RegexpPlugin is
     * misconfigured.
     */
    @NotNull
    protected static synchronized Matcher createMatcher(
        @NotNull final RegexpManager regexpManager)
      throws RegexpEngineNotFoundException,
             RegexpPluginMisconfiguredException
    {
        return createMatcher(regexpManager.getEngine());
    }

    /**
     * Creates the matcher.
     * @param regexpEngine the RegexpEngine instance.
     * @return the regexp matcher.
     */
    @NotNull
    protected static synchronized Matcher createMatcher(
        @NotNull final RegexpEngine regexpEngine)
    {
        return regexpEngine.createMatcher();
    }
}
