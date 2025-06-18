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
 * Filename: RegexpManager.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Manages which regexp engine to use, acting as a facade
 *              hiding all details of building or retrieving implementations.
 *
 */
package org.acmsl.commons.regexpplugin;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.Literals;
import org.acmsl.commons.patterns.Manager;
import org.acmsl.commons.patterns.Singleton;
import org.acmsl.commons.utils.ReflectionUtils;

/*
 * Importing some commons-logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Importing JDK classes.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Manages which regexp engine to use, acting as a facade hiding all
 * details of building or retrieving implementations.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class RegexpManager
    implements  Manager,
                Singleton
{
    /**
     * Configures whether to use class loaders or not.
     */
    private boolean m__bUseClassLoader;

    /**
     * The default engine.
     */
    public static final String DEFAULT_ENGINE =
        "org.acmsl.commons.regexpplugin.jakartaoro.ORORegexpEngine";

    /**
     * The name of the property used to identify the implementation
     * class name.
     */
    public static final String ENGINE_PROPERTY =
        "org.acmsl.commons.regexpplugin.RegexpEngine";

    /**
     * The name of the properties file to search for.
     */
    public static final String CONFIGURATION_SETTINGS =
        "regexpplugin.properties";

    /**
     * JDK1.3+ <a href="http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html#Service%20Provider"
     * >'Service Provider' specification</a>.
     */
    protected static final String SERVICE_ID =
        "META-INF/services/org.acmsl.regexpplugin.RegexpEngine";

    /**
     * The cached engines.
     */
    private static final Map<ClassLoader, RegexpEngine> m__htCachedEngines =
        new Hashtable<ClassLoader, RegexpEngine>();

    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class RegexpManagerSingletonContainer
    {
        /**
         * The actual singleton.
         */
        public static final RegexpManager SINGLETON = new RegexpManager();
    }

    /**
     * Creates a <code>RegexpManager</code> instance to use or not
     * class loaders.
     * @param useClassLoader whether to use it or not.
     */
    protected RegexpManager(final boolean useClassLoader)
    {
        immutableSetUsingClassLoader(useClassLoader);
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected RegexpManager()
    {
        this(true);
    }

    /**
     * Retrieves a <code>RegexpManager</code> instance.
     * @param useClassLoader whether to use class loader or not.
     * @return such instance.
     */
    public static RegexpManager getInstance(final boolean useClassLoader)
    {
        return
            (useClassLoader) ? new RegexpManager(useClassLoader) : getInstance();
    }
    
    /**
     * Retrieves a <code>RegexpManager</code> instance.
     * @return such instance.
     */
    @NotNull
    public static RegexpManager getInstance()
    {
        return RegexpManagerSingletonContainer.SINGLETON;
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
    @SuppressWarnings("unused")
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
     * Retrieves the cached engines.
     * @return such map.
     */
    @Nullable
    protected Map<ClassLoader, RegexpEngine> getCachedEngines()
    {
        return m__htCachedEngines;
    }

    /**
     * Retrieves current engine.
     * Note: The lookup mechanism is adapted from Commons-Logging.
     * @return the engine information.
     */
    @NotNull
    public RegexpEngine getEngine()
    {
        return getEngine(isUsingClassLoader());
    }
    
    /**
     * Retrieves current engine.
     * Note: The lookup mechanism is adapted from Commons-Logging.
     * @param useClassLoader whether to use class loader or not.
     * @return the engine information.
     * @throws RegexpEngineNotFoundException if no engine can be used.
     */
    @NotNull
    protected RegexpEngine getEngine(final boolean useClassLoader)
        throws RegexpEngineNotFoundException
    {
        @NotNull final RegexpEngine result;

        @NotNull ClassLoader t_ClassLoader = getClass().getClassLoader();

        if  (useClassLoader)
        {
            // Identify the class loader we will be using
            @SuppressWarnings("removal")
            ClassLoader tempClassLoader = 
                AccessController.doPrivileged(
                    new PrivilegedAction<ClassLoader>()
                    {
                        public ClassLoader run()
                            {
                                return getContextClassLoader();
                            }
                    });
            t_ClassLoader = tempClassLoader;
        }

        result = getEngineUsingClassLoader(t_ClassLoader);

        return result;
    }

    /**
     * Retrieves current engine.
     * Note: The lookup mechanism is adapted from Commons-Logging.
     * @param classLoader the class loader.
     * @return the engine information.
     * @throws RegexpEngineNotFoundException if no engine can be used.
     */
    @NotNull
    protected RegexpEngine getEngineUsingClassLoader(@NotNull final ClassLoader classLoader)
        throws RegexpEngineNotFoundException
    {
        @Nullable RegexpEngine result;

        // Return any previously registered engine for this class loader
        result = getCachedEngine(classLoader);

        InputStream t_isStream = null;

        Properties t_htProperties = null;

        if  (result == null)
        {

            // First, try the system property
            try
            {
                @NotNull final String engineClass =
                    System.getProperty(ENGINE_PROPERTY);

                if  (engineClass != null)
                {
                    result = createEngine(engineClass, classLoader);
                }
            }
            catch  (final SecurityException securityException)
            {
                LogFactory.getLog(RegexpManager.class).info(
                    Literals.COULD_NOT_LOAD_ENVIRONMENT_PROPERTY + ENGINE_PROPERTY,
                    securityException);
            }
        }

        if  (result == null)
        {
            // Second, try to find a service by using the JDK1.3 jar
            // discovery mechanism. This will allow users to plug a logger
            // by just placing it in the lib/ directory of the webapp ( or in
            // CLASSPATH or equivalent ). This is similar to the second
            // step, except that it uses the (standard?) jdk1.3 location in the jar.

            try
            {
                t_isStream =
                    getResourceAsStream(classLoader, SERVICE_ID);

                if  (t_isStream != null)
                {
                    // This code is needed by EBCDIC and other strange systems.
                    // It's a fix for bugs reported in xerces
                    BufferedReader t_brReader;

                    try
                    {
                        t_brReader =
                            new BufferedReader(
                                new InputStreamReader(
                                    t_isStream, "UTF-8"));
                    }
                    catch  (final UnsupportedEncodingException invalidEncoding)
                    {
                        LogFactory.getLog(RegexpManager.class).info(
                            "System doesn't support UTF-8",
                            invalidEncoding);

                        t_brReader =
                            new BufferedReader(
                                new InputStreamReader(t_isStream, Charset.defaultCharset().name()));
                    }

                    final String engineClassName = t_brReader.readLine();

                    t_brReader.close();

                    if  (   (engineClassName != null)
                         && (!"".equals(engineClassName)))
                    {
                        result =
                            createEngine(engineClassName, classLoader);
                    }
                }
            }
            catch  (final Exception exception)
            {
                LogFactory.getLog(RegexpManager.class).info(
                    "Could not find JDK1.3 service provider for RegexpPlugin",
                    exception);
            }
        }

        if  (result == null)
        {
            // Third try a properties file.
            // If the properties file exists, it'll be read and the properties
            // used. IMHO ( costin ) System property and JDK1.3 jar service
            // should be enough for detecting the class name. The properties
            // should be used to set the attributes ( which may be specific to
            // the webapp, even if a default logger is set at JVM level by a
            // system property )

            try
            {
                t_isStream =
                    getResourceAsStream(
                        classLoader, CONFIGURATION_SETTINGS);
            }
            catch  (final SecurityException securityException)
            {
                LogFactory.getLog(RegexpManager.class).info(
                      "Could not load " + CONFIGURATION_SETTINGS
                    + ". Trying /" + CONFIGURATION_SETTINGS,
                    securityException);
            }

            try
            {
                t_isStream =
                    getResourceAsStream(
                        classLoader, "/" + CONFIGURATION_SETTINGS);
            }
            catch  (final SecurityException securityException)
            {
                LogFactory.getLog(RegexpManager.class).info(
                    "Could not load /" + CONFIGURATION_SETTINGS,
                    securityException);
            }

            if  (t_isStream != null)
            {
                try
                {
                    t_htProperties = new Properties();
                    t_htProperties.load(t_isStream);
                    t_isStream.close();
                }
                catch  (final IOException ioException)
                {
                    LogFactory.getLog(RegexpManager.class).info(
                        "Could not load configuration properties.",
                        ioException);
                }
                catch  (final SecurityException securityException)
                {
                    LogFactory.getLog(RegexpManager.class).info(
                        "Could not load configuration properties.",
                        securityException);
                }
            }
        }

        if  (   (result == null)
             && (t_htProperties != null))
        {
            @Nullable final String engineClass =
                t_htProperties.getProperty(ENGINE_PROPERTY);

            if  (engineClass != null)
            {
                result = createEngine(engineClass, classLoader);
            }
        }

        // Fourth, try the fallback implementation class
        if  (result == null)
        {
            result =
                createEngine(
                    DEFAULT_ENGINE, classLoader); //RegexpEngine.class.getClassLoader());
        }

        if  (result != null)
        {
            /**
             * Always cache using context class loader.
             */
            cacheEngine(classLoader, result);
        }
        else
        {
            throw new RegexpEngineNotFoundException(DEFAULT_ENGINE);
        }

        return result;
    }

    /**
     * Returns the thread context class loader if available.
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * Note: This logic is adapted from Commons-Logging.
     * @return the class loader.
     * @throws RegexpPluginMisconfiguredException if a suitable class loader
     * cannot be identified.
     */
    @NotNull
    protected ClassLoader getContextClassLoader()
        throws RegexpPluginMisconfiguredException
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
     * @throws RegexpPluginMisconfiguredException if a suitable class loader
     * cannot be identified.
     */
    @NotNull
    protected ClassLoader getContextClassLoader(
        @NotNull final ReflectionUtils reflectionUtils)
      throws RegexpPluginMisconfiguredException
    {
        @NotNull final ClassLoader result;

        try
        {
            result = reflectionUtils.getContextClassLoader();
        }
        catch  (final IllegalAccessException illegalAccessException)
        {
            throw
                new RegexpPluginMisconfiguredException(
                    "unexpected.illegalaccessexception",
                    illegalAccessException);
        }
        catch  (final InvocationTargetException invocationTargetException)
        {
            throw
                new RegexpPluginMisconfiguredException(
                    "unexpected.invocationtargetexception",
                    invocationTargetException.getTargetException());
        }

        // Return the selected class loader
        return result;
    }

    /**
     * Retrieves the cached engine associated to given contextClassLoader.
     * @param contextClassLoader the context class loader.
     * @return the engine.
     */
    @Nullable
    protected RegexpEngine getCachedEngine(@NotNull final ClassLoader contextClassLoader)
    {
        return getCachedEngine(contextClassLoader, getCachedEngines());
    }

    /**
     * Check cached engines (keyed by contextClassLoader).
     * @param contextClassLoader the context class loader.
     * @param engines the cached engines.
     * @return the {@link RegexpEngine}.
     */
    @Nullable
    protected RegexpEngine getCachedEngine(
        @NotNull final ClassLoader contextClassLoader,
        @NotNull final Map<ClassLoader, RegexpEngine> engines)
    {
        return engines.get(contextClassLoader);
    }

    /**
     * Annotates a new engine to the cache.
     * @param classLoader the key.
     * @param engine the engine.
     */
    protected void cacheEngine(
        @NotNull final ClassLoader classLoader, @NotNull final RegexpEngine engine)
    {
        cacheEngine(classLoader, engine, getCachedEngines());
    }

    /**
     * Annotates a new engine to the cache.
     * @param classLoader the key.
     * @param engine the engine.
     * @param engines the cached engines.
     */
    protected void cacheEngine(
        @NotNull final ClassLoader classLoader,
        @NotNull final RegexpEngine engine,
        @NotNull final Map<ClassLoader, RegexpEngine> engines)
    {
        engines.put(classLoader, engine);
    }

    /**
     * Return a new instance of the specified <code>RegexpEngine</code>
     * implementation class, loaded by the specified class loader.
     * If that fails, try the class loader used to load the
     * RegexpEngine.
     * @param engineClass Fully qualified name of the <code>RegexpEngine</code>
     * implementation class.
     * @throws RegexpEngineNotFoundException if a suitable instance
     * cannot be created.
     * @throws RegexpPluginMisconfiguredException if RegexpPlugin is
     * misconfigured.
     * @return the {@link RegexpEngine}.
     */
    @SuppressWarnings("unused")
    public RegexpEngine createEngine(@NotNull final String engineClass)
      throws RegexpEngineNotFoundException,
             RegexpPluginMisconfiguredException
    {
        return createEngine(engineClass, getContextClassLoader());
    }

    /**
     * Return a new instance of the specified <code>RegexpEngine</code>
     * implementation class, loaded by the specified class loader.
     * If that fails, try the class loader used to load the
     * RegexpEngine.
     * @param engineClass Fully qualified name of the <code>RegexpEngine</code>
     * implementation class.
     * @param classLoader ClassLoader from which to load this class.
     * @throws RegexpEngineNotFoundException if a suitable instance
     * cannot be created.
     * @throws RegexpPluginMisconfiguredException if RegexpPlugin is
     * misconfigured.
     * @return the {@link RegexpEngine}.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected RegexpEngine createEngine(
        final String engineClass, final ClassLoader classLoader)
      throws RegexpEngineNotFoundException,
             RegexpPluginMisconfiguredException
    {
        @SuppressWarnings("removal")
        @Nullable final RegexpEngine result =
            AccessController.doPrivileged(
                new PrivilegedAction<RegexpEngine>()
                {
                    public RegexpEngine run()
                    {
                        RegexpEngine innerResult = null;

                        final RegexpPluginMisconfiguredException exception;

                        // This will be used to diagnose bad configurations
                        // and allow a useful message to be sent to the user
                        Class<RegexpEngine> t_RegexpEngineClass = null;

                        try
                        {
                            if  (classLoader != null)
                            {
                                try
                                {
                                    // First the given class loader param
                                    // (thread class loader)
                                    // Warning: must typecast here & allow
                                    // exception to be generated/caught &
                                    // recast properly.
                                    t_RegexpEngineClass =
                                        (Class<RegexpEngine>) classLoader.loadClass(engineClass);

                                    innerResult = t_RegexpEngineClass.getDeclaredConstructor().newInstance();

                                }
                                catch  (final ClassNotFoundException classNotFoundException)
                                {
                                    if  (   classLoader
                                         == RegexpEngine.class.getClassLoader())
                                    {
                                        // Nothing more to try, onwards.
                                        throw classNotFoundException;
                                    }
                                    // ignore exception, continue
                                }
                                catch  (final NoClassDefFoundError noClassDefFoundException)
                                {
                                    if  (   classLoader
                                         == RegexpEngine.class.getClassLoader())
                                    {
                                        // Nothing more to try, onwards.
                                        throw noClassDefFoundException;
                                    }
                                    // ignore exception, continue
                                }
                                catch  (final ClassCastException classCastException)
                                {
                                    if  (   classLoader
                                         == RegexpEngine.class.getClassLoader())
                                    {
                                        // Nothing more to try, onwards (bug in
                                        // loader implementation).
                                        throw classCastException;
                                    }
                                    // Ignore exception, continue
                                }
                            }

                            if (innerResult == null)
                            {
                                /* At this point, either classLoader == null, OR
                                 * classLoader was unable to load engineClass.
                                 * Try the class loader that loaded this class:
                                 * RegexpEngine.getClassLoader().
                                 *
                                 * Notes:
                                 * a) RegexpEngine.class.getClassLoader() may return
                                 *    'null' if RegexpEngine is loaded by the bootstrap
                                 *    classloader.
                                 * b) The Java endorsed library mechanism is instead
                                 *    Class.forName(engineClass);
                                 */

                                // Warning: must typecast here & allow exception
                                // to be generated/caught & recast properly.
                                t_RegexpEngineClass = (Class<RegexpEngine>) Class.forName(engineClass);

                                innerResult = t_RegexpEngineClass.getDeclaredConstructor().newInstance();
                            }
                        }
                        catch  (final Exception otherException)
                        {
                            // Check to see if we've got a bad configuration
                            if  (   (t_RegexpEngineClass != null)
                                 && (!RegexpEngine.class.isAssignableFrom(
                                         t_RegexpEngineClass)))
                            {
                                exception =
                                    new RegexpPluginMisconfiguredException(
                                          "implementation.does.not."
                                        + "implement.regexpegine",
                                        otherException);
                            }
                            else
                            {
                                exception =
                                    new RegexpPluginMisconfiguredException(
                                        "unexpected.problem", otherException);
                            }

                            throw exception;
                        }

                        return innerResult;
                    }
                });

        if  (result == null)
        {
            throw new RegexpEngineNotFoundException(engineClass);
        }

        return result;
    }

    /**
     * Retrieves the stream associated to the resource
     * whose name is given, using a concrete class loader.
     * @param loader the class loader.
     * @param name the resource name.
     * @return the stream.
     */
    @Nullable
    protected InputStream getResourceAsStream(
        @Nullable final ClassLoader loader, @NotNull final String name)
    {
        @SuppressWarnings("removal")
        InputStream result =
            AccessController.doPrivileged(
                new PrivilegedAction<InputStream>()
                {
                    public InputStream run()
                    {
                        final InputStream result;

                        if  (loader != null)
                        {
                            result = loader.getResourceAsStream(name);
                        }
                        else
                        {
                            result =
                                ClassLoader.getSystemResourceAsStream(name);
                        }

                        return result;
                    }
                });
        return result;
    }

    @Override
    public String toString()
    {
        return "RegexpManager{" +
               "useClassLoader=" + m__bUseClassLoader +
               '}';
    }
}
