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
    Contact info: jose.sanleandro@acm-sl.org

 ******************************************************************************
 *
 * Filename: ClassLoaderUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some useful methods when working with ClassLoaders.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing project classes.
 */
import org.acmsl.commons.Literals;
import org.acmsl.commons.patterns.Singleton;
import org.acmsl.commons.patterns.Utils;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Importing some JDK classes.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * Provides some useful methods when working with ClassLoaders.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class ClassLoaderUtils
    implements  Utils,
                Singleton
{
    /**
     * A cached empty class array.
     */
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    /**
     * A cached empty object array.
     */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    
    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class ClassLoaderUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        @NotNull public static final ClassLoaderUtils SINGLETON =
            new ClassLoaderUtils();
    }

    /**
     * Default protected constructor to avoid accidental instantiation.
     */
    protected ClassLoaderUtils() {}

    /**
     * Retrieves a <code>ClassLoaderUtils</code> instance.
     * @return such instance.
     */
    @NotNull
    public static ClassLoaderUtils getInstance()
    {
        return ClassLoaderUtilsSingletonContainer.SINGLETON;
    }

    /**
     * Tries to find the location where given class was loaded.
     * @param classInstance the class to check.
     * @param <T> the type.
     * @return the location.
     */
    @Nullable
    public <T> String findLocation(@NotNull final Class<T> classInstance)
    {
        return findLocation(classInstance, false);
    }

    /**
     * Tries to find the location where given class was loaded.
     * @param classInstance the class to check.
     * @param fullSearch whether to perform a full search or get
     * the first match.
     * @param <T> the type.
     * @return the location.
     */
    @Nullable
    public <T> String findLocation(
        @NotNull final Class<T> classInstance, final boolean fullSearch)
    {
        return
            findLocation(
                classInstance.getName() + Literals.CLASS,
                classInstance.getClassLoader(),
                fullSearch);
    }

    /**
     * Tries to find the location where given class was loaded.
     * @param resource the resource.
     * @param classLoader the class loader.
     * @return the location.
     */
    @SuppressWarnings("unused")
    @Nullable
    public String findLocation(
        @NotNull final String resource, @NotNull final ClassLoader classLoader)
    {
        return findLocation(resource, classLoader, false);
    }

    /**
     * Tries to find the location where given resource was loaded.
     * @param resource the resource to check.
     * @param classLoader the class loader.
     * @param fullSearch whether to perform a full search or get
     * the first match.
     * @return the location.
     */
    @Nullable
    public String findLocation(
        @NotNull final String resource,
        @Nullable final ClassLoader classLoader,
        final boolean fullSearch)
    {
        String result = null;

        ClassLoader loader = classLoader;

        if  (loader == null)
        {
            loader = ClassLoader.getSystemClassLoader();
        }

        if  (loader != null)
        {
            String resourceName = resource;

            if  (resourceName.endsWith(Literals.CLASS))
            {
                resourceName =
                    resource.substring(
                        0, resource.lastIndexOf(Literals.CLASS));
            }

            URL url = loader.getResource(resourceName);

            if  (url == null)
            {
                url = ClassLoader.getSystemResource(resourceName);
            }

            if  (url != null)
            {
                result = url.toString();
            }
            else
            {
                result =
                    findLocation(
                        resource,
                        printClassPath(loader),
                        fullSearch);
            }
        }

        return result;
    }

    /**
     * Finds the location in given classpath.
     * @param resource the resource.
     * @param classPath the classpath.
     * @param fullSearch whether to perform a full search or get
     * the first match.
     * @return such location.
     */
    @Nullable
    protected String findLocation(
        @NotNull final String resource,
        @NotNull final String classPath,
        final boolean fullSearch)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        @NotNull final String actualClassPath = trimBrackets(classPath);

        @NotNull final StringTokenizer t_Tokenizer =
            new StringTokenizer(actualClassPath, ":;,[]", false);
        
        String element;

        boolean nonFirstItem = false;

        @NotNull final String resourceName = resource;

        while  (t_Tokenizer.hasMoreTokens())
        {
            element = t_Tokenizer.nextToken();

            if  (   (element != null)
                 && (pathContainsResource(
                         element, resourceName)))
            {
                if  (fullSearch)
                {
                    if  (nonFirstItem)
                    {
                        result.append(",");
                    }

                    nonFirstItem = true;
                }

                result.append(element);

                if  (!fullSearch)
                {
                    break;
                }
            }
        }

        return result.toString();
    }

    /**
     * Prints the classpath defined by given class loader.
     * @param classLoader the class loader to print.
     * @return such information.
     */
    @NotNull
    public String printClassPath(@NotNull final ClassLoader classLoader)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        result.append(printAntClassPath(classLoader));

        result.append(printURLClassPath(classLoader));

        result.append(printSunClassPath(classLoader));

        @Nullable final ClassLoader parent = classLoader.getParent();

        if  (parent != null)
        {
            result.append(printClassPath(parent));
        }

        return result.toString();
    }

    /**
     * Prints the classpath defined by given class loader,
     * assuming it's a AntClassLoader instance.
     * @param classLoader the class loader to print.
     * @return such information.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected String printAntClassPath(@NotNull final ClassLoader classLoader)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        try
        {
            @Nullable Class<?> classInstance = classLoader.getClass();

            while  (classInstance != null)
            {
                @Nullable Method method = null;

                try
                {
                    method =
                        classInstance.getMethod(
                            Literals.GET_CLASSPATH, EMPTY_CLASS_ARRAY);

                }
                catch  (final NoSuchMethodException firstNoSuchMethodException)
                {
                    try
                    {
                        method =
                            classInstance.getDeclaredMethod(
                                Literals.GET_CLASSPATH, EMPTY_CLASS_ARRAY);
                    }
                    catch  (final NoSuchMethodException otherException)
                    {
                        // Left blank on purpose, since we don't know which
                        // ClassLoader implementation we have.
                    }
                }

                if  (method != null)
                {
                    //method.setAccessible(true);

                    result.append("[");
                    result.append(
                        method.invoke(classLoader, EMPTY_OBJECT_ARRAY));
                    result.append("]");

                    break;
                }

                classInstance = classInstance.getSuperclass();
            }
        }
        catch  (final SecurityException securityException)
        {
            // Left blank on purpose. Nothing to do if we cannot access
            // the classloader using reflection.
        }
        catch  (final InvocationTargetException invocationTargetException)
        {
            // Left blank on purpose, since the getClasspath method
            // returned an error.
        }
        catch  (final IllegalArgumentException illegalArgumentException)
        {
            // Left blank on purpose, since the getClasspath method
            // returned an error.
        }
        catch  (final IllegalAccessException illegalAccessException)
        {
            // Left blank on purpose. Nothing to do if we cannot invoke
            // the method on the classloader using reflection.
        }

        return result.toString();
    }

    /**
     * Prints the classpath defined by given class loader,
     * assuming it's a URLClassLoader instance.
     * @param instance the instance to print.
     * @return such information.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected String printURLClassPath(@NotNull final Object instance)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        try
        {
            @Nullable Class<?> classInstance = instance.getClass();

            while  (classInstance != null)
            {
                @Nullable Method method = null;

                try
                {
                    method =
                        classInstance.getMethod(Literals.GET_UR_LS, EMPTY_CLASS_ARRAY);

                }
                catch  (final NoSuchMethodException firstNoSuchMethodException)
                {
                    try
                    {
                        method =
                            classInstance.getDeclaredMethod(
                                Literals.GET_UR_LS, EMPTY_CLASS_ARRAY);
                    }
                    catch  (final NoSuchMethodException otherException)
                    {
                        // Left blank on purpose, since we don't know which
                        // ClassLoader implementation we have.
                    }
                }

                if  (method != null)
                {
                    //method.setAccessible(true);

                    result.append(
                        printURLs(
                            (URL[])
                                method.invoke(instance, EMPTY_OBJECT_ARRAY)));

                    break;
                }

                classInstance = classInstance.getSuperclass();
            }
        }
        catch  (final SecurityException securityException)
        {
            // Left blank on purpose. Nothing to do if we cannot access
            // the classloader using reflection.
        }
        catch  (final InvocationTargetException invocationTargetException)
        {
            // Left blank on purpose, since the getClasspath method
            // returned an error.
        }
        catch  (final IllegalArgumentException illegalArgumentException)
        {
            // Left blank on purpose, since the getClasspath method
            // returned an error.
        }
        catch  (final IllegalAccessException illegalAccessException)
        {
            // Left blank on purpose. Nothing to do if we cannot invoke
            // the method on the classloader using reflection.
        }

        return result.toString();
    }

    /**
     * Prints given array of URLs.
     * @param urls the URLs to print.
     * @return the formatted text containing the URL locations.
     */
    @NotNull
    protected String printURLs(@NotNull final URL[] urls)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        result.append("[");

        for  (int index = 0; index < urls.length; index++)
        {
            if  (index > 0)
            {
                result.append(",");
            }

            result.append(urls[index]);
        }

        result.append("]");

        return result.toString();
    }

    /**
     * Prints the classpath defined by given class loader,
     * assuming it's a Sun (sun.*) instance.
     * @param classLoader the class loader to print.
     * @return such information.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected String printSunClassPath(@NotNull final ClassLoader classLoader)
    {
        @NotNull final StringBuilder result = new StringBuilder();

        try
        {
            @Nullable Class<?> classInstance = classLoader.getClass();

            while  (classInstance != null)
            {
                @Nullable Method method = null;

                try
                {
                    method =
                        classInstance.getMethod(
                            Literals.GET_BOOTSTRAP_CLASS_PATH, EMPTY_CLASS_ARRAY);
                }
                catch  (final NoSuchMethodException firstNoSuchMethodException)
                {
                    try
                    {
                        method =
                            classInstance.getDeclaredMethod(
                                Literals.GET_BOOTSTRAP_CLASS_PATH, EMPTY_CLASS_ARRAY);
                    }
                    catch  (final NoSuchMethodException otherNoSuchMethodException)
                    {
                        // Left blank on purpose, since we don't know which
                        // ClassLoader implementation we have.
                    }
                }

                if  (method != null)
                {
                    method.setAccessible(true);

                    result.append(
                        printURLClassPath(
                            method.invoke(classLoader, EMPTY_OBJECT_ARRAY)));

                    break;
                }

                classInstance = classInstance.getSuperclass();
            }
        }
        catch  (final SecurityException securityException)
        {
            // Left blank on purpose. Nothing to do if we cannot access
            // the classloader using reflection.
        }
        catch  (final IllegalArgumentException illegalArgumentException)
        {
            // Left blank on purpose, since the getClasspath method
            // returned an error.
        }
        catch  (final InvocationTargetException invocationTargetException)
        {
            // Left blank on purpose, since the getClasspath method
            // returned an error.
        }
        catch  (final IllegalAccessException illegalAccessException)
        {
            // Left blank on purpose. Nothing to do if we cannot invoke
            // the method on the classloader using reflection.
        }

        return result.toString();
    }

    /**
     * Checks whether given path contains a concrete resource.
     * @param path the path.
     * @param resource the name of the class.
     * @return <code>true</code> in such case.
     */
    public boolean pathContainsResource(
        @NotNull final String path, @NotNull final String resource)
    {
        return
            pathContainsResource(
                path, resource, retrieveSuffix(resource));
    }

    /**
     * Retrieves the suffix of given resource.
     * @param resource the resource.
     * @return such suffix.
     */
    @NotNull
    protected String retrieveSuffix(@NotNull final String resource)
    {
        @NotNull final String result;

        final int dotIndex = resource.lastIndexOf(".");

        if  (dotIndex >= 0)
        {
            result = resource.substring(dotIndex + 1);
        }
        else
        {
            result = resource;
        }

        return result;
    }

    /**
     * Checks whether given path contains a concrete resource.
     * @param path the path.
     * @param resource the name of the resource.
     * @param suffix the suffix.
     * @return <code>true</code> in such case.
     */
    public boolean pathContainsResource(
        @NotNull final String path, @NotNull final String resource, @NotNull final String suffix)
    {
        // TODO: Add support for URL resources.
        return pathContainsFileResource(path, resource, suffix);
    }

    /**
     * Checks whether given (File) path contains a concrete resource.
     * @param path the path.
     * @param resource the name of the resource.
     * @param suffix the suffix.
     * @return <code>true</code> in such case.
     */
    protected boolean pathContainsFileResource(
        @NotNull final String path, @NotNull final String resource, @NotNull final String suffix)
    {
        boolean result = false;

        String actualPath = path;

        if  (actualPath.startsWith("file:"))
        {
            actualPath = actualPath.substring(5);
        }

        while  (actualPath.startsWith("//"))
        {
            actualPath = actualPath.substring(1);
        }
            
        File file = new File(actualPath);

        if  (   (file.exists())
             && (file.canRead()))
        {
            if  (file.isDirectory())
            {
                file =
                    new File(
                          removeTrailingSlash(file.getAbsolutePath())
                        + "/" + replace(resource, "\\.", "/")
                        + suffix);

                result =
                    (   (file.exists())
                     && (file.canRead()));
            }
            else
            {
                try
                {
                    @NotNull final InputStream inputStream = new FileInputStream(file);

                    @NotNull final ZipInputStream zipInputStream =
                        new ZipInputStream(inputStream);

                    @NotNull final String entryName =
                        replace(resource, "\\.", "/") + suffix;

                    ZipEntry entry;

                    while  (zipInputStream.available() > 0)
                    {
                        entry = zipInputStream.getNextEntry();

                        if  (   (entry != null)
                             && (entryName.equals(entry.getName())))
                        {
                            result = true;
                            break;
                        }
                    }

                    zipInputStream.close();

                    inputStream.close();
                }
                catch  (final ZipException zipException)
                {
                    // It's not a zip
                }
                catch  (final IOException ioException)
                {
                    // Cannot read it or it's corrupt
                }
                catch  (final SecurityException securityException)
                {
                    // I'm not allowed to access it
                }
                catch  (final IllegalArgumentException illegalArgumentException)
                {
                    // For some other reason.
                }
            }
        }

        return result;
    }

    /**
     * Replaces given values.
     * @param text the text.
     * @param original the original token.
     * @param replacement the replacement.
     * @return the modified text.
     */
    @NotNull
    protected String replace(
        @NotNull final String text, @NotNull final String original, @NotNull final String replacement)
    {
        return text.replaceAll(original, replacement);
    }

    /**
     * Removes surrounding brackets.
     * @param text the text.
     * @return the trimmed text.
     */
    @NotNull
    protected String trimBrackets(@NotNull final String text)
    {
        String result = text;

        int length = result.length();

        if  (length > 0)
        {
            while  (result.startsWith("["))
            {
                result = result.substring(1);
            }

            while  (   (result.endsWith("]"))
                    && (length > 1))
            {
                result = result.substring(0, length - 1);
                length = result.length();
            }
        }

        return result;
    }

    /**
     * Removes any trailing slash.
     * @param text the text.
     * @return the trimmed text.
     */
    @NotNull
    protected String removeTrailingSlash(@NotNull final String text)
    {
        String result = text;

        int length = result.length();

        while  (   (length > 1)
                && (result.endsWith("/")))
        {
            result = result.substring(0, length - 1);
            length = result.length();
        }

        return result;
    }
}
