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

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: BundleI14able.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents Throwable instances with support for messages
 *              in different languages.
 *
 */
package org.acmsl.commons;

/*
 * Importing project classes.
 */
import org.acmsl.commons.patterns.I14able;
import org.acmsl.commons.utils.ReflectionUtils;
import org.acmsl.commons.utils.StringValidator;

/*
 * Importing some JDK classes.
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.Throwable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/*
 * Importing Commons Logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents instances able to display messages in different languages.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class BundleI14able
    implements  I14able
{
    /**
     * An empty object array.
     */
    protected static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /**
     * The message key.
     */
    @NotNull private String m__strMessageKey;

    /**
     * The message params.
     */
    @NotNull private Object[] m__aParams;

    /**
     * The System property.
     */
    @NotNull private String m__strSystemProperty;

    /**
     * The bundle name.
     */
    @NotNull private String m__strBundleName;

    /**
     * Configures whether to use class loaders or not.
     */
    private boolean m__bUseClassLoader;

    /**
     * The first bundle to consider.
     */
    @Nullable private ResourceBundle m__FirstBundle;
    
    /**
     * The second bundle to consider.
     */
    @Nullable private ResourceBundle m__SecondBundle;

    /**
     * Whether to retry bundles.
     */
    private boolean m__bRetryBundles = true;
    
    /**
     * Creates a <code>BundleI14able</code> with given message.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     * @param systemProperty the name of the system property pointing to the
     * bundle.
     * @param bundleName the name of the bundle.
     * @param useClassLoader whether to use class loaders explicitly.
     */
    protected BundleI14able(
        @NotNull final String messageKey,
        @NotNull final Object[] params,
        @NotNull final String systemProperty,
        @NotNull final String bundleName,
        final boolean useClassLoader)
    {
        immutableSetMessageKey(messageKey);
        immutableSetParams(params);
        immutableSetSystemProperty(systemProperty);
        immutableSetBundleName(bundleName);
        immutableSetUsingClassLoader(useClassLoader);
    }

    /**
     * Creates a <code>BundleI14able</code> with given message.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     * @param systemProperty the name of the system property pointing to the
     * bundle.
     * @param bundleName the name of the bundle.
     */
    protected BundleI14able(
        @NotNull final String messageKey,
        @NotNull final Object[] params,
        @NotNull final String systemProperty,
        @NotNull final String bundleName)
    {
        this(messageKey, params, systemProperty, bundleName, false);
    }

    /**
     * Creates a <code>BundleI14able</code> with given message bundle.
     * @param messageKey the key to build the exception message.
     * @param systemProperty the name of the bundle.
     * @param bundleName the bundle name.
     */
    protected BundleI14able(
        @NotNull final String messageKey,
        @NotNull final String systemProperty,
        @NotNull final String bundleName)
    {
        this(messageKey, EMPTY_OBJECT_ARRAY, systemProperty, bundleName);
    }

    /**
     * Specifies the message key.
     * @param key the message key.
     */
    protected final void immutableSetMessageKey(@NotNull final String key)
    {
        m__strMessageKey = key;
    }

    /**
     * Specifies the message key.
     * @param key the message key.
     */
    @SuppressWarnings("unused")
    protected void setMessageKey(@NotNull final String key)
    {
        immutableSetMessageKey(key);
    }

    /**
     * Retrieves the message key.
     * @return such key.
     */
    @NotNull
    public String getMessageKey()
    {
        return m__strMessageKey;
    }

    /**
     * Specifies the parameters to build the internationalized message.
     * @param params the parameters.
     */
     protected final void immutableSetParams(@NotNull final Object[] params)
     {
         m__aParams = params;
     }

    /**
     * Specifies the parameters to build the internationalized message.
     * @param params the parameters.
     */
    @SuppressWarnings("unused")
     protected void setParams(@NotNull final Object[] params)
     {
         immutableSetParams(params);
     }

    /**
     * Retrieves the parameters needed to build the internationalized message.
     * @return such parameters.
     */
    @NotNull
    public Object[] getParams()
    {
        return m__aParams;
    }

    /**
     * Specifies the system property name.
     * @param name the property.
     */
    protected void immutableSetSystemProperty(@NotNull final String name)
    {
        m__strSystemProperty = name;
    }

    /**
     * Specifies the system property name
     * @param name the property.
     */
    @SuppressWarnings("unused")
    protected void setSystemProperty(@NotNull final String name)
    {
        immutableSetSystemProperty(name);
    }

    /**
     * Retrieves the system property name.
     * @return such property.
     */
    @NotNull
    public String getSystemProperty()
    {
        return m__strSystemProperty;
    }

    /**
     * Specifies the bundle name.
     * @param name the bundle name.
     */
    protected final void immutableSetBundleName(@NotNull final String name)
    {
        m__strBundleName = name;
    }

    /**
     * Specifies the bundle name.
     * @param name the bundle name.
     */
    @SuppressWarnings("unused")
    protected void setBundleName(@NotNull final String name)
    {
        immutableSetBundleName(name);
    }

    /**
     * Retrieves the bundle name.
     * @return such name.
     */
    @NotNull
    public String getBundleName()
    {
        return m__strBundleName;
    }

    /**
     * Specifies whether to use class loader or not.
     * @param flag such flag.
     */
    protected final void immutableSetUsingClassLoader(final boolean flag)
    {
        m__bUseClassLoader = flag;
    }
    
    /**
     * Specifies whether to use class loader or not.
     * @param flag such flag.
     */
    public void setUsingClassLoader(final boolean flag)
    {
        immutableSetUsingClassLoader(flag);
    }

    /**
     * Retrieves whether the engine is using a class loader or not.
     * @return such flag.
     */
    public boolean isUsingClassLoader()
    {
        return m__bUseClassLoader;
    }

    /**
     * Specifies the first bundle to consider.
     * @param bundle such bundle.
     */
    protected final void immutableSetFirstBundle(@NotNull final ResourceBundle bundle)
    {
        m__FirstBundle = bundle;
    }
    
    /**
     * Specifies the first bundle to consider.
     * @param bundle such bundle.
     */
    protected void setFirstBundle(@NotNull final ResourceBundle bundle)
    {
        immutableSetFirstBundle(bundle);
    }
    
    /**
     * Retrieves the first bundle to consider.
     * @return such bundle.
     */
    @Nullable
    protected ResourceBundle getFirstBundle()
    {
        return m__FirstBundle;
    }
    
    /**
     * Specifies the second bundle to consider.
     * @param bundle such bundle.
     */
    protected final void immutableSetSecondBundle(@NotNull final ResourceBundle bundle)
    {
        m__SecondBundle = bundle;
    }
    
    /**
     * Specifies the second bundle to consider.
     * @param bundle such bundle.
     */
    protected void setSecondBundle(@NotNull final ResourceBundle bundle)
    {
        immutableSetSecondBundle(bundle);
    }
    
    /**
     * Retrieves the second bundle to consider.
     * @return such bundle.
     */
    @Nullable
    protected ResourceBundle getSecondBundle()
    {
        return m__SecondBundle;
    }

    /**
     * Specifies whether to retry the bundle retrieval.
     * @param flag such condition.
     */
    protected final void immutableSetRetryBundles(final boolean flag)
    {
        m__bRetryBundles = flag;
    }
     
    /**
     * Specifies whether to retry the bundle retrieval.
     * @param flag such condition.
     */
    public void setRetryBundles(final boolean flag)
    {
        immutableSetRetryBundles(flag);
    }
    
    /**
     * Retrieves whether to retry the bundle retrieval.
     * @return such condition.
     */
    public boolean getRetryBundles()
    {
        return m__bRetryBundles;
    }
    
    /**
     * Retrieves the internationalized message.
     * @return such message.
     */
    @Override
    @NotNull
    public String toString()
    {
        return toString(Locale.getDefault());
    }

    /**
     * Retrieves the internationalized message for given locale.
     * @param locale the desired locale.
     * @return such message.
     */
    @NotNull
    public String toString(@NotNull final Locale locale)
    {
        return
            buildMessage(
                getMessageKey(),
                getParams(),
                locale,
                getBundleName(),
                getSystemProperty(),
                isUsingClassLoader(),
                getFirstBundle(),
                getSecondBundle(),
                getRetryBundles());
    }

    /**
     * Builds an internationalized message for given key, parameters
     * and locale.
     * @param key the message key.
     * @param params the message parameters.
     * @param locale the locale.
     * @param bundleName the bundle name.
     * @param systemProperty the name of the bundle.
     * @param useClassLoader whether to use class loader or not.
     * @param firstBundle the first bundle.
     * @param secondBundle the second bundle.
     * @param retryBundles whether to retry bundle retrieval or not.
     * @return the customized message.
     */
    protected String buildMessage(
        @NotNull final String key,
        @NotNull final Object[] params,
        @NotNull final Locale locale,
        @NotNull final String bundleName,
        @NotNull final String systemProperty,
        final boolean useClassLoader,
        @Nullable final ResourceBundle firstBundle,
        @Nullable final ResourceBundle secondBundle,
        final boolean retryBundles)
    {
        ResourceBundle t_FirstBundle = firstBundle;
        ResourceBundle t_SecondBundle = secondBundle;
        
        if  (retryBundles)
        {
            ClassLoader t_ClassLoader = getClass().getClassLoader();

            if  (useClassLoader)
            {
                // Identify the class loader we will be using
                @SuppressWarnings("removal")
                final ClassLoader t_AnotherClassLoader =
                    AccessController.doPrivileged(
                        new PrivilegedAction<ClassLoader>()
                        {
                            public ClassLoader run()
                            {
                                ClassLoader result = null;
                                    
                                try
                                {
                                    result = getContextClassLoader();
                                }
                                catch  (final IllegalAccessException exception)
                                {
                                    // We'll use the Class.getClassLoader();
                                }
                                catch  (final InvocationTargetException exception)
                                {
                                    // We'll use the Class.getClassLoader();
                                }

                                return result;
                            }
                        });

                if  (t_AnotherClassLoader != null)
                {
                    t_ClassLoader = t_AnotherClassLoader;
                }
            }

            if  (t_FirstBundle == null)
            {
                t_FirstBundle = 
                    retrieveSystemPropertyBundle(
                        systemProperty,
                        locale,
                        t_ClassLoader);

                if (t_FirstBundle != null)
                {
                    setFirstBundle(t_FirstBundle);
                }
            }
            
            if  (t_SecondBundle == null)
            {
                t_SecondBundle = retrieveBundle(bundleName, locale, t_ClassLoader);

                if (t_SecondBundle != null)
                {
                    setSecondBundle(t_SecondBundle);
                }
            }
            
            setRetryBundles(false);
        }
        
        return
            buildMessage(
                key,
                params,
                t_FirstBundle,
                t_SecondBundle);
    }

    /**
     * Retrieves the bundle from the environment using given information.
     * @param locale the locale.
     * @param systemProperty the system property.
     * @param classLoader the class loader.
     * @return the bundle.
     */
    @Nullable
    protected ResourceBundle retrieveSystemPropertyBundle(
        @NotNull final String systemProperty,
        @NotNull final Locale locale,
        @NotNull final ClassLoader classLoader)
    {
        ResourceBundle result = null;

        try
        {
            final String t_strBundleName =
                System.getProperty(systemProperty);

            if (t_strBundleName != null)
            {
                result = retrieveBundle(t_strBundleName, locale, classLoader);
            }
        }
        catch  (final SecurityException securityException)
        {
            LogFactory.getLog(BundleI14able.class).info(
                Literals.COULD_NOT_LOAD_ENVIRONMENT_PROPERTY + systemProperty,
                securityException);
        }

        return result;
    }
    
    /**
     * First attempt to retrieve the bundle.
     * @param bundleName the bundle name.
     * @param locale the locale.
     * @param classLoader the class loader.
     * @return the bundle.
     * @throws MissingResourceException if the resource is missing.
     */
    @Nullable
    protected ResourceBundle retrieveBundle(
        @NotNull final String bundleName,
        @NotNull final Locale locale,
        @NotNull final ClassLoader classLoader)
      throws MissingResourceException
    {
        @Nullable ResourceBundle result = null;
        
        MissingResourceException exceptionThrown = null;

        try
        {
            result =
                ResourceBundle.getBundle(bundleName, locale, classLoader);
        }
        catch  (final MissingResourceException firstMissingResourceException)
        {
            exceptionThrown = firstMissingResourceException;

            try
            {
                result = secondTry(bundleName, locale, classLoader);
            }
            catch (final MissingResourceException secondMissingResource)
            {
                try
                {
                    LogFactory.getLog(BundleI14able.class).debug(
                        "Could not retrieve bundle "
                        + bundleName,
                        firstMissingResourceException);
                }
                catch  (final Throwable throwable)
                {
                    // Class-loading problem.
                }
            }
        }

        if (exceptionThrown != null)
        {
            throw exceptionThrown;
        }

        return result;
    }

    /**
     * Second attempt to retrieve the bundle.
     * @param bundleName the bundle name.
     * @param locale the locale.
     * @param classLoader the class loader.
     * @return the bundle.
     * @throws MissingResourceException if the resource is missing.
     */
    @Nullable
    protected final ResourceBundle secondTry(
        @NotNull final String bundleName,
        @NotNull final Locale locale,
        @NotNull final ClassLoader classLoader)
        throws MissingResourceException
    {
        @Nullable ResourceBundle result = null;

        MissingResourceException exceptionToThrow = null;

        try
        {
            result =
                ResourceBundle.getBundle(
                    "/" + bundleName, locale, classLoader);
        }
        catch  (final MissingResourceException missingResourceException)
        {
            try
            {
                result = thirdTry(bundleName, locale);
            }
            catch (final MissingResourceException secondMissingResourceException)
            {
                exceptionToThrow = missingResourceException;
            }
        }

        if (exceptionToThrow != null)
        {
            throw exceptionToThrow;
        }

        return result;
    }

    /**
     * Third attempt to retrieve the bundle.
     * @param bundleName the bundle name.
     * @param locale the locale.
     * @return the bundle.
     * @throws MissingResourceException if the resource is missing.
     */
    @Nullable
    protected final ResourceBundle thirdTry(
        @NotNull final String bundleName,
        @NotNull final Locale locale)
        throws MissingResourceException
    {
        @Nullable ResourceBundle result = null;

        Throwable exceptionThrown = null;

        MissingResourceException exceptionToThrow = null;

        try
        {
            // treating bundle name as the first part of the file.
            result =
                new PropertyResourceBundle(
                    new FileInputStream(
                        bundleName + "_"
                        + locale.getLanguage() + "_"
                        + locale.getCountry() + "_"
                        + locale.getVariant()
                        + Literals.PROPERTIES));
        }
        catch  (final FileNotFoundException firstFileNotFoundException)
        {
            exceptionThrown = firstFileNotFoundException;
        }
        catch  (final IOException firstIOException)
        {
            exceptionThrown = firstIOException;
        }
        finally
        {
            if  (exceptionThrown != null)
            {
                try
                {
                    result = fourthTry(bundleName, locale);
                }
                catch (final MissingResourceException missingResource)
                {
                    exceptionToThrow = missingResource;
                }
            }
        }

        if (exceptionToThrow != null)
        {
            throw exceptionToThrow;
        }

        return result;
    }

    /**
     * Fourth attempt to retrieve the bundle.
     * @param bundleName the bundle name.
     * @param locale the locale.
     * @return the bundle.
     * @throws MissingResourceException if the resource is missing.
     */
    @Nullable
    protected final ResourceBundle fourthTry(
        @NotNull final String bundleName,
        @NotNull final Locale locale)
        throws MissingResourceException
    {
        @Nullable ResourceBundle result = null;

        Throwable exceptionThrown = null;

        MissingResourceException exceptionToThrow = null;

        try
        {
            result =
                new PropertyResourceBundle(
                    new FileInputStream(
                        bundleName + "_"
                        + locale.getLanguage() + "_ "
                        + locale.getCountry()
                        + Literals.PROPERTIES));
        }
        catch  (final FileNotFoundException secondFileNotFoundException)
        {
            exceptionThrown = secondFileNotFoundException;
        }
        catch  (final IOException secondIOException)
        {
            exceptionThrown = secondIOException;
        }
        finally
        {
            if  (exceptionThrown != null)
            {
                try
                {
                    result = fifthTry(bundleName, locale);
                }
                catch (final MissingResourceException missingResource)
                {
                    exceptionToThrow = missingResource;
                }
            }
        }

        if (exceptionToThrow != null)
        {
            throw exceptionToThrow;
        }

        return result;
    }

    /**
     * Fifth attempt to retrieve the bundle.
     * @param bundleName the bundle name.
     * @param locale the locale.
     * @return the bundle.
     * @throws MissingResourceException if the resource is missing.
     */
    @Nullable
    protected final ResourceBundle fifthTry(
        @NotNull final String bundleName,
        @NotNull final Locale locale)
      throws MissingResourceException
    {
        @Nullable ResourceBundle result = null;

        Throwable exceptionThrown = null;

        MissingResourceException exceptionToThrow = null;

        try
        {
            result =
                new PropertyResourceBundle(
                    new FileInputStream(
                        bundleName + "_"
                        + locale.getLanguage()
                        + Literals.PROPERTIES));
        }
        catch  (final FileNotFoundException thirdFileNotFoundException)
        {
            exceptionThrown = thirdFileNotFoundException;
        }
        catch  (final IOException thirdIOException)
        {
            exceptionThrown = thirdIOException;
        }
        finally
        {
            if (exceptionThrown != null)
            {
                try
                {
                    result = sixthTry(bundleName);
                }
                catch (final MissingResourceException missingResource)
                {
                    exceptionToThrow = missingResource;
                }
            }
        }

        if (exceptionToThrow != null)
        {
            throw exceptionToThrow;
        }

        return result;
    }

    /**
     * Sixth attempt to retrieve the bundle.
     * @param bundleName the bundle name.
     * @return the bundle.
     * @throws MissingResourceException if the resource is missing.
     */
    @Nullable
    protected final ResourceBundle sixthTry(@NotNull final String bundleName)
        throws MissingResourceException
    {
        @Nullable ResourceBundle result = null;

        Throwable exceptionThrown = null;

        MissingResourceException exceptionToThrow = null;

        try
        {
            result =
                new PropertyResourceBundle(
                    new FileInputStream(
                        bundleName
                        + Literals.PROPERTIES));
        }
        catch  (final FileNotFoundException thirdFileNotFoundException)
        {
            exceptionThrown = thirdFileNotFoundException;
        }
        catch  (final IOException thirdIOException)
        {
            exceptionThrown = thirdIOException;
        }
        finally
        {
            if  (exceptionThrown != null)
            {
                try
                {
                    result = seventhTry(bundleName);
                }
                catch (final MissingResourceException missingResource)
                {
                    exceptionToThrow = missingResource;
                }
            }
        }

        if (exceptionToThrow != null)
        {
            throw exceptionToThrow;
        }

        return result;
    }

    /**
     * Seventh attempt to retrieve the bundle.
     * @param bundleName the bundle name.
     * @return the bundle.
     * @throws MissingResourceException if the resource is missing.
     */
    @Nullable
    protected final ResourceBundle seventhTry(@NotNull final String bundleName)
        throws MissingResourceException
    {
        @Nullable ResourceBundle result = null;

        Throwable exceptionThrown = null;

        MissingResourceException exceptionToThrow = null;

        try
        {
            // last attempt.
            result =
                new PropertyResourceBundle(
                    new FileInputStream(
                        bundleName));
        }
        catch  (final FileNotFoundException thirdFileNotFoundException)
        {
            exceptionThrown = thirdFileNotFoundException;
        }
        catch  (final IOException thirdIOException)
        {
            exceptionThrown = thirdIOException;
        }
        finally
        {
            if  (exceptionThrown != null)
            {
                exceptionToThrow =
                    new MissingResourceException(
                        exceptionThrown.getLocalizedMessage(), exceptionThrown.getClass().getName(), "(none)");
            }
        }

        if (exceptionToThrow != null)
        {
            throw exceptionToThrow;
        }

        return result;
    }

    /**
     * Builds an internationalized message for given key, parameters
     * and locale.
     * @param key the message key.
     * @param params the message parameters.
     * @param firstBundle the first resource bundle.
     * @param secondBundle the second resource bundle.
     * @return the customized message.
     */
    @Nullable
    protected String buildMessage(
        @NotNull final String key,
        @NotNull final Object[] params,
        @Nullable final ResourceBundle firstBundle,
        @Nullable final ResourceBundle secondBundle)
    {
        @Nullable final String result;

        ResourceBundle t_ActualBundle = firstBundle;

        String t_strValue = null;

        if  (firstBundle != null)
        {
            t_strValue = firstBundle.getString(key);
        }

        if  (   (t_strValue == null)
             && (secondBundle != null))
        {
            t_strValue = secondBundle.getString(key);

            if  (t_strValue != null)
            {
                t_ActualBundle = secondBundle;
            }
        }
        
        @NotNull final Object[] t_aParams;

        if  (t_ActualBundle != null)
        {
            t_aParams =
                translateParams(
                    params,
                    t_ActualBundle,
                    StringValidator.getInstance());
        }
        else
        {
            t_aParams = EMPTY_OBJECT_ARRAY;
        }

        if  (t_strValue != null)
        {
            result = buildMessage(t_strValue, t_aParams);
        }
        else
        {
            result = null;
        }

        return result;
    }

    /**
     * Translates any elements of given array for which translation is
     * defined in the bundle.
     * @param params the parameters.
     * @param bundle the bundle.
     * @param stringValidator the StringValidator instance.
     * @return the translated parameters.
     */
    @NotNull
    protected Object[] translateParams(
        @NotNull final Object[] params,
        @NotNull final ResourceBundle bundle,
        @NotNull final StringValidator stringValidator)
    {
        @NotNull final Collection<Object> t_cResult = new ArrayList<Object>();

        for  (int t_iIndex = 0; t_iIndex < params.length; t_iIndex++)
        {
            @Nullable String t_strTranslation = null;

            try
            {
                t_strTranslation = bundle.getString("" + params[t_iIndex]);
            }
            catch  (final MissingResourceException exception)
            {
                LogFactory.getLog(getClass()).debug(
                    "No parameter translation found for: " + params[t_iIndex]);
            }

            if  (stringValidator.isEmpty(t_strTranslation))
            {
                t_cResult.add(params[t_iIndex]);
            }
            else
            {
                t_cResult.add(t_strTranslation);
            }
        }

        return t_cResult.toArray(params);
    }

    /**
     * Builds an internationalized message for given key, parameters and
     * locale.
     * @param message the message.
     * @param params the message parameters.
     * @return the customized message.
     */
    @NotNull
    protected String buildMessage(@NotNull final String message, @NotNull final Object[] params)
    {
        return new MessageFormat(message).format(params);
    }

    /**
     * Returns the thread context class loader if available.
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * Note: This logic is adapted from Commons-Logging.
     * @return the class loader.
     * @throws IllegalAccessException when trying to access
     * <code>Thread.getContextClassLoader()</code> via reflection.
     * @throws InvocationTargetException when trying to access
     * <code>Thread.getContextClassLoader()</code> via reflection, and
     * the target exception is not a <code>SecurityException</code>..
     */
    @NotNull
    protected ClassLoader getContextClassLoader()
        throws IllegalAccessException,
               InvocationTargetException
    {
        return getContextClassLoader(ReflectionUtils.getInstance());
    }
    
    /**
     * Returns the thread context class loader if available.
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * Note: This logic is adapted from Commons-Logging.
     * @param reflectionUtils the <code>ReflectionUtils</code> instance.
     * @return the class loader.
     * @throws IllegalAccessException when trying to access
     * <code>Thread.getContextClassLoader()</code> via reflection.
     * @throws InvocationTargetException when trying to access
     * <code>Thread.getContextClassLoader()</code> via reflection, and
     * the target exception is not a <code>SecurityException</code>..
     */
    @NotNull
    protected ClassLoader getContextClassLoader(
        @NotNull final ReflectionUtils reflectionUtils)
      throws IllegalAccessException,
             InvocationTargetException
    {
        return reflectionUtils.getContextClassLoader();
    }
}
