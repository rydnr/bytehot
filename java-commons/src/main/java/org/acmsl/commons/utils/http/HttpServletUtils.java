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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com
 
 ******************************************************************************
 *
 * Filename: HttpServletUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some useful methods when dealing with HTTP-related
 *              issues.
 */
package org.acmsl.commons.utils.http;

/*
 * Importing project classes.
 */
import org.acmsl.commons.patterns.Utils;
import org.acmsl.commons.patterns.Singleton;
import org.acmsl.commons.regexpplugin.Matcher;
import org.acmsl.commons.regexpplugin.MatchResult;
import org.acmsl.commons.regexpplugin.Pattern;
import org.acmsl.commons.regexpplugin.RegexpEngine;
import org.acmsl.commons.regexpplugin.RegexpEngineNotFoundException;
import org.acmsl.commons.regexpplugin.RegexpManager;
import org.acmsl.commons.regexpplugin.RegexpPluginMisconfiguredException;
import org.acmsl.commons.utils.regexp.RegexpUtils;

/*
 * Importing some Servlet-related classes.
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

/*
 * Importing some JDK classes.
 */
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.StringTokenizer;

/*
 * Importing some Apache Commons Logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */

/**
 * Provides some useful methods when dealing with HttpServlet-related issues.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class HttpServletUtils
    implements  Utils,
                Singleton
{
    /**
     * The locale parameter.
     */
    public static final String LOCALE_PARAMETER = "lang";

    /**
     * The name-value pair regexp.
     */
    public static final String NAME_VALUE_PAIR_REGEXP = "(.*)?=(.*)";

    /**
     * A cached name-value pair compiled regexp.
     */
    private static Pattern m__NameValuePairRegexp;

    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class HttpServletUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        
        public static final HttpServletUtils SINGLETON = new HttpServletUtils();
    }

    /**
     * Default protected constructor to avoid accidental instantiation.
     */
    protected HttpServletUtils() {}

    /**
     * Retrieves a HttpServletUtils instance.
     * @return such instance.
     */
    
    public static HttpServletUtils getInstance()
    {
        return HttpServletUtilsSingletonContainer.SINGLETON;
    }

    /**
     * Specifies the name-value pair regexp.
     * @param regexp such regexp.
     */
    protected static final void immutableSetNameValuePairRegexp(final Pattern regexp)
    {
        m__NameValuePairRegexp = regexp;
    }

    /**
     * Specifies the name-value pair regexp.
     * @param regexp such regexp.
     */
    protected static void setNameValuePairRegexp(final Pattern regexp)
    {
        immutableSetNameValuePairRegexp(regexp);
    }

    /**
     * Retrieves the name-value pair regexp.
     * @return such regexp.
     */
    
    protected static Pattern getNameValuePairRegexp()
    {
        return m__NameValuePairRegexp;
    }

    /**
     * Internationalizes given URL using a configurable convention.
     * @param url the url to internationalize.
     * @param locale the language.
     * @return the translated url.
     */
    
    public String i14e(final String url, final Locale locale)
    {
        return i14e(url, locale, LOCALE_PARAMETER);
    }

    /**
     * Internationalizes given URL using a configurable convention.
     * @param url the url to internationalize.
     * @param locale the language.
     * @param localeParameter the locale parameter.
     * @return the translated url.
     */
    
    public String i14e(
        final String url,
        final Locale locale,
        final String localeParameter)
    {
        return addParameter(url, localeParameter, locale.getLanguage());
    }

    /**
     * Adds a concrete parameter to given URL.
     * @param url the url to be updated.
     * @param paramName the parameter name.
     * @param paramValue the parameter value.
     * @return the modified url.
     */
    
    public String addParameter(
        final String url,
        final String paramName,
        final String paramValue)
    {
        String result = url;

        try 
        {
            result =
                append(
                    url,
                      URLEncoder.encode(paramName, "UTF-8")
                    + "="
                    + URLEncoder.encode(paramValue, "UTF-8"));
        }
        catch (final UnsupportedEncodingException unsupportedEncodingException)
        {
            LogFactory.getLog(HttpServletUtils.class).fatal(
                "cannot manage query-string parameters",
                unsupportedEncodingException);
        }

        return result;
    }

    /**
     * Safely appends extra information to given url.
     * @param url the first part of the url.
     * @param extraInfo the new information to add.
     * @return the updated url.
     */
    
    public String append(final String url, final String extraInfo)
    {
        String result;

        int questionPosition = url.indexOf("?");

        result =
            (questionPosition == -1)
            ?  "?"
            :  "&";

        questionPosition = extraInfo.indexOf("?");

        final String params =
            (questionPosition == -1)
            ?  extraInfo
            :  (questionPosition == extraInfo.length() - 1)
               ?  extraInfo.substring(0, questionPosition)
               :    extraInfo.substring(0, questionPosition)
                  + "&"
                  + extraInfo.substring(questionPosition + 1);

        result = url + result + params;

        return result;
    }

    /**
     * Retrieves an integer parameter.
     * @param request the request.
     * @param paramName the parameter name
     * @param defaultValue the default value.
     * @return the int value.
     */
    public int getIntParam(
        final ServletRequest request,
        final String paramName,
        final int defaultValue)
    {
        int result = defaultValue;

        final String value = request.getParameter(paramName);

        if  (value != null)
        {
            result = getIntParam(paramName, value, defaultValue);
        }

        return result;
    }

    /**
     * Retrieves an integer parameter.
     * @param paramName the parameter name.
     * @param paramValue the parameter value.
     * @param defaultValue the default value.
     * @return the int value.
     */
    protected int getIntParam(
        final String paramName,
        final String paramValue,
        final int defaultValue)
    {
        int result = defaultValue;

        try
        {
            result = Integer.parseInt(paramValue);
        }
        catch  (final NumberFormatException numberFormatException)
        {
            LogFactory.getLog(HttpServletUtils.class).debug(
                "Cannot retrieve the " + paramName + " value");
        }

        return result;
    }

    /**
     * Retrieves the real context path (even if it's managed by Struts)
     * of the application.
     * @param request the request.
     * @return the real context path.
     */
    
    public String getContextPath(final HttpServletRequest request)
    {
        return
            getContextPath(
                request, removeServletInfo(request.getServletPath()));
    }

    /**
     * Retrieves the real context path (even if it's managed by Struts)
     * of the application.
     * @param request the request.
     * @param subContext the subcontext.
     * @return the real context path.
     */
    
    protected String getContextPath(
        final HttpServletRequest request, final String subContext)
    {
        return request.getContextPath() + subContext;
    }

    /**
     * Retrieves the real context path (even if it's managed by Struts)
     * of the application.
     * @param request the request.
     * @return the real context path.
     */
    
    public String getServletPath(final HttpServletRequest request)
    {
        return removeServletInfo(request.getServletPath());
    }

    /**
     * Removes the trailing servlet info from given servlet path.
     * @param servletPath the servlet path.
     * @return the actual relative path of the servlet, without the
     * information about the servlet itself.
     */
    
    protected String removeServletInfo(final String servletPath)
    {
        final StringBuilder result = new StringBuilder();

        final StringTokenizer tokenizer = new StringTokenizer(servletPath, "/");

        final int totalTokens = tokenizer.countTokens();

        int tokenIndex = 1;

        while  (tokenizer.hasMoreTokens())
        {
            if (tokenIndex < totalTokens)
            {
                result.append("/");
                result.append(tokenizer.nextToken());
                tokenIndex++;
            }
            else
            {
                break;
            }
        }

        return result.toString();
    }

    /**
     * Retrieves an object from the repository.
     * @param key the key.
     * @param request the request.
     * @return the object if it's found.
     */
    
    public Object retrieve(final String key, final HttpServletRequest request)
    {
        return retrieve(key, request, request.getSession(true), null);
    }

    /**
     * Retrieves an object from the repository.
     * @param key the key.
     * @param session the session.
     * @return the object if it's found.
     */
    
    public Object retrieve(final String key, final HttpSession session)
    {
        return retrieve(key, null, session, null);
    }

    /**
     * Retrieves an object from the repository.
     * @param key the key.
     * @param request the request.
     * @param session the session.
     * @return the object if it's found.
     */
    
    public Object retrieve(
        final String key,
        final ServletRequest request,
        final HttpSession session)
    {
        return retrieve(key, request, session, null);
    }

    /**
     * Retrieves an object from the repository.
     * @param key the key.
     * @param request the request.
     * @param session the session.
     * @param context the context.
     * @return the object if it's found.
     */
    
    public Object retrieve(
        final String key,
        final ServletRequest request,
        final HttpSession session,
        final ServletContext context)
    {
        Object result = null;

        // Thanks Sun for had missed an interface for
        // all classes that export setAttribute().
        if  (request != null)
        {
            result = request.getAttribute(key);
        }

        if  (   (result  == null)
             && (session != null))
        {
            result = session.getAttribute(key);
        }

        if  (   (result  == null)
             && (context != null))
        {
            result = context.getAttribute(key);
        }

        return result;
    }

    /**
     * Stores an object in the repository.
     * @param object the object to store.
     * @param key the key.
     * @param request the request.
     * @param session the session.
     * @return <code>true</code> if the object has been stored successfully.
     */
    public boolean store(
        final Object object,
        final String key,
        final ServletRequest request,
        final HttpSession session)
    {
        return
            store(
                object,
                key,
                request,
                session,
                null);
    }
        
    /**
     * Stores an object in the repository.
     * @param object the object to store.
     * @param key the key.
     * @param request the request.
     * @return <code>true</code> if the object has been stored successfully.
     */
    public boolean store(
        final Object object,
        final String key,
        final ServletRequest request)
    {
        return
            store(
                object,
                key,
                request,
                null);
    }
        
    /**
     * Stores an object in the repository.
     * @param object the object to store.
     * @param key the key.
     * @param session the session.
     * @return <code>true</code> if the object has been stored successfully.
     */
    public boolean store(
        final Object object,
        final String key,
        final HttpSession session)
    {
        return
            store(
                object,
                key,
                null,
                session);
    }
        
    /**
     * Stores an object in the repository.
     * @param object the object to store.
     * @param key the key.
     * @param request the request.
     * @param session the session.
     * @param servletContext the context.
     * @return <code>true</code> if the object has been stored successfully.
     */
    public boolean store(
        final Object object,
        final String key,
        final ServletRequest request,
        final HttpSession session,
        final ServletContext servletContext)
    {
        boolean result = false;

        // Thanks Sun for had missed an interface for
        // all classes that export setAttribute().
        if  (request != null) 
        {
            request.setAttribute(key, object);
            result = true;
        }
                            
        if  (session != null) 
        {
            session.setAttribute(key, object);
            result = true;
        }

        if  (servletContext != null)
        {
            servletContext.setAttribute(key, object);
            result = true;
        }

        return result;
    }

    /**
     * Removes an object from the repository.
     * @param key the key.
     * @param session the session.
     * @return <code>true</code> if the object has been removed successfully.
     */
    public boolean remove(final String key, final HttpSession session)
    {
        return remove(key, null, session, null);
    }

    /**
     * Removes an object from the repository.
     * @param key the key.
     * @param request the request.
     * @param session the session.
     * @return <code>true</code> if the object has been removed successfully.
     */
    public boolean remove(
        final String key,
        final ServletRequest request,
        final HttpSession session)
    {
        return remove(key, request, session, null);
    }

    /**
     * Removes an object from the repository.
     * @param key the key.
     * @param request the request.
     * @param session the session.
     * @param context the context.
     * @return <code>true</code> if the object has been removed successfully.
     */
    public boolean remove(
        final String key,
        final ServletRequest request,
        final HttpSession session,
        final ServletContext context)
    {
        boolean result = false;

        // Thanks Sun for had missed an interface for
        // all classes that export setAttribute().
        if  (request != null) 
        {
            request.removeAttribute(key);
            result = true;
        }
                            
        if  (session != null) 
        {
            session.removeAttribute(key);
            result = true;
        }

        if  (context != null) 
        {
            context.removeAttribute(key);
            result = true;
        }

        return result;
    }

    /**
     * Creates the matcher.
     * @return the regexp matcher.
     * @throws RegexpEngineNotFoundException if a suitable instance
     * cannot be created.
     * @throws RegexpPluginMisconfiguredException if RegexpPlugin is
     * misconfigured.
     */
    
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
    
    protected static synchronized Matcher createMatcher(
        final RegexpManager regexpManager)
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
    
    protected static synchronized Matcher createMatcher(
        final RegexpEngine regexpEngine)
    {
        return regexpEngine.createMatcher();
    }

    /**
     * Parses a name=value text, splitting it in two strings.
     * @param unparsed the text to parse.
     * @return a two-element array, first for the left part, second for the
     * right.
     * @throws RegexpEngineNotFoundException if the system is not configured
     * properly in order to provide regexp services.
     */
    
    public String[] parseNameValuePair(final String unparsed)
        throws  RegexpEngineNotFoundException
    {
        return
            parseNameValuePair(
                unparsed, createMatcher(RegexpManager.getInstance()));
    }

    /**
     * Parses a name=value text, splitting it in two strings.
     * @param unparsed the text to parse.
     * @param matcher the regexp matcher.
     * @return a two-element array, first for the left part, second for the
     * right.
     */
    
    public String[] parseNameValuePair(
        final String unparsed, final Matcher matcher)
    {
        Pattern t_Pattern = getNameValuePairRegexp();

        final RegexpUtils regexpUtils = RegexpUtils.getInstance();

        if  (t_Pattern == null)
        {
            t_Pattern = buildNameValuePairRegexp(regexpUtils);

            setNameValuePairRegexp(t_Pattern);
        }

        return parseNameValuePair(unparsed, t_Pattern, matcher);
    }

    /**
     * Builds the regexp for parsing name=value texts.
     * @param regexpUtils the RegexpUtils instance.
     * @return such compiled regexp.
     */
    protected Pattern buildNameValuePairRegexp(
        final RegexpUtils regexpUtils)
    {
        return regexpUtils.buildPattern(NAME_VALUE_PAIR_REGEXP);
    }

    /**
     * Parses a name=value text, splitting it in two strings.
     * @param unparsed the text to parse.
     * @param pattern the regexp pattern.
     * @param matcher the regexp matcher.
     * @return a two-element array, first for the left part, second for the
     * right.
     */
    
    protected String[] parseNameValuePair(
        final String unparsed, final Pattern pattern, final Matcher matcher)
    {
        final String[] result = new String[2];

        if  (matcher.contains(unparsed, pattern))
        {
            final MatchResult t_MatchResult = matcher.getMatch();

            if  (   (t_MatchResult != null)
                 && (t_MatchResult.groups() >= 2))
            {
                result[0] = t_MatchResult.group(1);
                result[1] = t_MatchResult.group(2);
            }
        }
        
        return result;
    }
}
