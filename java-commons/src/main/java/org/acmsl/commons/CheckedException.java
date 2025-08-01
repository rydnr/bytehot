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
    Contact info: jose.sanleandro@acm-sl.org

 ******************************************************************************
 *
 * Filename: CheckedException.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents any exception which requires to be caught.
 *
 */
package org.acmsl.commons;

/*
 * Importing project classes.
 */
import org.acmsl.commons.patterns.Decorator;
import org.acmsl.commons.patterns.I14able;

/*
 * Importing Checker Framework annotations.
 */

/*
 * Importing some JDK classes.
 */
import java.lang.Exception;
import java.util.Locale;

/**
 * Represents any exception which requires to be caught.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public abstract class CheckedException
    extends     Exception
    implements Decorator,
                I14able
{
    /**
     *
     */
    private static final long serialVersionUID = -3846927911305191628L;
    /**
     * The wrapped object.
     */
    private BundleI14able m__BundleI14able;

    /**
     * Creates a CheckedException with given message.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     */
    protected CheckedException(
        final String messageKey,
        final Object[] params)
    {
        super(messageKey);
        immutableSetBundleI14able(
            new _BundleI14able(
                messageKey,
                params,
                retrieveExceptionsBundleProperty(),
                retrieveExceptionsBundleName()));
    }

    /**
     * Creates a CheckedException with given cause.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     * @param cause the error cause.
     */
    protected CheckedException(
        final String messageKey,
        final Object[] params,
        final Throwable cause)
    {
        super(messageKey, cause);
        immutableSetBundleI14able(
            new _BundleI14able(
                messageKey,
                params,
                retrieveExceptionsBundleProperty(),
                retrieveExceptionsBundleName()));
    }

    /**
     * Specifies the wrapped localized throwable.
     * @param bundleI14able the instance to wrap.
     */
     protected void immutableSetBundleI14able(
         final BundleI14able bundleI14able)
     {
         m__BundleI14able = bundleI14able;
     }

    /**
     * Specifies the wrapped localized throwable.
     * @param bundleI14able the instance to wrap.
     */
     protected void setBundleI14able(
         final BundleI14able bundleI14able)
     {
         immutableSetBundleI14able(bundleI14able);
     }

    /**
     * Retrieves the wrapped throwable instance.
     * @return such instance.
     */
    
    protected BundleI14able getBundleI14able()
    {
        return m__BundleI14able;
    }

    /**
     * Retrieves the parameters needed to build the internationalized message.
     * @return such parameters.
     */
    
    public Object[] getParams()
    {
        return getParams(getBundleI14able());
    }

    /**
     * Retrieves the parameters needed to build the internationalized message.
     * @param bundleI14able the localized throwable.
     * @return such parameters.
     */
    
    protected Object[] getParams(final BundleI14able bundleI14able)
    {
        return bundleI14able.getParams();
    }

    /**
     * Retrieves the bundle name.
     * @return such name.
     */
    
    public String getBundleName()
    {
        return getBundleName(getBundleI14able());
    }

    /**
     * Retrieves the bundle name.
     * @param bundleI14able the localized throwable.
     * @return such name.
     */
    
    protected String getBundleName(final BundleI14able bundleI14able)
    {
        return bundleI14able.getBundleName();
    }

    /**
     * Retrieves the internationalized message.
     * @return such message.
     */
    @Override
    
    public String getMessage()
    {
        return getMessage(getBundleI14able());
    }

    /**
     * Retrieves the internationalized message.
     * @param bundleI14able the localized throwable.
     * @return such message.
     */
    
    protected String getMessage(final BundleI14able bundleI14able)
    {
        return bundleI14able.toString();
    }

    /**
     * Retrieves the internationalized message for given locale.
     * @param locale the desired locale.
     * @return such message.
     */
    
    public String getMessage(final Locale locale)
    {
        return getMessage(locale, getBundleI14able());
    }

    /**
     * Retrieves the internationalized message for given locale.
     * @param locale the desired locale.
     * @param bundleI14able the localized throwable.
     * @return such message.
     */
    
    protected String getMessage(
        final Locale locale, final BundleI14able bundleI14able)
    {
        return bundleI14able.toString(locale);
    }

    /**
     * Retrieves the exceptions bundle property.
     * @return such property.
     */
    
    protected String retrieveExceptionsBundleProperty()
    {
        return
            retrieveExceptionsBundleProperty(
                CommonsBundleRepository.getInstance());
    }

    /**
     * Retrieves the exceptions bundle property.
     * @param bundleRepository the bundle repository.
     * @return such property.
     */
    
    protected String retrieveExceptionsBundleProperty(
        final CommonsBundleRepository bundleRepository)
    {
        return bundleRepository.getExceptionsBundleProperty();
    }

    /**
     * Retrieves the exceptions bundle.
     * @return such bundle name.
     */
    
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
    
    protected String retrieveExceptionsBundleName(
        final CommonsBundleRepository bundleRepository)
    {
        return bundleRepository.getExceptionsBundleName();
    }

    /**
     * Retrieves the text defined for the exception.
     * @param locale the locale.
     * @return such text, using given locale.
     */
    
    @Override
    public String toString(final Locale locale)
    {
        return getMessage(locale);
    }

    @Override
    
    public String toString()
    {
        return m__BundleI14able.toString();
    }

    /**
     * BundleI14able suited for CheckedException class.
     * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
     */
    protected static class _BundleI14able
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
            final String messageKey,
            final Object[] params,
            final String systemProperty,
            final String bundleName)
        {
            super(messageKey, params, systemProperty, bundleName);
        }
    }
}
