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

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: NonCheckedException.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents any exception which doesn't require to be caught.
 *
 */
package org.acmsl.commons;

/*
 * Importing project classes.
 */
import org.acmsl.commons.patterns.Decorator;
import org.acmsl.commons.patterns.I14able;

/*
 * Importing JetBrains annotations..
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Importing some JDK classes.
 */
import java.lang.RuntimeException;
import java.util.Locale;

/**
 * Represents any exception which doesn't require to be caught.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public abstract class NonCheckedException
    extends     RuntimeException
    implements  Decorator,
                I14able
{
    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -8970660738598246985L;

    /**
     * The wrapped object.
     */
    @NotNull private BundleI14able m__BundleI14able;

    /**
     * Creates a NonCheckedException with given message.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     */
    protected NonCheckedException(
        @NotNull final String messageKey, @NotNull final Object[] params)
    {
        super(messageKey);

        immutableSetBundleI14able(
            new _BundleI14able(
                messageKey,
                params,
                retrieveExceptionsBundleProperty(),
                retrieveExceptionsBundleName()) {});
    }

    /**
     * Creates a NonCheckedException with given cause.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     * @param cause the error cause.
     */
    protected NonCheckedException(
        @NotNull final String messageKey,
        @NotNull final Object[] params,
        @NotNull final Throwable cause)
    {
        super(messageKey, cause);

        immutableSetBundleI14able(
            new _BundleI14able(
                messageKey,
                params,
                retrieveExceptionsBundleProperty(),
                retrieveExceptionsBundleName()) {});
    }

    /**
     * Specifies the wrapped localized throwable.
     * @param bundleI14able the instance to wrap.
     */
     protected final void immutableSetBundleI14able(
         @NotNull final BundleI14able bundleI14able)
     {
         m__BundleI14able = bundleI14able;
     }

    /**
     * Specifies the wrapped localized throwable.
     * @param bundleI14able the instance to wrap.
     */
    @SuppressWarnings("unused")
     protected void setBundleI14able(
         @NotNull final BundleI14able bundleI14able)
     {
         immutableSetBundleI14able(bundleI14able);
     }

    /**
     * Retrieves the wrapped throwable instance.
     * @return such instance.
     */
    @NotNull
    protected BundleI14able getBundleI14able()
    {
        return m__BundleI14able;
    }

    /**
     * Retrieves the parameters needed to build the internationalized message.
     * @return such parameters.
     */
    @SuppressWarnings("unused")
    @NotNull
    public Object[] getParams()
    {
        return getParams(getBundleI14able());
    }

    /**
     * Retrieves the parameters needed to build the internationalized message.
     * @param bundleI14able the localized throwable.
     * @return such parameters.
     */
    @NotNull
    protected Object[] getParams(@NotNull final BundleI14able bundleI14able)
    {
        return bundleI14able.getParams();
    }

    /**
     * Retrieves the bundle name.
     * @return such name.
     */
    @SuppressWarnings("unused")
    @Nullable
    public String getBundleName()
    {
        return getBundleName(getBundleI14able());
    }

    /**
     * Retrieves the bundle name.
     * @param bundleI14able the localized throwable.
     * @return such name.
     */
    @Nullable
    protected String getBundleName(@NotNull final BundleI14able bundleI14able)
    {
        return bundleI14able.getBundleName();
    }

    /**
     * Retrieves the internationalized message.
     * @return such message.
     */
    @NotNull
    public String getMessage()
    {
        return getMessage(getBundleI14able());
    }

    /**
     * Retrieves the internationalized message.
     * @param bundleI14able the localized throwable.
     * @return such message.
     */
    @NotNull
    protected String getMessage(@NotNull final BundleI14able bundleI14able)
    {
        return bundleI14able.toString();
    }

    /**
     * Retrieves the internationalized message for given locale.
     * @param locale the desired locale.
     * @return such message.
     */
    @NotNull
    public String getMessage(@NotNull final Locale locale)
    {
        return getMessage(locale, getBundleI14able());
    }

    /**
     * Retrieves the internationalized message for given locale.
     * @param locale the desired locale.
     * @param bundleI14able the localized throwable.
     * @return such message.
     */
    @NotNull
    protected String getMessage(
        @NotNull final Locale locale, @NotNull final BundleI14able bundleI14able)
    {
        return bundleI14able.toString(locale);
    }

    /**
     * Retrieves the exceptions system property.
     * @return such bundle name.
     */
    @NotNull
    protected String retrieveExceptionsBundleProperty()
    {
        return
            retrieveExceptionsBundleProperty(
                CommonsBundleRepository.getInstance());
    }

    /**
     * Retrieves the exceptions system property.
     * @param bundleRepository the bundle repository.
     * @return such property.
     */
    @NotNull
    protected String retrieveExceptionsBundleProperty(
        @NotNull final CommonsBundleRepository bundleRepository)
    {
        return bundleRepository.getExceptionsBundleProperty();
    }

    /**
     * Retrieves the exceptions bundle.
     * @return such bundle name.
     */
    @NotNull
    protected String retrieveExceptionsBundleName()
    {
        return
            retrieveExceptionsBundleName(
                CommonsBundleRepository.getInstance());
    }

    /**
     * Retrieves the exceptions bundle.
     * @param bundleRepository the bundle repository.
     * @return such bundle name.
     */
    @NotNull
    protected String retrieveExceptionsBundleName(
        @NotNull final CommonsBundleRepository bundleRepository)
    {
        return bundleRepository.getExceptionsBundleName();
    }

    /**
     * Retrieves the text defined for the exception.
     * @param locale the locale.
     * @return such text, using given locale.
     */
    @NotNull
    @Override
    public String toString(@NotNull final Locale locale)
    {
        return getMessage(locale);
    }

    /**
     * Retrieves the text defined for the exception.
     * @return such text, using the default locale.
     */
    @NotNull
    @Override
    public String toString()
    {
        return m__BundleI14able.toString();
    }

    /**
     * BundleI14able suited for NonCheckedException class.
     * @author <a href="mailto:chous@acm-sl.org"
     * >Jose San Leandro Armendariz</a>
     * @version $Revision: 501 $ $Date: 2005-08-30 08:57:14 +0200 (Tue, 30 Aug 2005) $
     */
    protected class _BundleI14able
        extends  BundleI14able
    {
        /**
         * Creates a _BundleI14able with given information.
         * @param messageKey the key to build the exception message.
         * @param params the parameters to build the exception message.
         * @param systemProperty the system property.
         * @param bundleName the name of the bundle.
         */
        protected _BundleI14able(
            @NotNull final String messageKey,
            @NotNull final Object[] params,
            @NotNull final String systemProperty,
            @NotNull final String bundleName)
        {
            super(messageKey, params, systemProperty, bundleName);
        }
    }
}
