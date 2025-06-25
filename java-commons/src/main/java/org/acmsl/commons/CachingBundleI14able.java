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
 * Filename: CachingBundleI14able.java
 *
 * Author: Jose San Leandro Armendariz
 *
 */
package org.acmsl.commons;

/*
 * Importing JetBrains annotations..
 */

/*
 * Importing some JDK classes.
 */
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Represents instances able to display messages in different languages.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public class CachingBundleI14able
    extends  BundleI14able
{
    /**
     * The caching ttl.
     */
    private long m__lCachingTtl;

    /**
     * Whether to reset the cache.
     */
    private boolean m__bResetCache;

    /**
     * The resource bundle cache.
     */
    private static final Map<String, TimestampResourceBundle> RESOURCE_BUNDLE_CACHE =
        new HashMap<String, TimestampResourceBundle>();
    
    /**
     * Creates a <code>CachingBundleI14able</code> with given message.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     * @param systemProperty the name of the system property pointing to the
     * bundle.
     * @param bundleName the name of the bundle.
     * @param useClassLoader whether to use class loaders explicitly.
     * @param ttl the caching TTL.
     */
    @SuppressWarnings("unused")
    public CachingBundleI14able(
        final String messageKey,
        final Object[] params,
        final String systemProperty,
        final String bundleName,
        final boolean useClassLoader,
        final long ttl)
    {
        super(messageKey, params, systemProperty, bundleName, useClassLoader);
        immutableSetCacheTtl(ttl);
    }

    /**
     * Creates a <code>CachingBundleI14able</code> with given message.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     * @param systemProperty the name of the system property pointing to the
     * bundle.
     * @param bundleName the name of the bundle.
     * @param ttl the caching TTL.
     */
    @SuppressWarnings("unused")
    public CachingBundleI14able(
        final String messageKey,
        final Object[] params,
        final String systemProperty,
        final String bundleName,
        final long ttl)
    {
        super(messageKey, params, systemProperty, bundleName);
        immutableSetCacheTtl(ttl);
    }

    /**
     * Creates a <code>BundleI14able</code> with given message bundle.
     * @param messageKey the key to build the exception message.
     * @param systemProperty the name of the bundle.
     * @param bundleName the bundle name.
     * @param ttl the time to live.
     */
    @SuppressWarnings("unused")
    public CachingBundleI14able(
        final String messageKey,
        final String systemProperty,
        final String bundleName,
        final long ttl)
    {
        super(messageKey, systemProperty, bundleName);
        immutableSetCacheTtl(ttl);
    }

    /**
     * Specifies whether to retry the bundle retrieval.
     * @param flag such condition.
     */
    protected final void immutableSetResetCache(final boolean flag)
    {
        m__bResetCache = flag;
    }
     
    /**
     * Specifies whether to reset the cache.
     * @param flag such condition.
     */
    @SuppressWarnings("unused")
    public void setResetCache(final boolean flag)
    {
        immutableSetResetCache(flag);
    }
    
    /**
     * Retrieves whether to reset the cache.
     * @return such condition.
     */
    public boolean getResetCache()
    {
        return m__bResetCache;
    }

    /**
     * Specifies the cache TTL.
     * @param ttl such value.
     */
    protected final void immutableSetCacheTtl(final long ttl)
    {
        m__lCachingTtl = ttl;
    }
    
    /**
     * Specifies the cache TTL.
     * @param ttl such value.
     */
    @SuppressWarnings("unused")
    protected void setCacheTtl(final long ttl)
    {
        immutableSetCacheTtl(ttl);
    }

    /**
     * Retrieves the cache TTL.
     * @return such value.
     */
    public long getCacheTtl()
    {
        return m__lCachingTtl;
    }

    /**
     * Annotates given bundle in the cache.
     * @param resourceBundle the bundle to annotate.
     */
    protected void cacheBundle(final ResourceBundle resourceBundle)
    {
        final String t_strBundleName = getBundleName();

        cacheBundle(resourceBundle, t_strBundleName, RESOURCE_BUNDLE_CACHE);
    }
    
    /**
     * Annotates given bundle in the cache.
     * @param resourceBundle the bundle to annotate.
     * @param bundleName the bundle name.
     * @param map the caching mechanism.
     */
    protected void cacheBundle(
        final ResourceBundle resourceBundle,
        final String bundleName,
        final Map<String, TimestampResourceBundle> map)
    {
        map.put(bundleName, wrapTimestamp(resourceBundle));
    }

    /**
     * Retrieves the cached <code>ResourceBundle</code>.
     * @return such bundle, or <code>null</code> if any.
     */
    
    protected ResourceBundle getCachedBundle()
    {
        final ResourceBundle result;

        final String t_strBundleName = getBundleName();

        result =
            getCachedBundle(
                t_strBundleName, RESOURCE_BUNDLE_CACHE, getResetCache(), getCacheTtl());

        return result;
    }
    
    /**
     * Retrieves the cached <code>ResourceBundle</code>.
     * @param bundleName the bundle name.
     * @param map the caching mechanism.
     * @param resetCache whether the cache should be reset.
     * @param cacheTtl the cache TTL.
     * @return such bundle, or <code>null</code> if any.
     */
    
    protected ResourceBundle getCachedBundle(
        final String bundleName,
        final Map<String, TimestampResourceBundle> map,
        final boolean resetCache,
        final long cacheTtl)
    {
        TimestampResourceBundle result = map.get(bundleName);

        if  (resetCache)
        {
            emptyCache(map);
            result = null;
        }
        else if  (   (result != null)
                  && (hasExpiredFromCache(result, cacheTtl)))
        {
            map.remove(bundleName);
            result = null;
        }
        
        return result;
    }

    /**
     * Empties the cache.
     * @param map the caching mechanism.
     */
    protected void emptyCache(final Map<String, TimestampResourceBundle> map)
    {
        map.clear();
    }

    /**
     * Checks whether the bundle has expired from the cache.
     * @param bundle the bundle.
     * @param cacheTtl the cache TTL.
     * @return <code>true</code> in such case.
     */
    protected boolean hasExpiredFromCache(
        final TimestampResourceBundle bundle, final long cacheTtl)
    {
        boolean result = true;

        final Date timestamp = bundle.getTimestamp();
        
        final Date now = new Date();

        if  (now.getTime() < timestamp.getTime() + cacheTtl)
        {
            result = false;
        }

        return result;
    }
  
    /**
     * Wraps given bundle to attach a timestamp.
     * @param bundle the bundle.
     * @return a <code>TimestampResourceBundle</code> instance.
     */
    
    protected TimestampResourceBundle wrapTimestamp(final ResourceBundle bundle)
    {
        return new TimestampResourceBundle(bundle);
    }

    /**
     * A bundle wrapper to annotate the caching timestamp.
     * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro</a>.
     */
    protected static class TimestampResourceBundle
        extends  ResourceBundle
    {
        /**
         * The wrapped bundle.
         */
        private ResourceBundle m__WrappedBundle;
        
        /**
         * The timestamp.
         */
        private Date m__Timestamp;
        
        /**
         * Creates a <code>TimestampResourceBundle</code> to wrap given instance.
         * @param bundle the bundle to wrap.
         */
        protected TimestampResourceBundle(final ResourceBundle bundle)
        {
            immutableSetWrappedBundle(bundle);
            immutableSetTimestamp(new Date());
        }
        
        /**
         * Specifies the wrapped bundle.
         * @param bundle such bundle.
         */
        protected final void immutableSetWrappedBundle(final ResourceBundle bundle)
        {
            m__WrappedBundle = bundle;
        }
        
        /**
         * Specifies the wrapped bundle.
         * @param bundle such bundle.
         */
        @SuppressWarnings("unused")
        protected void setWrappedBundle(final ResourceBundle bundle)
        {
            immutableSetWrappedBundle(bundle);
        }

        /**
         * Retrieves the wrapped bundle.
         * @return such bundle.
         */
        
        protected ResourceBundle getWrappedBundle()
        {
            return m__WrappedBundle;
        }
        
        /**
         * Specifies the timestamp.
         * @param date such date.
         */
        protected final void immutableSetTimestamp(final Date date)
        {
            m__Timestamp = date;
        }
        
        /**
         * Specifies the timestamp.
         * @param date such date.
         */
        @SuppressWarnings("unused")
        protected void setTimestamp(final Date date)
        {
            immutableSetTimestamp(date);
        }
        
        /**
         * Retrieves the timestamp.
         * @return such information.
         */
        
        public Date getTimestamp()
        {
            return m__Timestamp;
        }

        /**
         * Retrieves the locale.
         * @return such information.
         */
        
        public Locale getLocale()
        {
            return getLocale(getWrappedBundle());
        }
        
        /**
         * Retrieves the locale.
         * @param bundle the bundle.
         * @return such information.
         */
        
        protected Locale getLocale(final ResourceBundle bundle)
        {
            return bundle.getLocale();
        }

        /**
         * Retrieves the bundle keys.
         * @return such keys.
         */
        
        public Enumeration<String> getKeys()
        {
            return getKeys(getWrappedBundle());
        }

        /**
         * Retrieves the bundle keys.
         * @param bundle the bundle.
         * @return such keys.
         */
        
        protected Enumeration<String> getKeys(final ResourceBundle bundle)
        {
            return bundle.getKeys();
        }

        /**
         * Gets an object for the given key from this resource bundle.
         * @param key the key.
         */
        
        protected Object handleGetObject(final String key)
        {
            return handleGetObject(key, getWrappedBundle());
        }
        
        /**
         * Gets an object for the given key from this resource bundle.
         * @param key the key.
         * @param bundle the bundle.
         * @return the object.
         */
        
        protected Object handleGetObject(final String key, final ResourceBundle bundle)
        {
            return bundle.getObject(key);
        }
        
        /**
         * Retrieves the hashcode.
         * @return such value.
         */
        @Override
        public int hashCode()
        {
            return hashCode(getWrappedBundle());
        }

        /**
         * Retrieves the hashcode.
         * @param bundle the bundle.
         * @return such value.
         */
        protected int hashCode(final ResourceBundle bundle)
        {
            return bundle.hashCode();
        }

        /**
         * Checks whether given instance is equal to this one.
         * @param object the object to check.
         * @return <code>true</code> in such case.
         */
        @Override
        public boolean equals(final Object object)
        {
            boolean result = false;
            
            if  (object instanceof ResourceBundle)
            {
                result = object.equals(getWrappedBundle());
            }
            
            return result;
        }

        /**
         * Gives the text representation of the instance.
         *
         * @return such text.
         */
        @Override
        public String toString()
        {
            return "TimestampResourceBundle{" +
                   "timestamp=" + m__Timestamp +
                   ", wrappedBundle=" + m__WrappedBundle +
                   '}';
        }

        /**
         * Gives the text representation of given bundle.
         * @param bundle the {@link ResourceBundle}.
         * @return such text.
         */
        
        protected String toString(final ResourceBundle bundle)
        {
            return bundle.toString();
        }
    }

    /**
     * Retrieves the internationalized message.
     *
     * @return such message.
     */
    @Override
    
    public String toString()
    {
        return "CachingBundleI14able{" +
               "resetCache=" + m__bResetCache +
               ", cachingTtl=" + m__lCachingTtl +
               '}';
    }

    /**
     * Retrieves the bundle from the environment using given information.
     * @param locale the locale.
     * @param systemProperty the system property.
     * @param classLoader the class loader.
     * @return the bundle.
     */
    
    protected ResourceBundle retrieveSystemPropertyBundle(
        final String systemProperty,
        final Locale locale,
        final ClassLoader classLoader)
    {
        ResourceBundle result = getCachedBundle();
        
        if  (result == null)
        {
            result =
                super.retrieveSystemPropertyBundle(
                    systemProperty, locale, classLoader);
        
            if  (result != null)
            {
                cacheBundle(result);
            }
        }
        
        return result;
    }
    
    /**
     * Retrieves the bundle using a concrete class loader.
     * @param bundleName the bundle name.
     * @param locale the locale.
     * @param classLoader the class loader.
     * @return the bundle.
     */
    
    protected ResourceBundle retrieveBundle(
        final String bundleName,
        final Locale locale,
        final ClassLoader classLoader)
    {
        ResourceBundle result = getCachedBundle();
        
        if  (result == null)
        {
            result =
                super.retrieveBundle(
                    bundleName, locale, classLoader);
        
            if  (result != null)
            {
                cacheBundle(result);
            }
        }
        
        return result;
    }
    
}
