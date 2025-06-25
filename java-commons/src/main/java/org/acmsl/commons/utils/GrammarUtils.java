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


    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: GrammarUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some grammar rules for language.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.CommonsBundleRepository;
import org.acmsl.commons.BundleI14able;
import org.acmsl.commons.patterns.Utils;

/*
 * Importing JetBrains annotations.
 */

/*
 * Importing some JDK classes.
 */
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * Provides some grammar rules for language.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public abstract class GrammarUtils
    implements  Utils
{
    /**
     * The locale.
     */
    private Locale m__Locale;

    /**
     * Protected constructor to avoid accidental instantiation.
     * @param locale the locale.
     */
    protected GrammarUtils(final Locale locale)
    {
        immutableSetLocale(locale);
    }

    /**
     * Specifies the locale.
     * @param locale the locale.
     */
    private void immutableSetLocale(final Locale locale)
    {
        m__Locale = locale;
    }

    /**
     * Specifies the locale.
     * @param locale the locale.
     */
    @SuppressWarnings("unused")
    protected void setLocale(final Locale locale)
    {
        immutableSetLocale(locale);
    }

    /**
     * Retrieves the locale.
     * @return such instance.
     */
    
    public Locale getLocale()
    {
        return m__Locale;
    }

    /**
     * Retrieves the system property for the words bundle.
     * @return such property.
     */
    
    protected String retrieveGrammarBundleProperty()
    {
        return
            retrieveGrammarBundleProperty(
                CommonsBundleRepository.getInstance());
    }

    /**
     * Retrieves the system property for the words bundle.
     * @param bundleRepository the bundle repository.
     * @return such property.
     */
    
    protected String retrieveGrammarBundleProperty(
        final CommonsBundleRepository bundleRepository)
    {
        return bundleRepository.getGrammarBundleProperty();
    }

    /**
     * Retrieves the words bundle.
     * @return such bundle name.
     */
    
    protected String retrieveGrammarBundleName()
    {
        return
            retrieveGrammarBundleName(
                CommonsBundleRepository.getInstance());
    }

    /**
     * Retrieves the words bundle.
     * @param bundleRepository the bundle repository.
     * @return such bundle name.
     */
    
    protected String retrieveGrammarBundleName(
        final CommonsBundleRepository bundleRepository)
    {
        return bundleRepository.getGrammarBundleName();
    }

    /**
     * Converts given word to singular.
     * @param word the word to convert.
     * @return the converted word.
     */
    
    public String getSingular(final String word)
    {
        final String result;

        final String t_strAux = getSingular(word, retrieveGrammarBundleName());

        if (t_strAux != null)
        {
            result = t_strAux;
        }
        else
        {
            result = word;
        }

        return result;
    }
    
    /**
     * Converts given word to singular.
     * @param word the word to convert.
     * @param bundleName the bundle name.
     * @return the converted word.
     */
    
    public String getSingular(final String word, final String bundleName)
    {
        return
            getWord(
                word,
                bundleName,
                retrieveGrammarBundleProperty(),
                getLocale(),
                ".singular");
    }


    /**
     * Converts given word to plural.
     * @param word the word to convert.
     * @return the converted word.
     */
    
    public String getPlural(final String word)
    {
        final String result;

        final String t_strAux = getPlural(word, retrieveGrammarBundleName());

        if (t_strAux != null)
        {
            result = t_strAux;
        }
        else
        {
            result = word;
        }

        return result;
    }
    
    /**
     * Converts given word to plural.
     * @param word the word to convert.
     * @param bundleName the bundle name.
     * @return the converted word.
     */
    
    public String getPlural(final String word, final String bundleName)
    {
        return
            getWord(
                word,
                bundleName,
                retrieveGrammarBundleProperty(),
                getLocale(),
                ".plural");
    }

    /**
     * Creates a <code>BundleI14able</code> with given information.
     * @param messageKey the message key.
     * @param bundleProperty the bundle property.
     * @param bundleName the bundle name.
     * @return the <code>BundleI14able</code> instance.
     */
    
    protected BundleI14able createBundleI14able(
        final String messageKey,
        final String bundleProperty,
        final String bundleName)
    {
        return
            new _BundleI14able(
                messageKey,
                bundleProperty,
                bundleName);
    }
    
    /**
     * Converts given word to an alternate form.
     * @param word the word to convert.
     * @param bundleName the bundle name.
     * @param bundleProperty the bundle property.
     * @param locale the locale.
     * @param suffix the suffix.
     * @return the converted word.
     */
    
    protected String getWord(
        final String word,
        final String bundleName,
        final String bundleProperty,
        final Locale locale,
        final String suffix)
    {
        String result =
            getWord(
                createBundleI14able(
                    word + suffix,
                    bundleProperty,
                    bundleName),
                locale);

        if  (result == null)
        {
            if  (word.toLowerCase().equals(word))
            {
                result =
                    getWord(
                        createBundleI14able(
                            word.toLowerCase() + suffix,
                            bundleProperty,
                            bundleName),
                        locale);

                if  (result == null)
                {
                    result =
                        getWord(
                            createBundleI14able(
                                word.toUpperCase() + suffix,
                                bundleProperty,
                                bundleName),
                            locale);
                }
            }
            else if  (word.toUpperCase().equals(word))
            {
                result =
                    getWord(
                        createBundleI14able(
                            word.toUpperCase() + suffix,
                            bundleProperty,
                            bundleName),
                        locale);

                if  (result == null)
                {
                    result =
                        getWord(
                            createBundleI14able(
                                word.toLowerCase() + suffix,
                                bundleProperty,
                                bundleName),
                            locale);
                }
            }
        }

        if  (result == null)
        {
            if (".plural".equals(suffix))
            {
                result = getRegularPluralForm(word);
            }
            else
            {
                result = getRegularSingularForm(word);
            }
        }

        return result;
    }
    /**
     * Converts given word to plural.
     * @param bundleI14able the bundleI14able instance.
     * @param locale the locale.
     * @return the converted word.
     */
    
    protected String getWord(
        final BundleI14able bundleI14able, final Locale locale)
    {
        String result = null;

        try
        {
            result = bundleI14able.toString(locale);
        }
        catch  (final MissingResourceException missingResourceException)
        {
            // Nothing to do.
        }

        return result;
    }

    /**
     * Manages the regular plural forms.
     * @param word the word.
     * @return the regular plural form.
     */
    
    protected abstract String getRegularPluralForm(final String word);

    /**
     * Manages the regular singular forms.
     * @param word the word.
     * @return the regular plural form.
     */
    
    protected abstract String getRegularSingularForm(final String word);

    /**
     * BundleI14able suited for <code>GrammarUtils</code> class.
     * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
     * @version $Revision: 1661 $ at $Date: 2007-01-30 15:23:13 +0100 (Tue, 30 Jan 2007) $ by $Author: chous $
     */
    protected static class _BundleI14able
        extends  BundleI14able
    {
        /**
         * Creates a _BundleI14able with given information.
         * @param messageKey the key to build the grammar message.
         * @param propertyName the property name.
         * @param bundleName the name of the bundle.
         */
        protected _BundleI14able(
            final String messageKey,
            final String propertyName,
            final String bundleName)
        {
            super(messageKey, propertyName, bundleName);
        }
    }

    @Override
    public String toString()
    {
        return "GrammarUtils{" +
               " locale=" + m__Locale +
               " }";
    }
}
